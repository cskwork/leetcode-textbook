/*
 * TRACE ATLAS canvas effects
 *
 * Optional visual layer. The cover hero mounts Canvas UI's GlyphRain
 * (assets/glyph-rain.js, MIT + Commons Clause) as its one dominant effect,
 * and lesson completion fires a short confetti response. Everything here is
 * progressive: no WebGL2, no GlyphRain global, or prefers-reduced-motion all
 * degrade to the plain themed page.
 */
(function traceAtlasFx(window, document) {
  'use strict';

  var COVER_CHARSET = '01{}[]()<>=+-*/;.#nlogkO' + 'Σλ→√π';
  var CONFETTI_COLORS = ['#f5bf42', '#56c7b5', '#ff7555', '#fff8e8', '#087d73', '#e64f2e'];

  var coverInstance = null;
  var coverHost = null;

  function reducedMotion() {
    return window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }

  function mountCover() {
    var glyphRain = window.GlyphRain;
    if (!glyphRain || typeof glyphRain.createGlyphRain !== 'function') return;
    if (reducedMotion()) return;

    var cover = document.querySelector('section.cover');
    if (!cover) return;

    // Docsify can rebuild the cover; remount only when the host changed.
    if (coverInstance && coverHost === cover && cover.querySelector('.ta-cover-fx')) return;
    if (coverInstance) {
      try { coverInstance.destroy(); } catch (error) {}
      coverInstance = null;
    }
    var stale = cover.querySelector('.ta-cover-fx');
    if (stale) stale.remove();

    var content = cover.querySelector('.cover-main') || cover;
    var source = document.createElement('canvas');
    source.className = 'ta-cover-fx ta-cover-fx__source';
    source.setAttribute('layoutsubtree', 'true');
    source.setAttribute('aria-hidden', 'true');
    var output = document.createElement('canvas');
    output.className = 'ta-cover-fx ta-cover-fx__output';
    output.setAttribute('aria-hidden', 'true');
    cover.insertBefore(source, cover.firstChild);
    cover.insertBefore(output, source.nextSibling);

    if (!glyphRain.supportsHtmlInCanvas()) {
      // Overlay fallback: the rain renders without sampling the page.
      source.style.display = 'none';
    }

    coverInstance = glyphRain.createGlyphRain(
      { source: source, content: content, output: output },
      {
        charset: COVER_CHARSET,
        cell: 17,
        color: [0.34, 0.78, 0.71],
        headColor: [0.96, 0.75, 0.26],
        speed: 0.14,
        speedVariance: 0.55,
        density: 0.1,
        trail: 0.72,
        glow: 1.4,
        layers: 2,
        dim: 0.42,
        light: 2.2,
        lightRadius: 260,
        lightHeight: 180,
        stir: 0.6,
        stirRadius: 240,
        settle: 0.9
      }
    );

    if (!coverInstance) {
      source.remove();
      output.remove();
      return;
    }
    coverHost = cover;
  }

  function celebrate(origin) {
    if (reducedMotion()) return;

    var canvas = document.createElement('canvas');
    canvas.className = 'ta-confetti';
    canvas.setAttribute('aria-hidden', 'true');
    var context = canvas.getContext('2d');
    if (!context) return;
    document.body.appendChild(canvas);

    var ratio = Math.min(window.devicePixelRatio || 1, 2);
    canvas.width = Math.floor(window.innerWidth * ratio);
    canvas.height = Math.floor(window.innerHeight * ratio);
    context.scale(ratio, ratio);

    var burstX = window.innerWidth / 2;
    var burstY = window.innerHeight * 0.3;
    if (origin && origin.getBoundingClientRect) {
      var rect = origin.getBoundingClientRect();
      if (rect.width || rect.height) {
        burstX = rect.left + rect.width / 2;
        burstY = Math.max(60, rect.top + Math.min(rect.height / 3, 120));
      }
    }

    var particles = [];
    var count = 130;
    for (var i = 0; i < count; i += 1) {
      var angle = Math.random() * Math.PI * 2;
      var speed = 4 + Math.random() * 9;
      particles.push({
        x: burstX,
        y: burstY,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed - 6,
        size: 5 + Math.random() * 6,
        color: CONFETTI_COLORS[i % CONFETTI_COLORS.length],
        rotation: Math.random() * Math.PI,
        spin: (Math.random() - 0.5) * 0.3,
        shape: i % 3
      });
    }

    var started = null;
    var DURATION = 1600;
    function frame(timestamp) {
      if (started === null) started = timestamp;
      var elapsed = timestamp - started;
      context.clearRect(0, 0, window.innerWidth, window.innerHeight);
      var fade = Math.max(0, 1 - elapsed / DURATION);
      particles.forEach(function (particle) {
        particle.vy += 0.28;
        particle.vx *= 0.985;
        particle.x += particle.vx;
        particle.y += particle.vy;
        particle.rotation += particle.spin;
        context.save();
        context.globalAlpha = fade;
        context.translate(particle.x, particle.y);
        context.rotate(particle.rotation);
        context.fillStyle = particle.color;
        if (particle.shape === 0) {
          context.fillRect(-particle.size / 2, -particle.size / 4, particle.size, particle.size / 2);
        } else if (particle.shape === 1) {
          context.beginPath();
          context.arc(0, 0, particle.size / 2.4, 0, Math.PI * 2);
          context.fill();
        } else {
          context.beginPath();
          context.moveTo(0, -particle.size / 2);
          context.lineTo(particle.size / 2, particle.size / 2);
          context.lineTo(-particle.size / 2, particle.size / 2);
          context.closePath();
          context.fill();
        }
        context.restore();
      });
      if (elapsed < DURATION) {
        window.requestAnimationFrame(frame);
      } else {
        canvas.remove();
      }
    }
    window.requestAnimationFrame(frame);
  }

  window.TraceAtlasFx = {
    mountCover: mountCover,
    celebrate: celebrate
  };
})(window, document);
