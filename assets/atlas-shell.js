/*
 * TRACE ATLAS learning shell — Expedition update
 *
 * A dependency-free Docsify plugin that progressively enhances the Markdown
 * textbook into a step-by-step guided course. Markdown remains the source of
 * truth: if this file, localStorage, IntersectionObserver, or a walkthrough
 * fails, every lesson still reads and navigates normally as one page.
 *
 * The guided experience teaches little by little: each Problem Lesson is
 * played one Learning Station at a time behind a progress bar, finishing a
 * lesson celebrates and awards XP, and the home cover shows a per-Pattern
 * path of lesson nodes so the learner always knows the next small step.
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
  var XP_LESSON_COMPLETED = 20;
  var XP_CHECKPOINT_REVEALED = 2;

  /*
   * Step metadata for the guided Lesson Player. Keys double as the persisted
   * "station" value, so saved locations from the scroll-era shell still
   * resolve (intuition, pseudocode, java, complexity, dry-run are unchanged).
   */
  var STEP_META = [
    { key: 'concepts', match: /^concepts\b/i, label: 'Warm-up', hint: 'Meet the ideas first. No code yet, no pressure.' },
    { key: 'problem', match: /^problem\b/i, label: 'The problem', hint: 'Read it twice. Understanding slowly is faster.' },
    { key: 'intuition', match: /^intuition\b/i, label: 'Intuition', hint: 'Build the idea in plain words before any syntax.' },
    { key: 'pseudocode', match: /^pseudocode\b/i, label: 'Pseudocode', hint: 'Sketch the plan. This is the bridge to code.' },
    { key: 'java', match: /^java(?:\s+solution)?\b/i, label: 'Java', hint: 'Now the plan becomes real, runnable code.' },
    { key: 'complexity', match: /^complexity\b/i, label: 'Complexity', hint: 'Count what the plan costs in time and space.' },
    { key: 'dry-run', match: /^dry[\s-]*run\b/i, label: 'Dry-run', hint: 'Watch the algorithm run on a concrete input.' },
    { key: 'mistakes', match: /^common\s+mistakes\b/i, label: 'Traps', hint: 'Dodge the mistakes that trip most people.' },
    { key: 'related', match: /^related\s+problems\b/i, label: 'Next up', hint: 'See where this pattern appears again.' }
  ];

  /* Learning Stations recognized by the one-page reading rail. */
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

  var ICONS = {
    flame: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="currentColor"><path d="M12 2s1 2.4 1 4.2c0 1.7-1.1 2.7-1.1 4.4 0 1.2.9 2.1 2.1 2.1 1.9 0 2.6-2.2 2.4-3.9 2 1.6 3.6 4 3.6 6.6 0 3.9-3.6 6.6-8 6.6s-8-2.7-8-6.6C4 10.1 9 7.7 9 4.4 9 3.2 8.6 2 8.6 2 10.5 2.4 12 2 12 2z"/></svg>',
    star: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="currentColor"><path d="M12 2.5l2.6 5.9 6.4.6-4.8 4.3 1.4 6.2L12 16.2l-5.6 3.3 1.4-6.2L3 9l6.4-.6z"/></svg>',
    check: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round"><path d="M4.5 12.8l4.6 4.7L19.5 6.6"/></svg>',
    map: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"/><line x1="8" y1="2" x2="8" y2="18"/><line x1="16" y1="6" x2="16" y2="22"/></svg>',
    arrowRight: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M13 6l6 6-6 6"/></svg>',
    arrowLeft: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"><path d="M19 12H5"/><path d="M11 6l-6 6 6 6"/></svg>',
    sparkle: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="currentColor"><path d="M12 2l1.8 6.2L20 10l-6.2 1.8L12 18l-1.8-6.2L4 10l6.2-1.8z"/><circle cx="19" cy="19" r="2"/><circle cx="5" cy="19" r="1.4"/></svg>',
    book: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>',
    play: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="currentColor"><path d="M7 4.8c0-1.1 1.2-1.8 2.2-1.2l10.5 6.2c1 .6 1 2 0 2.6L9.2 18.6c-1 .6-2.2-.1-2.2-1.2z"/></svg>',
    dice: '<svg class="ta-icon" viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="4"/><circle cx="8.5" cy="8.5" r="1.4" fill="currentColor" stroke="none"/><circle cx="15.5" cy="15.5" r="1.4" fill="currentColor" stroke="none"/><circle cx="15.5" cy="8.5" r="1.4" fill="currentColor" stroke="none"/><circle cx="8.5" cy="15.5" r="1.4" fill="currentColor" stroke="none"/></svg>'
  };

  var volatileState = makeDefaultState();
  var routeObserver = null;
  var currentRoute = '/';
  var currentModels = { patterns: [], lessons: [], lessonByRoute: {} };
  var currentPlayer = null;
  var activeDialog = null;
  var dialogOpener = null;
  var dialogFocusFrame = null;
  var inertSiblings = [];
  var themeSwitchFrame = null;

  /* ------------------------------------------------------------------ */
  /* Learner state                                                       */
  /* ------------------------------------------------------------------ */

  function makeDefaultState() {
    return {
      version: STATE_VERSION,
      lessons: {},
      lastRoute: null,
      sidebar: {},
      xp: 0,
      activity: {},
      prefs: { view: 'steps' }
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

  function normalizeState(parsed) {
    parsed.lessons = parsed.lessons && typeof parsed.lessons === 'object' ? parsed.lessons : {};
    parsed.sidebar = parsed.sidebar && typeof parsed.sidebar === 'object' ? parsed.sidebar : {};
    parsed.lastRoute = typeof parsed.lastRoute === 'string' ? parsed.lastRoute : null;
    parsed.xp = typeof parsed.xp === 'number' && isFinite(parsed.xp) ? parsed.xp : 0;
    parsed.activity = parsed.activity && typeof parsed.activity === 'object' ? parsed.activity : {};
    parsed.prefs = parsed.prefs && typeof parsed.prefs === 'object' ? parsed.prefs : {};
    if (parsed.prefs.view !== 'steps' && parsed.prefs.view !== 'page') parsed.prefs.view = 'steps';
    return parsed;
  }

  function readState() {
    var raw = safeGet(STORAGE_KEY);
    if (!raw) return copyState(volatileState);

    try {
      var parsed = JSON.parse(raw);
      if (!parsed || parsed.version !== STATE_VERSION) {
        return copyState(volatileState);
      }
      volatileState = normalizeState(parsed);
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
      return { status: 'not-started', checkpoints: {}, station: null, xpAwarded: false };
    }
    return {
      status: validStatus(lesson.status),
      checkpoints: lesson.checkpoints && typeof lesson.checkpoints === 'object'
        ? lesson.checkpoints
        : {},
      station: typeof lesson.station === 'string' ? lesson.station : null,
      xpAwarded: lesson.xpAwarded === true
    };
  }

  function localDayStamp() {
    var now = new Date();
    return now.getFullYear()
      + '-' + String(now.getMonth() + 1).padStart(2, '0')
      + '-' + String(now.getDate()).padStart(2, '0');
  }

  function awardXp(state, amount) {
    if (!amount) return;
    state.xp = (typeof state.xp === 'number' && isFinite(state.xp) ? state.xp : 0) + amount;
    var day = localDayStamp();
    if (!state.activity || typeof state.activity !== 'object') state.activity = {};
    state.activity[day] = (typeof state.activity[day] === 'number' ? state.activity[day] : 0) + amount;
  }

  function touchActivity(state) {
    var day = localDayStamp();
    if (!state.activity || typeof state.activity !== 'object') state.activity = {};
    if (typeof state.activity[day] !== 'number') state.activity[day] = 0;
  }

  function streakDays(state) {
    var activity = state.activity && typeof state.activity === 'object' ? state.activity : {};
    var cursor = new Date();
    var streak = 0;
    var stamp = function (date) {
      return date.getFullYear()
        + '-' + String(date.getMonth() + 1).padStart(2, '0')
        + '-' + String(date.getDate()).padStart(2, '0');
    };
    // A streak survives until a full day is missed: today counts if present,
    // otherwise yesterday may still anchor an unbroken run.
    if (!Object.prototype.hasOwnProperty.call(activity, stamp(cursor))) {
      cursor.setDate(cursor.getDate() - 1);
    }
    while (Object.prototype.hasOwnProperty.call(activity, stamp(cursor))) {
      streak += 1;
      cursor.setDate(cursor.getDate() - 1);
    }
    return streak;
  }

  function setLessonStatus(route, status) {
    status = validStatus(status);
    var gained = 0;
    var state = updateState(function (next) {
      var existing = lessonState(next, route);
      var xpAwarded = existing.xpAwarded;
      if (status === 'completed' && !existing.xpAwarded) {
        awardXp(next, XP_LESSON_COMPLETED);
        gained = XP_LESSON_COMPLETED;
        xpAwarded = true;
      }
      touchActivity(next);
      next.lessons[route] = {
        status: status,
        checkpoints: existing.checkpoints,
        station: existing.station,
        xpAwarded: xpAwarded,
        updatedAt: new Date().toISOString()
      };
      next.lastRoute = route;
    });
    refreshStatusUI(route, status, state);
    return gained;
  }

  function rememberRoute(route) {
    updateState(function (state) {
      state.lastRoute = route;
    });
  }

  function markCheckpointRevealed(route, checkpointId) {
    var state = updateState(function (next) {
      var existing = lessonState(next, route);
      if (!existing.checkpoints[checkpointId]) {
        // First reveal of this Checkpoint earns a small nudge of XP.
        awardXp(next, XP_CHECKPOINT_REVEALED);
      }
      existing.checkpoints[checkpointId] = new Date().toISOString();
      next.lessons[route] = {
        // Revealing an answer is deliberately independent of Lesson Status.
        status: existing.status,
        checkpoints: existing.checkpoints,
        station: existing.station,
        xpAwarded: existing.xpAwarded,
        updatedAt: new Date().toISOString()
      };
      next.lastRoute = route;
    });
    refreshHud(state);
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
        xpAwarded: existing.xpAwarded,
        updatedAt: new Date().toISOString()
      };
      state.lastRoute = route;
    });
  }

  function setViewPreference(view) {
    updateState(function (state) {
      state.prefs.view = view === 'page' ? 'page' : 'steps';
    });
  }

  function viewPreference(state) {
    return state.prefs && state.prefs.view === 'page' ? 'page' : 'steps';
  }

  /* ------------------------------------------------------------------ */
  /* Routing                                                             */
  /* ------------------------------------------------------------------ */

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

  /* ------------------------------------------------------------------ */
  /* DOM helpers                                                         */
  /* ------------------------------------------------------------------ */

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

  function iconButton(className, icon, text, action) {
    var node = button(className, '', action);
    node.innerHTML = icon;
    if (text) node.appendChild(createElement('span', '', text));
    return node;
  }

  function makeLink(className, text, href) {
    var node = createElement('a', className, text);
    node.href = href;
    return node;
  }

  function iconLink(className, icon, text, href) {
    var node = makeLink(className, '', href);
    node.innerHTML = icon;
    if (text) node.appendChild(createElement('span', '', text));
    return node;
  }

  function titleWithoutCount(value) {
    return String(value || '').replace(/\s+\d+\s+problems?\s*$/i, '').trim();
  }

  function lessonTitleFromLink(link) {
    return String(link.textContent || '').replace(/^\s+|\s+$/g, '');
  }

  function shortLessonTitle(title) {
    return String(title || '').replace(/^\d+\s*-\s*/, '').trim() || title;
  }

  /* ------------------------------------------------------------------ */
  /* Curriculum model from the sidebar                                   */
  /* ------------------------------------------------------------------ */

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
      var numberMatch = rawTitle.match(/^Pattern\s+(\d+)/i);
      var pattern = {
        slug: slug,
        number: numberMatch ? numberMatch[1] : String(models.patterns.length + 1),
        title: titleWithoutCount(rawTitle).replace(/^Pattern\s+\d+\s*:\s*/i, ''),
        fullTitle: titleWithoutCount(rawTitle),
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

  function nextLesson(state) {
    var last = state.lastRoute && currentModels.lessonByRoute[state.lastRoute]
      ? currentModels.lessonByRoute[state.lastRoute]
      : null;
    if (last && lessonState(state, last.route).status !== 'completed') return last;
    var firstOpen = null;
    currentModels.lessons.some(function (lesson) {
      if (lessonState(state, lesson.route).status !== 'completed') {
        firstOpen = lesson;
        return true;
      }
      return false;
    });
    return firstOpen || last || currentModels.lessons[0] || null;
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

  /* ------------------------------------------------------------------ */
  /* Sidebar enhancement                                                 */
  /* ------------------------------------------------------------------ */

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
      var toggleTitle = createElement('span', 'ta-pattern-toggle__title', pattern.fullTitle);
      var toggleProgress = createElement(
        'span',
        'ta-pattern-toggle__progress',
        progress.completed + '/' + progress.total
      );
      if (progress.completed === progress.total && progress.total) {
        toggle.classList.add('ta-pattern-toggle--done');
      } else {
        toggle.classList.remove('ta-pattern-toggle--done');
      }
      toggle.appendChild(toggleTitle);
      toggle.appendChild(toggleProgress);
      toggle.setAttribute(
        'aria-label',
        pattern.fullTitle + ', ' + progress.completed + ' of ' + progress.total + ' completed'
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

  /* ------------------------------------------------------------------ */
  /* Breadcrumbs                                                         */
  /* ------------------------------------------------------------------ */

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
        patternItem.appendChild(makeLink('', pattern.fullTitle, pattern.overview.href));
      } else {
        patternItem.textContent = pattern.fullTitle;
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

  /* ------------------------------------------------------------------ */
  /* Lesson brief (always-visible lesson header)                         */
  /* ------------------------------------------------------------------ */

  function extractMetaChips(article) {
    // The lesson Markdown opens with "**Difficulty:** Easy" style lines that
    // render as one paragraph of <strong> label/value pairs.
    var heading = article.querySelector('h1');
    if (!heading) return null;
    var paragraph = heading.nextElementSibling;
    while (paragraph && paragraph.classList && (
      paragraph.classList.contains('ta-lesson-toolbar')
      || paragraph.classList.contains('ta-quest-bar')
    )) {
      paragraph = paragraph.nextElementSibling;
    }
    if (!paragraph || paragraph.tagName !== 'P' || !paragraph.querySelector('strong')) return null;

    var chips = [];
    var strongs = paragraph.querySelectorAll('strong');
    Array.prototype.forEach.call(strongs, function (strong) {
      var label = strong.textContent.replace(/:\s*$/, '').trim();
      var value = '';
      var href = '';
      var cursor = strong.nextSibling;
      while (cursor && !(cursor.nodeType === 1 && (cursor.tagName === 'STRONG' || cursor.tagName === 'BR'))) {
        if (cursor.nodeType === 3) value += cursor.textContent;
        else if (cursor.nodeType === 1 && cursor.tagName === 'A') {
          value += cursor.textContent;
          href = cursor.getAttribute('href') || '';
        } else if (cursor.nodeType === 1) value += cursor.textContent;
        cursor = cursor.nextSibling;
      }
      value = value.trim();
      if (label && value) chips.push({ label: label, value: value, href: href });
    });
    if (!chips.length) return null;
    return { paragraph: paragraph, chips: chips };
  }

  /*
   * Reverse every DOM change the shell made to the rendered Markdown so a
   * view-mode switch (or repeated enhancement) always starts from the same
   * clean article Docsify produced.
   */
  function teardownArticleEnhancements(article) {
    if (!article) return;
    var injectedSelectors = [
      '.ta-quest-bar', '.ta-step-dots', '.ta-player-live', '.ta-step-actions',
      '.ta-finish', '.ta-step__hint', '.ta-execution-rail', '.ta-lesson-nav',
      '.ta-lesson-brief', '.ta-breadcrumbs', '.ta-walkthrough-button'
    ];
    injectedSelectors.forEach(function (selector) {
      Array.prototype.forEach.call(article.querySelectorAll(selector), function (node) {
        node.remove();
      });
    });
    Array.prototype.forEach.call(article.querySelectorAll('.ta-step'), function (section) {
      while (section.firstChild) {
        section.parentNode.insertBefore(section.firstChild, section);
      }
      section.remove();
    });
    Array.prototype.forEach.call(article.querySelectorAll('[data-ta-meta]'), function (node) {
      node.hidden = false;
      node.removeAttribute('data-ta-meta');
    });
  }

  function createLessonBrief(route, models, article) {
    var old = article.querySelector('.ta-lesson-brief');
    if (old) old.remove();

    var heading = article.querySelector('h1');
    if (!heading) return null;

    var state = readState();
    var status = lessonState(state, route).status;
    var brief = createElement('section', 'ta-lesson-brief');
    brief.setAttribute('aria-label', 'Lesson overview');

    var meta = extractMetaChips(article);
    var chipRow = createElement('div', 'ta-lesson-brief__chips');
    if (meta) {
      meta.chips.forEach(function (chip) {
        if (/^leetcode$/i.test(chip.label) && chip.href) {
          var link = makeLink('ta-chip ta-chip--link', 'View on LeetCode', chip.href);
          link.target = '_blank';
          link.rel = 'noreferrer noopener';
          chipRow.appendChild(link);
          return;
        }
        var node = createElement('span', 'ta-chip', chip.value);
        if (/^difficulty$/i.test(chip.label)) {
          node.className = 'ta-chip ta-chip--' + chip.value.trim().toLowerCase().replace(/[^a-z]+/g, '-');
          node.setAttribute('aria-label', 'Difficulty: ' + chip.value);
        } else if (/^pattern$/i.test(chip.label)) {
          node.classList.add('ta-chip--pattern');
          node.setAttribute('aria-label', 'Pattern: ' + chip.value);
        }
        chipRow.appendChild(node);
      });
      meta.paragraph.hidden = true;
      meta.paragraph.setAttribute('data-ta-meta', 'true');
    }

    var statusRow = createElement('div', 'ta-lesson-brief__status');
    var statusText = createElement('span', 'ta-lesson-brief__status-text', STATUS_LABELS[status]);
    statusText.setAttribute('aria-live', 'polite');
    statusText.setAttribute('data-status', status);
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
    statusRow.appendChild(statusText);
    statusRow.appendChild(group);

    if (chipRow.children.length) brief.appendChild(chipRow);
    brief.appendChild(statusRow);
    heading.insertAdjacentElement('afterend', brief);
    return brief;
  }

  /* ------------------------------------------------------------------ */
  /* Guided Lesson Player (steps mode)                                   */
  /* ------------------------------------------------------------------ */

  function stepMetaForHeading(text) {
    var found = null;
    STEP_META.some(function (meta) {
      if (meta.match.test(text)) {
        found = meta;
        return true;
      }
      return false;
    });
    return found;
  }

  function slugifyStepKey(text, index) {
    var slug = String(text || '').toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '').slice(0, 32);
    return slug || 'section-' + (index + 1);
  }

  function collectStepBuckets(article) {
    // Everything before the first H2 belongs to the always-visible lesson
    // brief. Each H2 then opens a new step bucket.
    var buckets = [];
    var current = null;
    var children = Array.prototype.slice.call(article.children);
    children.forEach(function (node) {
      if (node.tagName === 'H2') {
        var text = node.textContent.trim();
        var meta = stepMetaForHeading(text);
        current = {
          key: meta ? meta.key : slugifyStepKey(text, buckets.length),
          label: meta ? meta.label : text,
          hint: meta ? meta.hint : '',
          heading: node,
          nodes: [node]
        };
        buckets.push(current);
        return;
      }
      if (current) current.nodes.push(node);
    });
    return buckets;
  }

  function createQuestBar(route, models, article, player) {
    var bar = createElement('div', 'ta-quest-bar');
    bar.setAttribute('data-route', route);

    var back = iconLink('ta-quest-bar__map', ICONS.map, 'Map', '#/');
    back.setAttribute('aria-label', 'Back to the curriculum map');
    bar.appendChild(back);

    var track = createElement('div', 'ta-quest-bar__track');
    track.setAttribute('role', 'progressbar');
    track.setAttribute('aria-label', 'Lesson progress');
    track.setAttribute('aria-valuemin', '0');
    track.setAttribute('aria-valuemax', String(player.steps.length));
    var fill = createElement('span', 'ta-quest-bar__fill');
    track.appendChild(fill);
    bar.appendChild(track);

    var counter = createElement('span', 'ta-quest-bar__counter', '');
    counter.setAttribute('aria-hidden', 'true');
    bar.appendChild(counter);

    var hud = createElement('div', 'ta-quest-bar__hud');
    hud.appendChild(makeHudChips(readState()));
    bar.appendChild(hud);

    var viewToggle = iconButton('ta-view-toggle', ICONS.book, 'One page', 'set-view-page');
    viewToggle.setAttribute('aria-label', 'Read this lesson as one page');
    bar.appendChild(viewToggle);

    player.track = track;
    player.fill = fill;
    player.counter = counter;
    return bar;
  }

  function makeHudChips(state) {
    var wrap = createElement('span', 'ta-hud');
    var streak = streakDays(state);
    var streakChip = createElement('span', 'ta-hud__chip ta-hud__chip--streak');
    streakChip.innerHTML = ICONS.flame;
    streakChip.appendChild(createElement('span', '', String(streak)));
    streakChip.setAttribute('title', streak === 1 ? '1 day streak' : streak + ' day streak');
    streakChip.setAttribute('aria-label', streak === 1 ? '1 day streak' : streak + ' day streak');
    if (!streak) streakChip.classList.add('ta-hud__chip--zero');
    var xpChip = createElement('span', 'ta-hud__chip ta-hud__chip--xp');
    xpChip.innerHTML = ICONS.star;
    xpChip.appendChild(createElement('span', '', String(state.xp || 0)));
    xpChip.setAttribute('title', (state.xp || 0) + ' XP earned');
    xpChip.setAttribute('aria-label', (state.xp || 0) + ' XP earned');
    wrap.appendChild(streakChip);
    wrap.appendChild(xpChip);
    return wrap;
  }

  function refreshHud(state) {
    if (!state) state = readState();
    Array.prototype.forEach.call(document.querySelectorAll('.ta-hud'), function (node) {
      var fresh = makeHudChips(state);
      node.replaceWith(fresh);
    });
  }

  function stepAnnouncement(player) {
    var step = player.steps[player.index];
    return 'Step ' + (player.index + 1) + ' of ' + player.steps.length + ': ' + step.label;
  }

  function showPlayerStep(player, index, options) {
    options = options || {};
    if (index < 0 || index >= player.steps.length) return;
    player.index = index;

    player.steps.forEach(function (step, stepIndex) {
      step.section.hidden = stepIndex !== index;
    });
    if (player.finishCard) player.finishCard.hidden = true;

    var step = player.steps[index];
    var percent = Math.round(((index + 1) / player.steps.length) * 100);
    player.fill.style.width = percent + '%';
    player.track.setAttribute('aria-valuenow', String(index + 1));
    player.track.setAttribute('aria-valuetext', stepAnnouncement(player));
    player.counter.textContent = (index + 1) + ' / ' + player.steps.length;
    if (player.live) player.live.textContent = stepAnnouncement(player);

    player.dots.forEach(function (dot, dotIndex) {
      if (dotIndex === index) dot.setAttribute('aria-current', 'step');
      else dot.removeAttribute('aria-current');
    });

    player.backButton.disabled = index === 0;
    var isLast = index === player.steps.length - 1;
    player.continueButton.innerHTML = '';
    if (isLast) {
      player.continueButton.appendChild(createElement('span', '', 'Finish lesson'));
      var sparkle = createElement('span', 'ta-btn__icon');
      sparkle.innerHTML = ICONS.sparkle;
      player.continueButton.appendChild(sparkle);
    } else {
      player.continueButton.appendChild(createElement('span', '', 'Continue'));
      var arrow = createElement('span', 'ta-btn__icon');
      arrow.innerHTML = ICONS.arrowRight;
      player.continueButton.appendChild(arrow);
    }

    rememberStation(player.route, step.key);

    if (!options.silent) {
      window.scrollTo(0, 0);
      if (options.focusHeading && step.heading) {
        step.heading.tabIndex = -1;
        step.heading.focus({ preventScroll: true });
      }
    }
  }

  function advancePlayer(player) {
    var state = readState();
    if (lessonState(state, player.route).status === 'not-started') {
      setLessonStatus(player.route, 'studying');
    }
    if (player.index >= player.steps.length - 1) {
      finishLesson(player);
      return;
    }
    showPlayerStep(player, player.index + 1, { focusHeading: true });
  }

  function retreatPlayer(player) {
    if (player.index === 0) return;
    showPlayerStep(player, player.index - 1, { focusHeading: true });
  }

  function finishLesson(player) {
    var wasCompleted = lessonState(readState(), player.route).status === 'completed';
    var gained = setLessonStatus(player.route, 'completed');
    var state = readState();

    player.steps.forEach(function (step) { step.section.hidden = true; });
    player.fill.style.width = '100%';
    player.counter.textContent = player.steps.length + ' / ' + player.steps.length;

    if (player.finishCard) player.finishCard.remove();
    var card = createElement('section', 'ta-finish');
    card.setAttribute('aria-label', 'Lesson complete');
    var mark = createElement('p', 'ta-finish__mark');
    mark.innerHTML = ICONS.sparkle;
    card.appendChild(mark);
    card.appendChild(createElement('h2', 'ta-finish__title', wasCompleted ? 'Reviewed and still solid' : 'Lesson complete'));

    var lesson = currentLesson(currentModels, player.route);
    var summary = wasCompleted
      ? 'Coming back to a finished lesson is how patterns stick. Nice consistency.'
      : 'One more pattern rep in the bank. Small steps, big compounding.';
    card.appendChild(createElement('p', 'ta-finish__summary', summary));

    var statRow = createElement('div', 'ta-finish__stats');
    if (gained) {
      var xpStat = createElement('span', 'ta-finish__stat ta-finish__stat--xp');
      xpStat.innerHTML = ICONS.star;
      xpStat.appendChild(createElement('span', '', '+' + gained + ' XP'));
      statRow.appendChild(xpStat);
    }
    var streak = streakDays(state);
    if (streak) {
      var streakStat = createElement('span', 'ta-finish__stat ta-finish__stat--streak');
      streakStat.innerHTML = ICONS.flame;
      streakStat.appendChild(createElement('span', '', streak === 1 ? '1 day streak' : streak + ' day streak'));
      statRow.appendChild(streakStat);
    }
    if (statRow.children.length) card.appendChild(statRow);

    var actions = createElement('div', 'ta-finish__actions');
    var index = -1;
    currentModels.lessons.some(function (candidate, candidateIndex) {
      if (candidate.route === player.route) {
        index = candidateIndex;
        return true;
      }
      return false;
    });
    var next = index >= 0 ? currentModels.lessons[index + 1] : null;
    if (next) {
      var nextLink = iconLink('ta-btn ta-btn--primary', '', 'Next: ' + shortLessonTitle(next.title), next.href);
      var arrow = createElement('span', 'ta-btn__icon');
      arrow.innerHTML = ICONS.arrowRight;
      nextLink.appendChild(arrow);
      actions.appendChild(nextLink);
    } else {
      actions.appendChild(iconLink('ta-btn ta-btn--primary', ICONS.map, 'Back to the map', '#/'));
    }
    var review = button('ta-btn ta-btn--ghost', 'Review this lesson', 'restart-steps');
    actions.appendChild(review);
    if (next) actions.appendChild(iconLink('ta-btn ta-btn--ghost', ICONS.map, 'Map', '#/'));
    card.appendChild(actions);

    player.actionsRow.hidden = true;
    player.actionsRow.insertAdjacentElement('beforebegin', card);
    player.finishCard = card;

    if (player.live) player.live.textContent = 'Lesson complete.';
    card.tabIndex = -1;
    window.scrollTo(0, 0);
    card.focus({ preventScroll: true });

    if (window.TraceAtlasFx && typeof window.TraceAtlasFx.celebrate === 'function') {
      try { window.TraceAtlasFx.celebrate(card); } catch (error) {}
    }
  }

  function restartPlayerSteps(player) {
    if (player.finishCard) {
      player.finishCard.remove();
      player.finishCard = null;
    }
    player.actionsRow.hidden = false;
    showPlayerStep(player, 0, { focusHeading: true });
  }

  function buildLessonPlayer(route, models, article) {
    var buckets = collectStepBuckets(article);
    if (buckets.length < 2) return null;

    var player = {
      route: route,
      steps: [],
      index: 0,
      dots: [],
      finishCard: null
    };

    // Wrap each bucket's nodes in a step <section> so a single [hidden]
    // attribute controls visibility without touching the Markdown DOM.
    buckets.forEach(function (bucket, index) {
      var section = createElement('section', 'ta-step');
      section.setAttribute('data-ta-step', bucket.key);
      section.setAttribute('aria-label', 'Step ' + (index + 1) + ' of ' + buckets.length + ': ' + bucket.label);
      article.insertBefore(section, bucket.nodes[0]);
      bucket.nodes.forEach(function (node) { section.appendChild(node); });

      if (bucket.hint) {
        var hint = createElement('p', 'ta-step__hint', bucket.hint);
        var heading = bucket.heading;
        heading.insertAdjacentElement('afterend', hint);
      }

      player.steps.push({
        key: bucket.key,
        label: bucket.label,
        heading: bucket.heading,
        section: section
      });
    });

    var bar = createQuestBar(route, models, article, player);
    article.insertBefore(bar, article.firstChild);
    player.bar = bar;

    var dots = createElement('nav', 'ta-step-dots');
    dots.setAttribute('aria-label', 'Lesson steps');
    player.steps.forEach(function (step, index) {
      var dot = button('ta-step-dot', '', 'goto-step');
      dot.setAttribute('data-step-index', String(index));
      dot.setAttribute('aria-label', 'Step ' + (index + 1) + ': ' + step.label);
      dot.appendChild(createElement('span', 'ta-step-dot__label', step.label));
      dots.appendChild(dot);
      player.dots.push(dot);
    });
    bar.insertAdjacentElement('afterend', dots);

    var live = createElement('p', 'ta-player-live ta-visually-hidden');
    live.setAttribute('aria-live', 'polite');
    dots.insertAdjacentElement('afterend', live);
    player.live = live;

    var actionsRow = createElement('div', 'ta-step-actions');
    var backButton = iconButton('ta-btn ta-btn--ghost ta-btn--back', ICONS.arrowLeft, 'Back', 'step-back');
    var continueButton = button('ta-btn ta-btn--primary ta-btn--continue', '', 'step-continue');
    actionsRow.appendChild(backButton);
    actionsRow.appendChild(continueButton);
    article.appendChild(actionsRow);
    player.actionsRow = actionsRow;
    player.backButton = backButton;
    player.continueButton = continueButton;

    // Resume where the learner left off; an explicit deep link outranks the
    // saved location so search results and shared URLs open the right step.
    var startIndex = 0;
    var savedStation = lessonState(readState(), route).station;
    var savedIndex = player.steps.findIndex(function (step) { return step.key === savedStation; });
    if (savedIndex > 0) startIndex = savedIndex;
    var anchorId = explicitAnchorId();
    if (anchorId) {
      var anchorNode = document.getElementById(anchorId);
      var anchorIndex = player.steps.findIndex(function (step) {
        return step.section.contains(anchorNode) || step.heading.id === anchorId || step.key === anchorId;
      });
      if (anchorIndex >= 0) startIndex = anchorIndex;
    }

    showPlayerStep(player, startIndex, { silent: !anchorId });
    if (anchorId) {
      var anchorTarget = document.getElementById(anchorId);
      if (anchorTarget) {
        window.setTimeout(function () {
          var padding = parseFloat(window.getComputedStyle(document.documentElement).scrollPaddingTop) || 0;
          var top = Math.max(0, window.scrollY + anchorTarget.getBoundingClientRect().top - padding);
          document.documentElement.scrollTop = top;
          document.body.scrollTop = top;
        }, 0);
      }
    }
    return player;
  }

  /* ------------------------------------------------------------------ */
  /* One-page reading rail (page mode)                                   */
  /* ------------------------------------------------------------------ */

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

  function createPageModeBar(route, article) {
    var bar = createElement('div', 'ta-quest-bar ta-quest-bar--page');
    bar.setAttribute('data-route', route);
    var back = iconLink('ta-quest-bar__map', ICONS.map, 'Map', '#/');
    back.setAttribute('aria-label', 'Back to the curriculum map');
    bar.appendChild(back);
    var hud = createElement('div', 'ta-quest-bar__hud');
    hud.appendChild(makeHudChips(readState()));
    bar.appendChild(hud);
    var viewToggle = iconButton('ta-view-toggle', ICONS.play, 'Step mode', 'set-view-steps');
    viewToggle.setAttribute('aria-label', 'Study this lesson step by step');
    bar.appendChild(viewToggle);
    article.insertBefore(bar, article.firstChild);
    return bar;
  }

  function createExecutionRail(route, article, anchorNodeElement) {
    var stations = findStations(article);
    if (!stations.length || !anchorNodeElement) return;
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
    anchorNodeElement.insertAdjacentElement('afterend', rail);

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

  /* ------------------------------------------------------------------ */
  /* Adjacent lesson navigation (page mode)                              */
  /* ------------------------------------------------------------------ */

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
        '← ' + previous.title,
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
        next.title + ' →',
        next.href
      );
      nextLink.setAttribute('rel', 'next');
      nav.appendChild(nextLink);
    }
    article.appendChild(nav);
  }

  /* ------------------------------------------------------------------ */
  /* Checkpoints                                                         */
  /* ------------------------------------------------------------------ */

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

  /* ------------------------------------------------------------------ */
  /* Interactive Walkthrough dialog                                      */
  /* ------------------------------------------------------------------ */

  function visualizationUrl(route) {
    try {
      return new window.URL(route + '/visualization/index.html', document.baseURI).href;
    } catch (error) {
      return route + '/visualization/index.html';
    }
  }

  function addWalkthroughAction(route, article) {
    if (!article || WALKTHROUGH_ROUTES.indexOf(route) === -1) return;
    if (article.querySelector('.ta-walkthrough-button')) return;
    var url = visualizationUrl(route);
    var brief = article.querySelector('.ta-lesson-brief');
    if (!brief) return;
    var launch = iconButton('ta-walkthrough-button', ICONS.play, 'Interactive walkthrough', 'open-walkthrough');
    launch.setAttribute('data-src', url);
    launch.setAttribute('data-route', route);
    launch.setAttribute('aria-haspopup', 'dialog');
    brief.appendChild(launch);
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
      frame.addEventListener('load', function () {
        if (activeDialog === overlay && !overlay.contains(document.activeElement)) close.focus();
      });
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
    watchDialogFocus();
  }

  function closeWalkthrough() {
    if (!activeDialog) return;
    if (dialogFocusFrame !== null) {
      window.cancelAnimationFrame(dialogFocusFrame);
      dialogFocusFrame = null;
    }
    var restore = dialogOpener;
    activeDialog.remove();
    activeDialog = null;
    restoreDialogBackground();
    dialogOpener = null;
    document.body.classList.remove('ta-dialog-open');
    if (restore && document.documentElement.contains(restore)) restore.focus();
  }

  /* ------------------------------------------------------------------ */
  /* Lesson jumping                                                      */
  /* ------------------------------------------------------------------ */

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
    var next = nextLesson(state);
    if (!next) return;
    window.location.hash = next.href.indexOf('#') === 0 ? next.href.slice(1) : '/' + next.route + '/README';
  }

  /* ------------------------------------------------------------------ */
  /* Home dashboard: the expedition path                                 */
  /* ------------------------------------------------------------------ */

  function renderUnitPath(pattern, state, upNextRoute) {
    var path = createElement('ol', 'ta-unit__path');
    pattern.lessons.forEach(function (lesson) {
      var item = createElement('li');
      var status = lessonState(state, lesson.route).status;
      var node = makeLink('ta-node', '', lesson.href);
      node.setAttribute('data-status', status);
      var title = shortLessonTitle(lesson.title);
      node.setAttribute('aria-label', title + ', ' + STATUS_LABELS[status]);
      node.setAttribute('title', title + ' — ' + STATUS_LABELS[status]);
      if (status === 'completed') {
        node.innerHTML = ICONS.check;
      } else {
        node.appendChild(createElement('span', 'ta-node__dot'));
      }
      if (lesson.route === upNextRoute) {
        node.classList.add('ta-node--next');
        node.setAttribute('aria-label', title + ', up next');
        node.setAttribute('title', title + ' — up next');
      }
      item.appendChild(node);
      path.appendChild(item);
    });
    return path;
  }

  function renderUnit(pattern, state, upNextRoute) {
    var progress = progressForLessons(pattern.lessons, state);
    var unit = createElement('section', 'ta-unit');
    if (progress.completed === progress.total && progress.total) unit.classList.add('ta-unit--done');
    if (pattern.lessons.some(function (lesson) { return lesson.route === upNextRoute; })) {
      unit.classList.add('ta-unit--active');
    }

    var head = createElement('div', 'ta-unit__head');
    var badge = createElement('span', 'ta-unit__badge', pattern.number);
    badge.setAttribute('aria-hidden', 'true');
    var titleWrap = createElement('div', 'ta-unit__titles');
    var title = createElement('h3', 'ta-unit__title');
    if (pattern.overview) {
      title.appendChild(makeLink('', pattern.title, pattern.overview.href));
    } else {
      title.textContent = pattern.title;
    }
    var count = createElement(
      'p',
      'ta-unit__count',
      progress.completed + ' of ' + progress.total + ' lessons'
    );
    titleWrap.appendChild(title);
    titleWrap.appendChild(count);
    head.appendChild(badge);
    head.appendChild(titleWrap);
    unit.appendChild(head);
    unit.appendChild(makeMeter(progress, pattern.fullTitle + ' completion'));
    unit.appendChild(renderUnitPath(pattern, state, upNextRoute));
    return unit;
  }

  function renderDashboard(placeholder, models) {
    if (!placeholder) return;
    var state = readState();
    var progress = progressForLessons(models.lessons, state);
    // Announce the concise summary, not all 16 units, when replacing the loader.
    placeholder.setAttribute('aria-live', 'off');
    placeholder.textContent = '';
    var dashboard = createElement('section', 'ta-dashboard');
    dashboard.setAttribute('aria-labelledby', 'ta-dashboard-title');

    var hero = createElement('div', 'ta-dash-hero');
    var eyebrow = createElement('p', 'ta-dashboard__eyebrow', 'Your expedition');
    var title = createElement('h2', 'ta-dashboard__title', progress.completed
      ? 'Keep the streak moving'
      : 'Start small. Finish big.');
    title.id = 'ta-dashboard-title';
    var summary = createElement(
      'p',
      'ta-dashboard__summary',
      progress.completed + ' of ' + progress.total + ' Problem Lessons completed'
    );
    summary.setAttribute('role', 'status');
    hero.appendChild(eyebrow);
    hero.appendChild(title);
    var hud = createElement('div', 'ta-dash-hero__hud');
    hud.appendChild(makeHudChips(state));
    hero.appendChild(hud);
    hero.appendChild(summary);
    var meterWrap = createElement('div', 'ta-dashboard__meter');
    meterWrap.appendChild(makeMeter(progress, 'Curriculum completion'));
    hero.appendChild(meterWrap);
    dashboard.appendChild(hero);

    var next = nextLesson(state);
    if (next) {
      var nextCard = createElement('div', 'ta-dash-next');
      var nextEyebrow = createElement('p', 'ta-dash-next__eyebrow', state.lastRoute ? 'Jump back in' : 'First lesson');
      var nextTitle = createElement('p', 'ta-dash-next__title', shortLessonTitle(next.title));
      var nextPattern = createElement('p', 'ta-dash-next__pattern', next.pattern ? next.pattern.fullTitle : '');
      nextCard.appendChild(nextEyebrow);
      nextCard.appendChild(nextTitle);
      if (next.pattern) nextCard.appendChild(nextPattern);
      var nextActions = createElement('div', 'ta-dashboard__actions');
      var continueLink = iconLink('ta-btn ta-btn--primary ta-dashboard__continue', ICONS.play, state.lastRoute ? 'Continue' : 'Start here', next.href);
      nextActions.appendChild(continueLink);
      var random = iconButton('ta-btn ta-btn--ghost ta-dashboard__random', ICONS.dice, 'Surprise me', 'random-unseen');
      random.disabled = !models.lessons.some(function (lesson) {
        return lessonState(state, lesson.route).status === 'not-started';
      });
      nextActions.appendChild(random);
      nextCard.appendChild(nextActions);
      dashboard.appendChild(nextCard);
    }

    var units = createElement('div', 'ta-units');
    var upNextRoute = next ? next.route : null;
    models.patterns.forEach(function (pattern) {
      units.appendChild(renderUnit(pattern, state, upNextRoute));
    });
    dashboard.appendChild(units);
    placeholder.appendChild(dashboard);
    placeholder.setAttribute('aria-busy', 'false');
  }

  /* ------------------------------------------------------------------ */
  /* Theme                                                               */
  /* ------------------------------------------------------------------ */

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
      toggle.textContent = isDark ? '☀' : '☾';
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

  /* ------------------------------------------------------------------ */
  /* Cross-surface refresh                                               */
  /* ------------------------------------------------------------------ */

  function refreshStatusUI(route, status, state) {
    Array.prototype.forEach.call(document.querySelectorAll('.ta-status-button[data-route]'), function (node) {
      if (node.getAttribute('data-route') === route) {
        node.setAttribute('aria-pressed', String(node.getAttribute('data-status') === status));
      }
    });
    var statusText = document.querySelector('.ta-lesson-brief__status-text');
    if (statusText && route === currentRoute) {
      statusText.textContent = STATUS_LABELS[status];
      statusText.setAttribute('data-status', status);
    }
    var hasUnseen = currentModels.lessons.some(function (lesson) {
      return lessonState(state, lesson.route).status === 'not-started';
    });
    Array.prototype.forEach.call(document.querySelectorAll('.ta-dashboard__random'), function (node) {
      node.disabled = !hasUnseen;
    });
    enhanceSidebar(currentModels);
    refreshHud(state);
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

  /* ------------------------------------------------------------------ */
  /* Event wiring                                                        */
  /* ------------------------------------------------------------------ */

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
    } else if (action === 'step-continue') {
      if (currentPlayer) advancePlayer(currentPlayer);
    } else if (action === 'step-back') {
      if (currentPlayer) retreatPlayer(currentPlayer);
    } else if (action === 'goto-step') {
      if (currentPlayer) {
        var stepIndex = parseInt(actionNode.getAttribute('data-step-index'), 10);
        if (!isNaN(stepIndex)) {
          if (currentPlayer.finishCard) restartPlayerSteps(currentPlayer);
          showPlayerStep(currentPlayer, stepIndex, { focusHeading: true });
        }
      }
    } else if (action === 'restart-steps') {
      if (currentPlayer) restartPlayerSteps(currentPlayer);
    } else if (action === 'set-view-page') {
      setViewPreference('page');
      enhanceCurrentRoute();
    } else if (action === 'set-view-steps') {
      setViewPreference('steps');
      enhanceCurrentRoute();
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

  function watchDialogFocus() {
    if (dialogFocusFrame !== null) window.cancelAnimationFrame(dialogFocusFrame);
    function keepFocusInside() {
      if (!activeDialog) {
        dialogFocusFrame = null;
        return;
      }
      if (!activeDialog.contains(document.activeElement)) {
        var focusables = focusableElements(activeDialog);
        if (focusables.length) focusables[0].focus();
      }
      dialogFocusFrame = window.requestAnimationFrame(keepFocusInside);
    }
    dialogFocusFrame = window.requestAnimationFrame(keepFocusInside);
  }

  function syncExplicitStation() {
    var anchorId = explicitAnchorId();
    var route = routeFromLocation();
    if (!anchorId || route !== currentRoute) return;

    // In steps mode a deep link may target a heading inside a hidden step:
    // reveal that step before honoring the scroll intent.
    if (currentPlayer) {
      var anchorNode = document.getElementById(anchorId);
      var stepIndex = currentPlayer.steps.findIndex(function (step) {
        return step.section.contains(anchorNode) || step.heading.id === anchorId || step.key === anchorId;
      });
      if (stepIndex >= 0 && stepIndex !== currentPlayer.index) {
        if (currentPlayer.finishCard) restartPlayerSteps(currentPlayer);
        showPlayerStep(currentPlayer, stepIndex, { silent: true });
      }
      if (anchorNode) {
        var stepPadding = parseFloat(window.getComputedStyle(document.documentElement).scrollPaddingTop) || 0;
        var stepTarget = Math.max(0, window.scrollY + anchorNode.getBoundingClientRect().top - stepPadding);
        document.documentElement.scrollTop = stepTarget;
        document.body.scrollTop = stepTarget;
      }
      return;
    }

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

  /* ------------------------------------------------------------------ */
  /* Route lifecycle                                                     */
  /* ------------------------------------------------------------------ */

  function clearRouteEnhancements() {
    if (routeObserver) {
      routeObserver.disconnect();
      routeObserver = null;
    }
    currentPlayer = null;
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

    teardownArticleEnhancements(article);
    createBreadcrumbs(currentRoute, currentModels, article);

    if (isProblemRoute(currentRoute)) {
      rememberRoute(currentRoute);
      var brief = createLessonBrief(currentRoute, currentModels, article);
      enhanceCheckpoints(currentRoute, article);
      addWalkthroughAction(currentRoute, article);

      var view = viewPreference(readState());
      if (view === 'steps') {
        currentPlayer = buildLessonPlayer(currentRoute, currentModels, article);
      }
      if (!currentPlayer) {
        // One-page reading keeps the classic scroll experience with a
        // station rail and adjacent-lesson navigation.
        var bar = createPageModeBar(currentRoute, article);
        createExecutionRail(currentRoute, article, bar);
        createLessonNavigation(currentRoute, currentModels, article);
      }
    }

    var dashboardPlaceholder = document.getElementById('trace-atlas-dashboard');
    if (dashboardPlaceholder) renderDashboard(dashboardPlaceholder, currentModels);

    if (window.TraceAtlasFx && typeof window.TraceAtlasFx.mountCover === 'function') {
      try { window.TraceAtlasFx.mountCover(); } catch (error) {}
    }
  }

  // Apply the saved theme before Docsify paints whenever this script is loaded in <head>.
  applyTheme(preferredTheme());

  TraceAtlas.storageKey = STORAGE_KEY;
  TraceAtlas.readState = readState;
  TraceAtlas.streakDays = function () { return streakDays(readState()); };
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
