/**
 * canvas-effects.js - Interactive canvas effects for LeetCode Textbook
 * Inspired by canvas-ui (https://github.com/DavidHDev/canvas-ui)
 * Vanilla JS, no build step required.
 *
 * Effects:
 * 1. Fluid gradient background (cover page)
 * 2. Cursor-reactive particle network (content pages)
 * 3. Page reveal transitions
 * 4. Code block glow effects
 * 5. Sidebar active-link ripple
 */

(function () {
  'use strict';

  // ================================================================
  // 1. FLUID GRADIENT BACKGROUND (Cover Page)
  // ================================================================
  class FluidGradient {
    constructor(canvas) {
      this.canvas = canvas;
      this.ctx = canvas.getContext('2d');
      this.blobs = [];
      this.mouseX = 0;
      this.mouseY = 0;
      this.targetMouseX = 0;
      this.targetMouseY = 0;
      this.running = false;
      this.raf = 0;
      this.init();
    }

    init() {
      this.resize();
      const colors = [
        'rgba(102, 126, 234, 0.45)',  // Indigo
        'rgba(118, 75, 162, 0.40)',   // Purple
        'rgba(240, 80, 180, 0.30)',   // Pink
        'rgba(80, 200, 240, 0.25)',   // Cyan
      ];
      for (let i = 0; i < 5; i++) {
        this.blobs.push({
          x: Math.random() * this.canvas.width,
          y: Math.random() * this.canvas.height,
          vx: (Math.random() - 0.5) * 0.6,
          vy: (Math.random() - 0.5) * 0.6,
          r: 200 + Math.random() * 250,
          color: colors[i % colors.length],
          pulse: Math.random() * Math.PI * 2,
          pulseSpeed: 0.008 + Math.random() * 0.012,
        });
      }
      this.targetMouseX = this.mouseX = this.canvas.width / 2;
      this.targetMouseY = this.mouseY = this.canvas.height / 2;
    }

    resize() {
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      this.canvas.width = window.innerWidth * dpr;
      this.canvas.height = window.innerHeight * dpr;
      this.ctx.scale(dpr, dpr);
      this.canvas.style.width = window.innerWidth + 'px';
      this.canvas.style.height = window.innerHeight + 'px';
    }

    start() {
      if (this.running) return;
      this.running = true;
      this.loop();
    }

    stop() {
      this.running = false;
      cancelAnimationFrame(this.raf);
    }

    onMove(x, y) {
      this.targetMouseX = x;
      this.targetMouseY = y;
    }

    loop() {
      if (!this.running) return;
      const w = window.innerWidth;
      const h = window.innerHeight;

      this.mouseX += (this.targetMouseX - this.mouseX) * 0.05;
      this.mouseY += (this.targetMouseY - this.mouseY) * 0.05;

      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      for (const blob of this.blobs) {
        blob.x += blob.vx;
        blob.y += blob.vy;
        blob.pulse += blob.pulseSpeed;

        if (blob.x < -blob.r) blob.vx = Math.abs(blob.vx);
        if (blob.x > w + blob.r) blob.vx = -Math.abs(blob.vx);
        if (blob.y < -blob.r) blob.vy = Math.abs(blob.vy);
        if (blob.y > h + blob.r) blob.vy = -Math.abs(blob.vy);

        // Mouse attraction
        const dx = this.mouseX - blob.x;
        const dy = this.mouseY - blob.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 300) {
          blob.vx += (dx / dist) * 0.03;
          blob.vy += (dy / dist) * 0.03;
        }

        // Speed cap
        const speed = Math.sqrt(blob.vx * blob.vx + blob.vy * blob.vy);
        const maxSpeed = 1.5;
        if (speed > maxSpeed) {
          blob.vx = (blob.vx / speed) * maxSpeed;
          blob.vy = (blob.vy / speed) * maxSpeed;
        }

        const pulseR = blob.r + Math.sin(blob.pulse) * 30;
        const gradient = this.ctx.createRadialGradient(blob.x, blob.y, 0, blob.x, blob.y, pulseR);
        gradient.addColorStop(0, blob.color);
        gradient.addColorStop(1, 'rgba(0,0,0,0)');

        this.ctx.fillStyle = gradient;
        this.ctx.beginPath();
        this.ctx.arc(blob.x, blob.y, pulseR, 0, Math.PI * 2);
        this.ctx.fill();
      }

      // Cursor glow
      const cursorGrad = this.ctx.createRadialGradient(
        this.mouseX, this.mouseY, 0,
        this.mouseX, this.mouseY, 150
      );
      cursorGrad.addColorStop(0, 'rgba(255, 255, 255, 0.15)');
      cursorGrad.addColorStop(1, 'rgba(255, 255, 255, 0)');
      this.ctx.fillStyle = cursorGrad;
      this.ctx.beginPath();
      this.ctx.arc(this.mouseX, this.mouseY, 150, 0, Math.PI * 2);
      this.ctx.fill();

      this.raf = requestAnimationFrame(() => this.loop());
    }
  }

  // ================================================================
  // 2. CURSOR PARTICLE TRAIL (Content Pages)
  // ================================================================
  class ParticleTrail {
    constructor() {
      this.particles = [];
      this.canvas = null;
      this.ctx = null;
      this.running = false;
      this.raf = 0;
      this.lastSpawn = 0;
    }

    ensureCanvas() {
      if (this.canvas) return;
      this.canvas = document.createElement('canvas');
      this.canvas.id = 'particle-canvas';
      this.canvas.style.cssText = 'position:fixed;top:0;left:0;width:100vw;height:100vh;pointer-events:none;z-index:9998;';
      document.body.appendChild(this.canvas);
      this.ctx = this.canvas.getContext('2d');
      this.resize();
      window.addEventListener('resize', () => this.resize());
    }

    resize() {
      if (!this.canvas) return;
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      this.canvas.width = window.innerWidth * dpr;
      this.canvas.height = window.innerHeight * dpr;
      this.canvas.style.width = window.innerWidth + 'px';
      this.canvas.style.height = window.innerHeight + 'px';
      this.ctx.setTransform(1, 0, 0, 1, 0, 0);
      this.ctx.scale(dpr, dpr);
    }

    spawn(x, y) {
      const colors = ['#667eea', '#764ba2', '#0969da', '#58a6ff', '#f050b4', '#50c8f0'];
      this.particles.push({
        x, y,
        vx: (Math.random() - 0.5) * 2,
        vy: (Math.random() - 0.5) * 2 - 0.5,
        life: 1,
        decay: 0.015 + Math.random() * 0.02,
        size: 2 + Math.random() * 4,
        color: colors[Math.floor(Math.random() * colors.length)],
      });
      if (this.particles.length > 120) this.particles.shift();
    }

    start() {
      this.ensureCanvas();
      if (this.running) return;
      this.running = true;
      this.loop();
    }

    stop() {
      this.running = false;
      cancelAnimationFrame(this.raf);
    }

    loop() {
      if (!this.running) return;
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      // Update and draw particles
      for (let i = this.particles.length - 1; i >= 0; i--) {
        const p = this.particles[i];
        p.x += p.vx;
        p.y += p.vy;
        p.vy += 0.03; // gravity
        p.life -= p.decay;
        if (p.life <= 0) {
          this.particles.splice(i, 1);
          continue;
        }

        // Draw connecting lines to nearby particles
        for (let j = i - 1; j >= 0; j--) {
          const other = this.particles[j];
          const dx = p.x - other.x;
          const dy = p.y - other.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < 80) {
            this.ctx.strokeStyle = p.color + Math.floor(p.life * 30).toString(16).padStart(2, '0');
            this.ctx.lineWidth = 0.5;
            this.ctx.beginPath();
            this.ctx.moveTo(p.x, p.y);
            this.ctx.lineTo(other.x, other.y);
            this.ctx.stroke();
          }
        }

        // Draw particle
        this.ctx.globalAlpha = p.life;
        this.ctx.fillStyle = p.color;
        this.ctx.beginPath();
        this.ctx.arc(p.x, p.y, p.size * p.life, 0, Math.PI * 2);
        this.ctx.fill();

        // Glow
        this.ctx.shadowBlur = 8;
        this.ctx.shadowColor = p.color;
        this.ctx.fill();
        this.ctx.shadowBlur = 0;
      }
      this.ctx.globalAlpha = 1;

      this.raf = requestAnimationFrame(() => this.loop());
    }
  }

  // ================================================================
  // 3. PAGE REVEAL TRANSITIONS
  // ================================================================
  class PageReveal {
    constructor() {
      this.observer = null;
    }

    observe() {
      if (this.observer) this.observer.disconnect();

      this.observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('canvas-revealed');
          }
        });
      }, { threshold: 0.05, rootMargin: '0px 0px -40px 0px' });

      // Observe all direct children of markdown-section
      const section = document.querySelector('.markdown-section');
      if (!section) return;
      const children = section.children;
      for (const child of children) {
        child.classList.add('canvas-reveal');
        this.observer.observe(child);
      }
    }
  }

  // ================================================================
  // 4. CODE BLOCK GLOW EFFECT
  // ================================================================
  class CodeBlockGlow {
    constructor() {
      this.canvas = null;
      this.ctx = null;
      this.glows = [];
      this.running = false;
      this.raf = 0;
    }

    attach() {
      const blocks = document.querySelectorAll('pre[class*="language-"], pre code');
      blocks.forEach((block) => {
        if (block.dataset.canvasGlow) return;
        block.dataset.canvasGlow = '1';

        block.addEventListener('mousemove', (e) => {
          const rect = block.getBoundingClientRect();
          const x = e.clientX - rect.left;
          const y = e.clientY - rect.top;
          block.style.setProperty('--glow-x', x + 'px');
          block.style.setProperty('--glow-y', y + 'px');
          block.style.setProperty('--glow-opacity', '1');
        });

        block.addEventListener('mouseleave', () => {
          block.style.setProperty('--glow-opacity', '0');
        });
      });
    }
  }

  // ================================================================
  // 5. READING PROGRESS BAR
  // ================================================================
  class ReadingProgress {
    constructor() {
      this.bar = null;
    }

    ensure() {
      if (this.bar) return;
      this.bar = document.createElement('div');
      this.bar.id = 'reading-progress';
      this.bar.style.cssText = `
        position: fixed;
        top: 0; left: 0;
        height: 3px;
        width: 0%;
        background: linear-gradient(90deg, #667eea, #764ba2, #f050b4);
        z-index: 9999;
        transition: width 0.1s ease;
        box-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
      `;
      document.body.appendChild(this.bar);
    }

    update() {
      const content = document.querySelector('.markdown-section');
      if (!content) return;
      const rect = content.getBoundingClientRect();
      const scrollable = rect.height - window.innerHeight;
      if (scrollable <= 0) {
        this.bar.style.width = '100%';
        return;
      }
      const scrolled = Math.max(0, -rect.top);
      const progress = Math.min(100, (scrolled / scrollable) * 100);
      this.bar.style.width = progress + '%';
    }
  }

  // ================================================================
  // MAIN CONTROLLER
  // ================================================================
  const App = {
    fluidGradient: null,
    particleTrail: null,
    pageReveal: null,
    codeGlow: null,
    readingProgress: null,
    effectsEnabled: true,
    mouseThrottle: 0,

    init() {
      // Check for reduced motion preference
      const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
      if (reducedMotion) {
        this.effectsEnabled = false;
      }

      // Check if effects have been toggled off
      const savedPref = localStorage.getItem('canvas-effects-disabled');
      if (savedPref === 'true') {
        this.effectsEnabled = false;
      }

      this.setupGlobalMouseTracking();
      this.setupCoverEffect();
      this.setupContentEffects();
      this.addEffectStyles();
    },

    setupGlobalMouseTracking() {
      document.addEventListener('mousemove', (e) => {
        const now = performance.now();
        if (now - this.mouseThrottle < 16) return;
        this.mouseThrottle = now;

        // Cover fluid
        if (this.fluidGradient && this.fluidGradient.running) {
          this.fluidGradient.onMove(e.clientX, e.clientY);
        }

        // Particle trail
        if (this.particleTrail && this.particleTrail.running && this.effectsEnabled) {
          // Only spawn particles over content, not sidebar
          if (e.clientX > 280) {
            this.particleTrail.spawn(e.clientX, e.clientY);
          }
        }
      }, { passive: true });

      // Touch support
      document.addEventListener('touchmove', (e) => {
        if (e.touches.length > 0) {
          const t = e.touches[0];
          if (this.particleTrail && this.particleTrail.running && this.effectsEnabled) {
            if (t.clientX > 280) {
              this.particleTrail.spawn(t.clientX, t.clientY);
            }
          }
        }
      }, { passive: true });
    },

    setupCoverEffect() {
      const checkCover = () => {
        const cover = document.querySelector('.cover.show') || document.querySelector('.cover');
        if (!cover || !cover.classList.contains('show')) return;

        // If canvas already exists and running, do nothing
        if (this.fluidGradient && this.fluidGradient.running) return;

        // Remove any stale init flag if effects are now enabled
        if (this.effectsEnabled) {
          delete cover.dataset.canvasInit;
        }

        if (cover.dataset.canvasInit) return;
        cover.dataset.canvasInit = '1';

        if (!this.effectsEnabled) return;

        const canvas = document.createElement('canvas');
        canvas.id = 'fluid-canvas';
        canvas.style.cssText = 'position:absolute;top:0;left:0;width:100%;height:100%;z-index:-1;pointer-events:none;';
        cover.style.position = 'relative';
        cover.insertBefore(canvas, cover.firstChild);

        this.fluidGradient = new FluidGradient(canvas);
        this.fluidGradient.start();
      };
      checkCover();
      // Re-check on route changes
      this._checkCover = checkCover;
    },

    setupContentEffects() {
      const initContent = () => {
        // Only skip if cover is actually visible (has 'show' class)
        const visibleCover = document.querySelector('.cover.show');
        if (visibleCover) return;

        // Particle trail
        if (this.effectsEnabled && !this.particleTrail) {
          this.particleTrail = new ParticleTrail();
          this.particleTrail.start();
        } else if (this.particleTrail && !this.particleTrail.running && this.effectsEnabled) {
          this.particleTrail.start();
        }

        // Page reveal
        this.pageReveal = new PageReveal();
        setTimeout(() => this.pageReveal.observe(), 100);

        // Code block glow
        if (!this.codeGlow) {
          this.codeGlow = new CodeBlockGlow();
        }
        this.codeGlow.attach();

        // Reading progress
        this.readingProgress = new ReadingProgress();
        this.readingProgress.ensure();
      };

      this._initContent = initContent;
    },

    addEffectStyles() {
      if (document.getElementById('canvas-effect-styles')) return;
      const style = document.createElement('style');
      style.id = 'canvas-effect-styles';
      style.textContent = `
        /* Page reveal animation */
        .canvas-reveal {
          opacity: 0;
          transform: translateY(20px);
          transition: opacity 0.5s ease, transform 0.5s ease;
        }
        .canvas-revealed {
          opacity: 1 !important;
          transform: translateY(0) !important;
        }

        /* Code block glow */
        pre[class*="language-"], pre code {
          position: relative;
          --glow-x: 50%;
          --glow-y: 50%;
          --glow-opacity: 0;
        }
        pre[class*="language-"]::after,
        pre[data-lang]::after {
          content: '';
          position: absolute;
          inset: 0;
          pointer-events: none;
          background: radial-gradient(
            200px circle at var(--glow-x) var(--glow-y),
            rgba(102, 126, 234, calc(var(--glow-opacity) * 0.15)),
            transparent 70%
          );
          transition: --glow-opacity 0.3s ease;
          border-radius: inherit;
        }

        /* Sidebar link hover ripple */
        .sidebar a {
          position: relative;
          overflow: hidden;
        }
        .sidebar a::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          width: 0;
          height: 2px;
          background: var(--theme-color);
          transition: width 0.2s ease;
          transform: translateY(-50%);
        }
        .sidebar a:hover::before {
          width: 3px;
          border-radius: 2px;
        }
        .sidebar a.active::before {
          width: 3px;
          border-radius: 2px;
        }

        /* Smooth table hover */
        .markdown-section tbody tr {
          transition: background 0.15s ease;
        }
        .markdown-section tbody tr:hover {
          background: var(--sidebar-item-active-background) !important;
        }

        /* Details expand animation */
        .markdown-section details summary {
          transition: color 0.2s;
        }
        .markdown-section details[open] summary {
          animation: detailsOpen 0.2s ease;
        }
        @keyframes detailsOpen {
          from { opacity: 0.5; }
          to { opacity: 1; }
        }

        /* Smooth anchor scroll */
        html {
          scroll-behavior: smooth;
        }

        /* Content fade-in on route change */
        .content {
          animation: contentFadeIn 0.3s ease;
        }
        @keyframes contentFadeIn {
          from { opacity: 0; transform: translateY(8px); }
          to { opacity: 1; transform: translateY(0); }
        }

        /* Heading anchor links appear on hover */
        .markdown-section h1[id]::before,
        .markdown-section h2[id]::before,
        .markdown-section h3[id]::before {
          content: '#';
          position: absolute;
          margin-left: -1.2em;
          color: var(--theme-color);
          opacity: 0;
          transition: opacity 0.2s;
        }
        .markdown-section h1[id]:hover::before,
        .markdown-section h2[id]:hover::before,
        .markdown-section h3[id]:hover::before {
          opacity: 0.5;
        }
        .markdown-section h1[id],
        .markdown-section h2[id],
        .markdown-section h3[id] {
          position: relative;
          cursor: pointer;
        }

        /* Effects toggle button */
        .effects-toggle {
          position: fixed;
          bottom: 1.2rem;
          right: 4rem;
          z-index: 200;
          width: 44px;
          height: 44px;
          border-radius: 50%;
          border: 2px solid var(--table-border-color);
          background: var(--background-color);
          color: var(--text-color);
          cursor: pointer;
          font-size: 1.1rem;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 12px rgba(0,0,0,0.1);
          transition: all 0.2s ease;
        }
        .effects-toggle:hover {
          border-color: var(--theme-color);
          transform: scale(1.08);
        }
      `;
      document.head.appendChild(style);
    },

    onRouteChange() {
      // Handle cover start/stop
      if (this._checkCover) this._checkCover();

      // Handle content
      if (this._initContent) this._initContent();

      // Update reading progress
      if (this.readingProgress) this.readingProgress.update();
    },

    toggleEffects() {
      this.effectsEnabled = !this.effectsEnabled;
      localStorage.setItem('canvas-effects-disabled', (!this.effectsEnabled).toString());

      if (this.effectsEnabled) {
        // Re-init cover effect
        if (this._checkCover) this._checkCover();
        // Re-init content effects
        if (this._initContent) this._initContent();
        if (this.particleTrail) this.particleTrail.start();
        const canvas = document.getElementById('fluid-canvas');
        if (canvas && this.fluidGradient) this.fluidGradient.start();
      } else {
        if (this.particleTrail) this.particleTrail.stop();
        if (this.fluidGradient) this.fluidGradient.stop();
        // Clear particles
        if (this.particleTrail) this.particleTrail.particles = [];
        // Remove fluid canvas
        const fc = document.getElementById('fluid-canvas');
        if (fc) fc.remove();
      }
    },
  };

  // Initialize on load
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => App.init());
  } else {
    App.init();
  }

  // Export for Docsify plugin integration
  window.CanvasEffects = App;

  // Scroll handler for reading progress
  window.addEventListener('scroll', () => {
    if (App.readingProgress) App.readingProgress.update();
  }, { passive: true });
})();
