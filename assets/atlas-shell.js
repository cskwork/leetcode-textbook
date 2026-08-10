/*
 * TRACE ATLAS learning shell
 *
 * A dependency-free Docsify plugin that progressively enhances the Markdown
 * textbook. Markdown remains the source of truth: if this file, localStorage,
 * IntersectionObserver, or a walkthrough fails, every lesson still reads and
 * navigates normally.
 */
(function traceAtlasBootstrap(window, document) {
  'use strict';

  var TraceAtlas = window.TraceAtlas = window.TraceAtlas || {};
  var STORAGE_KEY = 'trace-atlas:v1:learner-state';
  var THEME_KEY = 'trace-atlas:v1:theme';
  var STATE_VERSION = 1;
  var STATUSES = ['not-started', 'studying', 'completed'];
  var STATUS_LABELS = {
    'not-started': 'Not started',
    studying: 'Studying',
    completed: 'Completed'
  };
  var STATIONS = [
    { key: 'intuition', label: 'Intuition', match: /^intuition\b/i },
    { key: 'pseudocode', label: 'Pseudocode', match: /^pseudocode\b/i },
    { key: 'java', label: 'Java', match: /^java(?:\s+solution)?\b/i },
    { key: 'complexity', label: 'Complexity', match: /^complexity\b/i },
    { key: 'dry-run', label: 'Dry-run', match: /^dry[\s-]*run\b/i }
  ];
  var WALKTHROUGH_ROUTES = Array.isArray(window.TraceAtlasWalkthroughRoutes)
    ? window.TraceAtlasWalkthroughRoutes
    : [];

  var volatileState = makeDefaultState();
  var routeObserver = null;
  var currentRoute = '/';
  var currentModels = { patterns: [], lessons: [], lessonByRoute: {} };
  var activeDialog = null;
  var dialogOpener = null;
  var inertSiblings = [];
  var themeSwitchFrame = null;

  function makeDefaultState() {
    return {
      version: STATE_VERSION,
      lessons: {},
      lastRoute: null,
      sidebar: {}
    };
  }

  function copyState(value) {
    try {
      return JSON.parse(JSON.stringify(value));
    } catch (error) {
      return makeDefaultState();
    }
  }

  function safeGet(key) {
    try {
      return window.localStorage.getItem(key);
    } catch (error) {
      return null;
    }
  }

  function safeSet(key, value) {
    try {
      window.localStorage.setItem(key, value);
      return true;
    } catch (error) {
      return false;
    }
  }

  function readState() {
    var raw = safeGet(STORAGE_KEY);
    if (!raw) return copyState(volatileState);

    try {
      var parsed = JSON.parse(raw);
      if (!parsed || parsed.version !== STATE_VERSION) {
        return copyState(volatileState);
      }
      parsed.lessons = parsed.lessons && typeof parsed.lessons === 'object' ? parsed.lessons : {};
      parsed.sidebar = parsed.sidebar && typeof parsed.sidebar === 'object' ? parsed.sidebar : {};
      parsed.lastRoute = typeof parsed.lastRoute === 'string' ? parsed.lastRoute : null;
      volatileState = parsed;
      return copyState(parsed);
    } catch (error) {
      return copyState(volatileState);
    }
  }

  function writeState(state) {
    state.version = STATE_VERSION;
    volatileState = copyState(state);
    safeSet(STORAGE_KEY, JSON.stringify(state));
  }

  function updateState(change) {
    var state = readState();
    change(state);
    writeState(state);
    return state;
  }

  function validStatus(value) {
    return STATUSES.indexOf(value) !== -1 ? value : 'not-started';
  }

  function lessonState(state, route) {
    var lesson = state.lessons[route];
    if (!lesson || typeof lesson !== 'object') {
      return { status: 'not-started', checkpoints: {}, station: null };
    }
    return {
      status: validStatus(lesson.status),
      checkpoints: lesson.checkpoints && typeof lesson.checkpoints === 'object'
        ? lesson.checkpoints
        : {},
      station: typeof lesson.station === 'string' ? lesson.station : null
    };
  }

  function setLessonStatus(route, status) {
    status = validStatus(status);
    var state = updateState(function (next) {
      var existing = lessonState(next, route);
      next.lessons[route] = {
        status: status,
        checkpoints: existing.checkpoints,
        station: existing.station,
        updatedAt: new Date().toISOString()
      };
      next.lastRoute = route;
    });
    refreshStatusUI(route, status, state);
  }

  function rememberRoute(route) {
    updateState(function (state) {
      state.lastRoute = route;
    });
  }

  function markCheckpointRevealed(route, checkpointId) {
    updateState(function (state) {
      var existing = lessonState(state, route);
      existing.checkpoints[checkpointId] = new Date().toISOString();
      state.lessons[route] = {
        // Revealing an answer is deliberately independent of Lesson Status.
        status: existing.status,
        checkpoints: existing.checkpoints,
        station: existing.station,
        updatedAt: new Date().toISOString()
      };
      state.lastRoute = route;
    });
  }

  function rememberStation(route, station) {
    if (!station) return;
    var current = lessonState(readState(), route);
    if (current.station === station) return;
    updateState(function (state) {
      var existing = lessonState(state, route);
      state.lessons[route] = {
        status: existing.status,
        checkpoints: existing.checkpoints,
        station: station,
        updatedAt: new Date().toISOString()
      };
      state.lastRoute = route;
    });
  }

  function normalizeRoute(input) {
    var value = String(input || '').trim();
    var hashIndex = value.indexOf('#/');
    if (hashIndex !== -1) value = value.slice(hashIndex + 2);
    value = value.replace(/^#\/?/, '').replace(/^\/?/, '');
    value = value.split('?')[0].split('#')[0];
    try { value = decodeURIComponent(value); } catch (error) {}
    value = value.replace(/\.md$/i, '').replace(/\/README$/i, '');
    value = value.replace(/^\/+|\/+$/g, '');
    return !value || /^README$/i.test(value) ? '/' : value;
  }

  function routeFromLocation() {
    return normalizeRoute(window.location.hash || '/');
  }

  function routeFromLink(link) {
    if (!link) return '/';
    return normalizeRoute(link.getAttribute('href') || link.href || '/');
  }

  function explicitAnchorId() {
    var match = window.location.hash.match(/[?&]id=([^&]+)/);
    if (!match) return '';
    try { return decodeURIComponent(match[1]); }
    catch (error) { return match[1]; }
  }

  function isProblemRoute(route) {
    return /^patterns\/[^/]+\/[^/]+$/.test(route);
  }

  function isPatternRoute(route) {
    return /^patterns\/[^/]+$/.test(route);
  }

  function routePatternSlug(route) {
    var parts = route.split('/');
    return parts[0] === 'patterns' ? parts[1] || '' : '';
  }

  function directChild(parent, tagName) {
    if (!parent) return null;
    var wanted = String(tagName).toUpperCase();
    for (var i = 0; i < parent.children.length; i += 1) {
      if (parent.children[i].tagName === wanted) return parent.children[i];
    }
    return null;
  }

  function createElement(tagName, className, text) {
    var node = document.createElement(tagName);
    if (className) node.className = className;
    if (typeof text === 'string') node.textContent = text;
    return node;
  }

  function button(className, text, action) {
    var node = createElement('button', className, text);
    node.type = 'button';
    if (action) node.setAttribute('data-ta-action', action);
    return node;
  }

  function makeLink(className, text, href) {
    var node = createElement('a', className, text);
    node.href = href;
    return node;
  }

  function titleWithoutCount(value) {
    return String(value || '').replace(/\s+\d+\s+problems?\s*$/i, '').trim();
  }

  function lessonTitleFromLink(link) {
    return String(link.textContent || '').replace(/^\s+|\s+$/g, '');
  }

  function collectSidebarModels() {
    var sidebar = document.querySelector('.sidebar');
    var models = { patterns: [], lessons: [], lessonByRoute: {} };
    if (!sidebar) return models;

    // Docsify's Markdown renderer wraps a list item's bold lead-in in <p>,
    // while other Markdown engines may leave <strong> as the direct child.
    // Accept both real DOM shapes instead of coupling curriculum discovery to
    // one renderer detail.
    var strongNodes = sidebar.querySelectorAll('li > strong, li > p > strong');
    Array.prototype.forEach.call(strongNodes, function (strong) {
      var item = strong.closest('li');
      var rawTitle = strong.textContent.trim();
      if (!/^Pattern\s+\d+\s*:/i.test(rawTitle) || !item) return;

      var list = directChild(item, 'ul');
      if (!list) return;
      var overview = null;
      var lessons = [];

      // Docsify injects the current page's heading outline as a nested list
      // below its sidebar link. Read only the curriculum list's direct links;
      // descendant queries would miscount headings as extra Problem Lessons.
      Array.prototype.forEach.call(list.children, function (listItem) {
        var link = directChild(listItem, 'a');
        if (!link || !link.hasAttribute('href')) return;
        var route = routeFromLink(link);
        if (isPatternRoute(route) && !overview) {
          overview = { route: route, href: link.getAttribute('href') || '#/' + route + '/README' };
        } else if (isProblemRoute(route)) {
          var lesson = {
            route: route,
            href: link.getAttribute('href') || '#/' + route + '/README',
            title: lessonTitleFromLink(link),
            link: link
          };
          lessons.push(lesson);
          models.lessons.push(lesson);
          models.lessonByRoute[route] = lesson;
        }
      });

      var slug = overview ? routePatternSlug(overview.route) : routePatternSlug(lessons[0] ? lessons[0].route : '');
      var pattern = {
        slug: slug,
        title: titleWithoutCount(rawTitle),
        overview: overview,
        lessons: lessons,
        item: item,
        list: list,
        strong: strong
      };
      lessons.forEach(function (lesson) { lesson.pattern = pattern; });
      models.patterns.push(pattern);
    });

    return models;
  }

  function progressForLessons(lessons, state) {
    var completed = 0;
    var studying = 0;
    lessons.forEach(function (lesson) {
      var status = lessonState(state, lesson.route).status;
      if (status === 'completed') completed += 1;
      if (status === 'studying') studying += 1;
    });
    return {
      completed: completed,
      studying: studying,
      total: lessons.length,
      percent: lessons.length ? Math.round((completed / lessons.length) * 100) : 0
    };
  }

  function makeMeter(progress, label) {
    var meter = createElement('div', 'ta-meter');
    meter.setAttribute('role', 'progressbar');
    meter.setAttribute('aria-label', label);
    meter.setAttribute('aria-valuemin', '0');
    meter.setAttribute('aria-valuemax', String(progress.total));
    meter.setAttribute('aria-valuenow', String(progress.completed));
    var fill = createElement('span', 'ta-meter__fill');
    fill.style.width = progress.percent + '%';
    meter.appendChild(fill);
    return meter;
  }

  function enhanceSidebar(models) {
    var state = readState();
    models.patterns.forEach(function (pattern, index) {
      var item = pattern.item;
      var list = pattern.list;
      if (!item || !list) return;
      item.classList.add('ta-pattern-group');

      var id = 'ta-pattern-list-' + (pattern.slug || index + 1);
      list.id = id;
      list.classList.add('ta-pattern-group__list');

      var toggle = directChild(item, 'button');
      if (!toggle || !toggle.classList.contains('ta-pattern-toggle')) {
        toggle = button('ta-pattern-toggle', '', 'toggle-pattern');
        toggle.setAttribute('aria-controls', id);
        toggle.setAttribute('data-pattern', pattern.slug);
        item.insertBefore(toggle, item.firstChild);
      }

      var progress = progressForLessons(pattern.lessons, state);
      toggle.textContent = '';
      var toggleTitle = createElement('span', 'ta-pattern-toggle__title', pattern.title);
      var toggleProgress = createElement(
        'span',
        'ta-pattern-toggle__progress',
        progress.completed + '/' + progress.total
      );
      toggle.appendChild(toggleTitle);
      toggle.appendChild(toggleProgress);
      toggle.setAttribute(
        'aria-label',
        pattern.title + ', ' + progress.completed + ' of ' + progress.total + ' completed'
      );

      var headingRow = pattern.strong.parentElement;
      if (headingRow && headingRow.tagName === 'P') {
        headingRow.hidden = true;
      } else {
        pattern.strong.hidden = true;
        var countBadge = pattern.strong.nextElementSibling;
        if (countBadge && countBadge.tagName === 'CODE') countBadge.hidden = true;
      }

      var hasSavedChoice = Object.prototype.hasOwnProperty.call(state.sidebar, pattern.slug);
      var currentPattern = routePatternSlug(currentRoute) === pattern.slug;
      // Keep the active Pattern discoverable even if it was collapsed on another route.
      var expanded = currentPattern || (hasSavedChoice ? !!state.sidebar[pattern.slug] : index === 0);
      toggle.setAttribute('aria-expanded', String(expanded));
      list.hidden = !expanded;

      pattern.lessons.forEach(function (lesson) {
        var status = lessonState(state, lesson.route).status;
        lesson.link.setAttribute('data-lesson-status', status);
        lesson.link.setAttribute('aria-label', lesson.title + ', ' + STATUS_LABELS[status]);
      });
    });
  }

  function currentLesson(models, route) {
    return models.lessonByRoute[route] || null;
  }

  function createBreadcrumbs(route, models, article) {
    if (!article || (!isProblemRoute(route) && !isPatternRoute(route))) return;
    var old = article.querySelector('.ta-breadcrumbs');
    if (old) old.remove();

    var nav = createElement('nav', 'ta-breadcrumbs');
    nav.setAttribute('aria-label', 'Breadcrumb');
    var list = createElement('ol');
    var homeItem = createElement('li');
    homeItem.appendChild(makeLink('', 'Curriculum', '#/'));
    list.appendChild(homeItem);

    var pattern = null;
    if (isProblemRoute(route)) {
      var lesson = currentLesson(models, route);
      pattern = lesson ? lesson.pattern : null;
    } else {
      models.patterns.some(function (candidate) {
        if (candidate.overview && candidate.overview.route === route) {
          pattern = candidate;
          return true;
        }
        return false;
      });
    }

    if (pattern) {
      var patternItem = createElement('li');
      if (isProblemRoute(route) && pattern.overview) {
        patternItem.appendChild(makeLink('', pattern.title, pattern.overview.href));
      } else {
        patternItem.textContent = pattern.title;
        patternItem.setAttribute('aria-current', 'page');
      }
      list.appendChild(patternItem);
    }

    if (isProblemRoute(route)) {
      var lessonItem = createElement('li', '', currentLesson(models, route)
        ? currentLesson(models, route).title
        : route.split('/').pop());
      lessonItem.setAttribute('aria-current', 'page');
      list.appendChild(lessonItem);
    }

    nav.appendChild(list);
    article.insertBefore(nav, article.firstChild);
  }

  function createStatusGroup(route, status) {
    var group = createElement('div', 'ta-status-group');
    group.setAttribute('role', 'group');
    group.setAttribute('aria-label', 'Lesson status');
    STATUSES.forEach(function (value) {
      var statusButton = button('ta-status-button', STATUS_LABELS[value], 'set-status');
      statusButton.setAttribute('data-status', value);
      statusButton.setAttribute('data-route', route);
      statusButton.setAttribute('aria-pressed', String(value === status));
      group.appendChild(statusButton);
    });
    return group;
  }

  function createLessonToolbar(route, models, article) {
    if (!article || !isProblemRoute(route)) return null;
    var old = article.querySelector('.ta-lesson-toolbar');
    if (old) old.remove();

    var lesson = currentLesson(models, route);
    var state = readState();
    var status = lessonState(state, route).status;
    var toolbar = createElement('section', 'ta-lesson-toolbar');
    toolbar.setAttribute('aria-label', 'Lesson controls');

    var meta = createElement('div', 'ta-lesson-toolbar__meta');
    var eyebrow = createElement('span', 'ta-lesson-toolbar__eyebrow', 'Device-local lesson status');
    var announcement = createElement('span', 'ta-lesson-toolbar__status', STATUS_LABELS[status]);
    announcement.setAttribute('aria-live', 'polite');
    meta.appendChild(eyebrow);
    meta.appendChild(announcement);
    toolbar.appendChild(meta);
    toolbar.appendChild(createStatusGroup(route, status));

    var actions = createElement('div', 'ta-lesson-actions');
    var random = button('ta-random-button', 'Random unseen', 'random-unseen');
    random.setAttribute('aria-label', 'Open a random not-started Problem Lesson');
    if (!models.lessons.some(function (candidate) {
      return lessonState(state, candidate.route).status === 'not-started';
    })) random.disabled = true;
    actions.appendChild(random);
    toolbar.appendChild(actions);

    var breadcrumbs = article.querySelector('.ta-breadcrumbs');
    if (breadcrumbs && breadcrumbs.nextSibling) {
      article.insertBefore(toolbar, breadcrumbs.nextSibling);
    } else {
      article.insertBefore(toolbar, article.firstChild);
    }
    return toolbar;
  }

  function headingAnchor(heading, route) {
    var anchor = heading.querySelector('a[href]');
    if (anchor) return anchor.getAttribute('href');
    return '#/' + route + '?id=' + encodeURIComponent(heading.id);
  }

  function findStations(article) {
    var found = [];
    if (!article) return found;
    var headings = article.querySelectorAll('h2[id]');
    Array.prototype.forEach.call(headings, function (heading) {
      var text = heading.textContent.trim();
      STATIONS.some(function (station) {
        if (!station.match.test(text)) return false;
        found.push({ key: station.key, label: station.label, heading: heading });
        return true;
      });
    });
    return found;
  }

  function createExecutionRail(route, article, toolbar) {
    var stations = findStations(article);
    if (!stations.length || !toolbar) return;
    var old = article.querySelector('.ta-execution-rail');
    if (old) old.remove();

    var rail = createElement('nav', 'ta-execution-rail');
    rail.setAttribute('aria-label', 'Learning stations');
    var savedStation = lessonState(readState(), route).station;
    var savedIndex = stations.findIndex(function (station) { return station.key === savedStation; });
    var anchorId = explicitAnchorId();
    var anchorIndex = stations.findIndex(function (station) {
      return station.heading.id === anchorId || station.key === anchorId;
    });
    // Explicit URL intent outranks resume state. For a non-station heading, do
    // not pull the learner away from that deep link to a previously saved station.
    var restoreIndex = anchorId ? anchorIndex : savedIndex;
    if (anchorIndex >= 0) rememberStation(route, stations[anchorIndex].key);
    stations.forEach(function (station, index) {
      var link = makeLink('ta-station-link', station.label, headingAnchor(station.heading, route));
      link.setAttribute('data-station', station.key);
      link.setAttribute('data-route', route);
      link.setAttribute('data-ta-action', 'station');
      if (index === (restoreIndex >= 0 ? restoreIndex : 0)) link.setAttribute('aria-current', 'step');
      rail.appendChild(link);
    });
    toolbar.insertAdjacentElement('afterend', rail);

    function activateStation(index) {
      Array.prototype.forEach.call(rail.querySelectorAll('.ta-station-link'), function (link, linkIndex) {
        if (linkIndex === index) link.setAttribute('aria-current', 'step');
        else link.removeAttribute('aria-current');
      });
    }

    // Docsify's own auto-to-top work can run just after a plugin's doneEach hook.
    // A second bounded restore keeps the saved station authoritative without a
    // continuous scroll handler, and also works when IntersectionObserver is absent.
    if (restoreIndex >= 0) {
      var restoreStation = function () {
        var headingTop = stations[restoreIndex].heading.getBoundingClientRect().top;
        var scrollPadding = parseFloat(window.getComputedStyle(document.documentElement).scrollPaddingTop) || 0;
        var targetTop = Math.max(0, window.scrollY + headingTop - scrollPadding);
        // Directly assigning scrollTop avoids replaying the site's smooth-scroll
        // animation when restoring a saved location on route entry.
        document.documentElement.scrollTop = targetTop;
        document.body.scrollTop = targetTop;
        activateStation(restoreIndex);
      };
      window.setTimeout(restoreStation, 0);
      window.setTimeout(restoreStation, 420);
    }

    if (!('IntersectionObserver' in window)) return;
    var readyToPersist = false;
    routeObserver = new window.IntersectionObserver(function () {
      if (!readyToPersist && restoreIndex >= 0) return;
      var marker = window.innerHeight * 0.32;
      var active = 0;
      stations.forEach(function (station, index) {
        if (station.heading.getBoundingClientRect().top <= marker) active = index;
      });
      activateStation(active);
      if (readyToPersist) rememberStation(route, stations[active].key);
    }, { rootMargin: '-15% 0px -60% 0px', threshold: [0, 1] });
    stations.forEach(function (station) { routeObserver.observe(station.heading); });

    // Let Docsify/browser anchor scrolling and saved-station restoration settle
    // before scroll observation is allowed to update the persisted station.
    window.setTimeout(function () { readyToPersist = true; }, restoreIndex >= 0 ? 780 : 350);
  }

  function createLessonNavigation(route, models, article) {
    if (!article || !isProblemRoute(route)) return;
    var existing = article.querySelector('.ta-lesson-nav');
    if (existing) existing.remove();
    var index = -1;
    models.lessons.some(function (lesson, candidateIndex) {
      if (lesson.route === route) {
        index = candidateIndex;
        return true;
      }
      return false;
    });
    if (index === -1) return;

    var nav = createElement('nav', 'ta-lesson-nav');
    nav.setAttribute('aria-label', 'Adjacent Problem Lessons');
    var previous = models.lessons[index - 1];
    var next = models.lessons[index + 1];
    if (previous) {
      var previousLink = makeLink(
        'ta-lesson-nav__link ta-lesson-nav__link--prev',
        '\u2190 ' + previous.title,
        previous.href
      );
      previousLink.setAttribute('rel', 'prev');
      nav.appendChild(previousLink);
    } else {
      nav.appendChild(createElement('span', 'ta-lesson-nav__spacer'));
    }
    if (next) {
      var nextLink = makeLink(
        'ta-lesson-nav__link ta-lesson-nav__link--next',
        next.title + ' \u2192',
        next.href
      );
      nextLink.setAttribute('rel', 'next');
      nav.appendChild(nextLink);
    }
    article.appendChild(nav);
  }

  function checkpointId(details, index) {
    var cursor = details.previousElementSibling;
    while (cursor && !/^H[23]$/.test(cursor.tagName)) cursor = cursor.previousElementSibling;
    var prefix = cursor ? cursor.textContent.trim().toLowerCase() : 'checkpoint';
    prefix = prefix.replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '').slice(0, 48) || 'checkpoint';
    return prefix + '-' + (index + 1);
  }

  function enhanceCheckpoints(route, article) {
    if (!isProblemRoute(route) || !article) return;
    var revealed = lessonState(readState(), route).checkpoints;
    var details = article.querySelectorAll('details');
    var checkpointIndex = 0;
    Array.prototype.forEach.call(details, function (detail) {
      var summary = detail.querySelector('summary');
      if (!summary || !/show\s+answer/i.test(summary.textContent)) return;
      var id = checkpointId(detail, checkpointIndex);
      checkpointIndex += 1;
      detail.setAttribute('data-ta-checkpoint', id);
      detail.setAttribute('data-ta-route', route);
      if (revealed[id]) detail.classList.add('ta-checkpoint--revealed');
    });
  }

  function visualizationUrl(route) {
    try {
      return new window.URL(route + '/visualization/index.html', document.baseURI).href;
    } catch (error) {
      return route + '/visualization/index.html';
    }
  }

  function addWalkthroughAction(route, toolbar) {
    if (!toolbar || WALKTHROUGH_ROUTES.indexOf(route) === -1) return;
    var url = visualizationUrl(route);
    var actions = toolbar.querySelector('.ta-lesson-actions');
    if (!actions || actions.querySelector('.ta-walkthrough-button')) return;
    var launch = button('ta-walkthrough-button', 'Open interactive walkthrough', 'open-walkthrough');
    launch.setAttribute('data-src', url);
    launch.setAttribute('data-route', route);
    launch.setAttribute('aria-haspopup', 'dialog');
    actions.insertBefore(launch, actions.firstChild);
  }

  function focusableElements(container) {
    var selector = [
      'a[href]', 'button:not([disabled])', 'input:not([disabled])',
      'select:not([disabled])', 'textarea:not([disabled])',
      'iframe', '[tabindex]:not([tabindex="-1"])'
    ].join(',');
    return Array.prototype.filter.call(container.querySelectorAll(selector), function (node) {
      return !node.hidden && node.getAttribute('aria-hidden') !== 'true';
    });
  }

  function isolateDialog(dialog) {
    inertSiblings = [];
    Array.prototype.forEach.call(document.body.children, function (node) {
      if (node === dialog || node.tagName === 'SCRIPT' || node.tagName === 'NOSCRIPT') return;
      inertSiblings.push({
        node: node,
        hadInert: node.hasAttribute('inert'),
        ariaHidden: node.getAttribute('aria-hidden')
      });
      node.inert = true;
      node.setAttribute('inert', '');
      node.setAttribute('aria-hidden', 'true');
    });
  }

  function restoreDialogBackground() {
    inertSiblings.forEach(function (entry) {
      if (!entry.node || !document.documentElement.contains(entry.node)) return;
      entry.node.inert = entry.hadInert;
      if (!entry.hadInert) entry.node.removeAttribute('inert');
      if (entry.ariaHidden === null) entry.node.removeAttribute('aria-hidden');
      else entry.node.setAttribute('aria-hidden', entry.ariaHidden);
    });
    inertSiblings = [];
  }

  function supportsWebGL() {
    try {
      var canvas = document.createElement('canvas');
      return Boolean(
        window.WebGLRenderingContext
        && (canvas.getContext('webgl2') || canvas.getContext('webgl'))
      );
    } catch (error) {
      return false;
    }
  }

  function openWalkthrough(source, route, opener) {
    closeWalkthrough();
    dialogOpener = opener || document.activeElement;
    var overlay = createElement('div', 'ta-dialog');
    overlay.setAttribute('role', 'dialog');
    overlay.setAttribute('aria-modal', 'true');
    overlay.setAttribute('aria-labelledby', 'ta-dialog-title');

    var backdrop = createElement('div', 'ta-dialog__backdrop');
    backdrop.setAttribute('data-ta-action', 'close-walkthrough');
    backdrop.setAttribute('aria-hidden', 'true');
    var panel = createElement('div', 'ta-dialog__panel');
    var header = createElement('header', 'ta-dialog__header');
    var title = createElement('h2', '', 'Interactive Walkthrough');
    title.id = 'ta-dialog-title';
    var close = button('ta-dialog__close', 'Close', 'close-walkthrough');
    header.appendChild(title);
    header.appendChild(close);

    var body = createElement('div', 'ta-dialog__body');
    if (supportsWebGL()) {
      var frame = createElement('iframe', 'ta-dialog__frame');
      frame.title = 'Interactive walkthrough for ' + ((currentModels.lessonByRoute[route] || {}).title || 'this lesson');
      frame.loading = 'lazy';
      frame.setAttribute('sandbox', 'allow-scripts');
      frame.src = source;
      body.appendChild(frame);
    } else {
      var fallback = createElement('div', 'ta-dialog__fallback');
      fallback.appendChild(createElement('p', 'ta-dialog__fallback-mark', '3D unavailable'));
      fallback.appendChild(createElement(
        'h3',
        '',
        'This browser could not start a WebGL canvas.'
      ));
      fallback.appendChild(createElement(
        'p',
        '',
        'The lesson is still fully available. Enable hardware acceleration or try a WebGL-capable browser to use this optional walkthrough.'
      ));
      body.appendChild(fallback);
    }
    panel.appendChild(header);
    panel.appendChild(body);
    overlay.appendChild(backdrop);
    overlay.appendChild(panel);
    document.body.appendChild(overlay);
    isolateDialog(overlay);
    document.body.classList.add('ta-dialog-open');
    activeDialog = overlay;
    close.focus();
  }

  function closeWalkthrough() {
    if (!activeDialog) return;
    var restore = dialogOpener;
    activeDialog.remove();
    activeDialog = null;
    restoreDialogBackground();
    dialogOpener = null;
    document.body.classList.remove('ta-dialog-open');
    if (restore && document.documentElement.contains(restore)) restore.focus();
  }

  function randomUnseenLesson() {
    var state = readState();
    var unseen = currentModels.lessons.filter(function (lesson) {
      return lessonState(state, lesson.route).status === 'not-started';
    });
    if (!unseen.length) return;
    if (unseen.length > 1) {
      unseen = unseen.filter(function (lesson) { return lesson.route !== currentRoute; });
    }
    var next = unseen[Math.floor(Math.random() * unseen.length)];
    window.location.hash = next.href.indexOf('#') === 0 ? next.href.slice(1) : '/' + next.route + '/README';
  }

  function continueLearning() {
    var state = readState();
    if (!state.lastRoute) {
      window.location.hash = '/00-how-to-use-this-book';
      return;
    }
    var next = state.lastRoute && currentModels.lessonByRoute[state.lastRoute]
      ? currentModels.lessonByRoute[state.lastRoute]
      : currentModels.lessons.filter(function (lesson) {
        return lessonState(state, lesson.route).status !== 'completed';
      })[0];
    if (!next) next = currentModels.lessons[0];
    if (!next) return;
    window.location.hash = next.href.indexOf('#') === 0 ? next.href.slice(1) : '/' + next.route + '/README';
  }

  function renderPatternCard(pattern, state) {
    var progress = progressForLessons(pattern.lessons, state);
    var card = createElement('article', 'ta-pattern-card');
    var head = createElement('div', 'ta-pattern-card__head');
    var title = createElement('h3', '', pattern.title);
    var count = createElement(
      'span',
      'ta-pattern-card__progress',
      progress.completed + ' / ' + progress.total + ' complete'
    );
    head.appendChild(title);
    head.appendChild(count);
    card.appendChild(head);
    card.appendChild(makeMeter(progress, pattern.title + ' completion'));

    var target = pattern.lessons.filter(function (lesson) {
      return lessonState(state, lesson.route).status !== 'completed';
    })[0];
    var href = target ? target.href : (pattern.overview ? pattern.overview.href : '#/');
    var label = progress.completed === progress.total && progress.total
      ? 'Review pattern'
      : progress.completed || progress.studying ? 'Continue pattern' : 'Start pattern';
    card.appendChild(makeLink('ta-pattern-card__link', label, href));
    return card;
  }

  function renderDashboard(placeholder, models) {
    if (!placeholder) return;
    var state = readState();
    var progress = progressForLessons(models.lessons, state);
    // Announce the concise summary, not all 16 cards, when replacing the loader.
    placeholder.setAttribute('aria-live', 'off');
    placeholder.textContent = '';
    var dashboard = createElement('section', 'ta-dashboard');
    dashboard.setAttribute('aria-labelledby', 'ta-dashboard-title');

    var eyebrow = createElement('p', 'ta-dashboard__eyebrow', 'Your device-local field notes');
    var title = createElement('h2', 'ta-dashboard__title', 'Trace your next algorithm');
    title.id = 'ta-dashboard-title';
    var summary = createElement(
      'p',
      'ta-dashboard__summary',
      progress.completed + ' of ' + progress.total + ' Problem Lessons completed'
    );
    summary.setAttribute('role', 'status');
    dashboard.appendChild(eyebrow);
    dashboard.appendChild(title);
    dashboard.appendChild(summary);
    var meterWrap = createElement('div', 'ta-dashboard__meter');
    meterWrap.appendChild(makeMeter(progress, 'Curriculum completion'));
    dashboard.appendChild(meterWrap);

    var actions = createElement('div', 'ta-dashboard__actions');
    var last = state.lastRoute && models.lessonByRoute[state.lastRoute]
      ? models.lessonByRoute[state.lastRoute]
      : models.lessons.filter(function (lesson) {
        return lessonState(state, lesson.route).status !== 'completed';
      })[0];
    if (last) {
      var continueLabel = state.lastRoute === last.route ? 'Continue learning' : 'Start the curriculum';
      actions.appendChild(makeLink('ta-dashboard__continue', continueLabel, last.href));
    }
    var random = button('ta-dashboard__random', 'Random unseen lesson', 'random-unseen');
    random.disabled = !models.lessons.some(function (lesson) {
      return lessonState(state, lesson.route).status === 'not-started';
    });
    actions.appendChild(random);
    dashboard.appendChild(actions);

    var grid = createElement('div', 'ta-pattern-grid');
    models.patterns.forEach(function (pattern) {
      grid.appendChild(renderPatternCard(pattern, state));
    });
    dashboard.appendChild(grid);
    placeholder.appendChild(dashboard);
    placeholder.setAttribute('aria-busy', 'false');
  }

  function preferredTheme() {
    var saved = safeGet(THEME_KEY);
    if (saved === 'light' || saved === 'dark') return saved;
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  function applyTheme(theme) {
    var isDark = theme === 'dark';
    document.documentElement.setAttribute('data-ta-theme', isDark ? 'dark' : 'light');
    document.documentElement.style.colorScheme = isDark ? 'dark' : 'light';
    // Keep compatibility with the pre-redesign theme while it is still cached.
    document.documentElement.classList.toggle('dark-mode', isDark);
    var toggle = document.querySelector('.ta-theme-toggle');
    if (toggle) {
      toggle.setAttribute('aria-pressed', String(isDark));
      toggle.setAttribute('aria-label', 'Switch to ' + (isDark ? 'light' : 'dark') + ' theme');
      toggle.title = 'Switch to ' + (isDark ? 'light' : 'dark') + ' theme';
      toggle.textContent = isDark ? '\u2600' : '\u263e';
    }
  }

  function switchThemeWithoutTransition(theme) {
    var root = document.documentElement;
    if (themeSwitchFrame !== null) window.cancelAnimationFrame(themeSwitchFrame);
    root.classList.add('ta-theme-switching');
    applyTheme(theme);
    themeSwitchFrame = window.requestAnimationFrame(function () {
      themeSwitchFrame = window.requestAnimationFrame(function () {
        root.classList.remove('ta-theme-switching');
        themeSwitchFrame = null;
      });
    });
  }

  function toggleTheme() {
    var current = document.documentElement.getAttribute('data-ta-theme') || preferredTheme();
    var next = current === 'dark' ? 'light' : 'dark';
    safeSet(THEME_KEY, next);
    switchThemeWithoutTransition(next);
  }

  function ensureThemeToggle() {
    var toggle = document.querySelector('.ta-theme-toggle');
    if (!toggle) {
      toggle = button('ta-theme-toggle', '', 'toggle-theme');
      document.body.appendChild(toggle);
    }
    applyTheme(document.documentElement.getAttribute('data-ta-theme') || preferredTheme());
  }

  function refreshStatusUI(route, status, state) {
    Array.prototype.forEach.call(document.querySelectorAll('.ta-status-button[data-route]'), function (node) {
      if (node.getAttribute('data-route') === route) {
        node.setAttribute('aria-pressed', String(node.getAttribute('data-status') === status));
      }
    });
    var statusText = document.querySelector('.ta-lesson-toolbar__status');
    if (statusText && route === currentRoute) statusText.textContent = STATUS_LABELS[status];
    var hasUnseen = currentModels.lessons.some(function (lesson) {
      return lessonState(state, lesson.route).status === 'not-started';
    });
    Array.prototype.forEach.call(document.querySelectorAll('.ta-random-button'), function (node) {
      node.disabled = !hasUnseen;
    });
    enhanceSidebar(currentModels);
    var placeholder = document.getElementById('trace-atlas-dashboard');
    if (placeholder) renderDashboard(placeholder, currentModels);
  }

  function togglePattern(buttonNode) {
    var targetId = buttonNode.getAttribute('aria-controls');
    var list = targetId ? document.getElementById(targetId) : null;
    if (!list) return;
    var expanded = buttonNode.getAttribute('aria-expanded') === 'true';
    buttonNode.setAttribute('aria-expanded', String(!expanded));
    list.hidden = expanded;
    var slug = buttonNode.getAttribute('data-pattern');
    updateState(function (state) { state.sidebar[slug] = !expanded; });
  }

  function closestAction(target) {
    if (!target || !target.closest) return null;
    return target.closest('[data-ta-action]');
  }

  function handleDocumentClick(event) {
    var actionNode = closestAction(event.target);
    if (!actionNode) return;
    var action = actionNode.getAttribute('data-ta-action');
    if (action === 'skip-content') {
      event.preventDefault();
      var content = document.querySelector('.content');
      if (content) {
        content.id = 'main-content';
        content.tabIndex = -1;
        content.focus({ preventScroll: true });
        content.scrollIntoView({ block: 'start' });
      }
    } else if (action === 'set-status') {
      setLessonStatus(actionNode.getAttribute('data-route'), actionNode.getAttribute('data-status'));
    } else if (action === 'continue') {
      event.preventDefault();
      continueLearning();
    } else if (action === 'random-unseen') {
      event.preventDefault();
      randomUnseenLesson();
    } else if (action === 'toggle-pattern') {
      togglePattern(actionNode);
    } else if (action === 'station') {
      rememberStation(actionNode.getAttribute('data-route'), actionNode.getAttribute('data-station'));
    } else if (action === 'open-walkthrough') {
      openWalkthrough(actionNode.getAttribute('data-src'), actionNode.getAttribute('data-route'), actionNode);
    } else if (action === 'close-walkthrough') {
      closeWalkthrough();
    } else if (action === 'toggle-theme') {
      toggleTheme();
    }
  }

  function handleCheckpointToggle(event) {
    var detail = event.target;
    if (!detail || detail.tagName !== 'DETAILS' || !detail.open) return;
    var id = detail.getAttribute('data-ta-checkpoint');
    var route = detail.getAttribute('data-ta-route');
    if (!id || !route) return;
    detail.classList.add('ta-checkpoint--revealed');
    markCheckpointRevealed(route, id);
  }

  function handleDialogKeydown(event) {
    if (!activeDialog) return;
    if (event.key === 'Escape') {
      event.preventDefault();
      closeWalkthrough();
      return;
    }
    if (event.key !== 'Tab') return;
    var focusables = focusableElements(activeDialog);
    if (!focusables.length) {
      event.preventDefault();
      return;
    }
    var first = focusables[0];
    var last = focusables[focusables.length - 1];
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  function syncExplicitStation() {
    var anchorId = explicitAnchorId();
    var route = routeFromLocation();
    if (!anchorId || route !== currentRoute) return;
    var links = document.querySelectorAll('.ta-station-link[data-route]');
    var matched = null;
    Array.prototype.some.call(links, function (link) {
      var href = link.getAttribute('href') || '';
      var match = href.match(/[?&]id=([^&]+)/);
      var linkAnchor = '';
      if (match) {
        try { linkAnchor = decodeURIComponent(match[1]); }
        catch (error) { linkAnchor = match[1]; }
      }
      if (link.getAttribute('data-route') === route && linkAnchor === anchorId) {
        matched = link;
        return true;
      }
      return false;
    });
    if (!matched) return;
    Array.prototype.forEach.call(links, function (link) {
      if (link === matched) link.setAttribute('aria-current', 'step');
      else link.removeAttribute('aria-current');
    });
    var heading = document.getElementById(anchorId);
    if (heading) {
      var padding = parseFloat(window.getComputedStyle(document.documentElement).scrollPaddingTop) || 0;
      var target = Math.max(0, window.scrollY + heading.getBoundingClientRect().top - padding);
      document.documentElement.scrollTop = target;
      document.body.scrollTop = target;
    }
    rememberStation(route, matched.getAttribute('data-station'));
  }

  function bindGlobalEventsOnce() {
    if (TraceAtlas._eventsBound) return;
    TraceAtlas._eventsBound = true;
    document.addEventListener('click', handleDocumentClick);
    document.addEventListener('toggle', handleCheckpointToggle, true);
    document.addEventListener('keydown', handleDialogKeydown);
    window.addEventListener('hashchange', function () {
      window.setTimeout(syncExplicitStation, 0);
    });
    window.addEventListener('storage', function (event) {
      if (event.key !== STORAGE_KEY && event.key !== THEME_KEY) return;
      if (event.key === THEME_KEY) switchThemeWithoutTransition(preferredTheme());
      if (event.key === STORAGE_KEY) {
        volatileState = makeDefaultState();
        enhanceSidebar(currentModels);
        var placeholder = document.getElementById('trace-atlas-dashboard');
        if (placeholder) renderDashboard(placeholder, currentModels);
        var status = lessonState(readState(), currentRoute).status;
        refreshStatusUI(currentRoute, status, readState());
      }
    });
  }

  function clearRouteEnhancements() {
    if (routeObserver) {
      routeObserver.disconnect();
      routeObserver = null;
    }
    closeWalkthrough();
  }

  function enhanceCurrentRoute() {
    clearRouteEnhancements();
    currentRoute = routeFromLocation();
    currentModels = collectSidebarModels();
    enhanceSidebar(currentModels);
    ensureThemeToggle();

    var app = document.getElementById('app');
    if (app) {
      app.setAttribute('aria-busy', 'false');
      app.removeAttribute('role');
    }

    var article = document.querySelector('.markdown-section');
    if (!article) return;
    createBreadcrumbs(currentRoute, currentModels, article);

    if (isProblemRoute(currentRoute)) {
      rememberRoute(currentRoute);
      var toolbar = createLessonToolbar(currentRoute, currentModels, article);
      createExecutionRail(currentRoute, article, toolbar);
      enhanceCheckpoints(currentRoute, article);
      createLessonNavigation(currentRoute, currentModels, article);
      addWalkthroughAction(currentRoute, toolbar);
    }

    var dashboardPlaceholder = document.getElementById('trace-atlas-dashboard');
    if (dashboardPlaceholder) renderDashboard(dashboardPlaceholder, currentModels);
  }

  // Apply the saved theme before Docsify paints whenever this script is loaded in <head>.
  applyTheme(preferredTheme());

  TraceAtlas.storageKey = STORAGE_KEY;
  TraceAtlas.readState = readState;
  TraceAtlas.docsifyPlugin = function docsifyPlugin(hook) {
    if (TraceAtlas._pluginInstalled) return;
    TraceAtlas._pluginInstalled = true;
    bindGlobalEventsOnce();
    hook.doneEach(function () {
      // Docsify has committed the Markdown and sidebar DOM at this point.
      window.requestAnimationFrame(enhanceCurrentRoute);
    });
  };
})(window, document);
