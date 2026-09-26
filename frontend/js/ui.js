/**
 * ENTITY.RESOLVE — UI Enhancement Layer
 * Handles: particles, scroll reveals, navigation, hero viz, ring score
 * Does NOT modify any data or matching logic in app.js / data.js
 */

/* ======================================================
   PARTICLE BACKGROUND (hero canvas)
   ====================================================== */
(function initParticles() {
  const canvas = document.getElementById('heroParticles');
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  let W, H, particles = [];

  function resize() {
    W = canvas.width  = window.innerWidth;
    H = canvas.height = window.innerHeight;
  }

  resize();
  window.addEventListener('resize', resize);

  const COUNT = Math.min(60, Math.floor(window.innerWidth / 22));

  for (let i = 0; i < COUNT; i++) {
    particles.push({
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      r: Math.random() * 1.4 + 0.4,
      dx: (Math.random() - 0.5) * 0.3,
      dy: (Math.random() - 0.5) * 0.3,
      alpha: Math.random() * 0.4 + 0.1
    });
  }

  function draw() {
    ctx.clearRect(0, 0, W, H);

    // Draw particles
    particles.forEach(p => {
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(139, 92, 246, ${p.alpha})`;
      ctx.fill();
      p.x += p.dx;
      p.y += p.dy;
      if (p.x < 0) p.x = W;
      if (p.x > W) p.x = 0;
      if (p.y < 0) p.y = H;
      if (p.y > H) p.y = 0;
    });

    // Draw connections between nearby particles
    particles.forEach((a, i) => {
      particles.slice(i + 1).forEach(b => {
        const dx = a.x - b.x, dy = a.y - b.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 120) {
          ctx.beginPath();
          ctx.moveTo(a.x, a.y);
          ctx.lineTo(b.x, b.y);
          ctx.strokeStyle = `rgba(139, 92, 246, ${0.08 * (1 - dist / 120)})`;
          ctx.lineWidth = 0.6;
          ctx.stroke();
        }
      });
    });

    requestAnimationFrame(draw);
  }

  draw();
})();

/* ======================================================
   SCROLL REVEAL (IntersectionObserver)
   ====================================================== */
(function initScrollReveal() {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry, idx) => {
      if (entry.isIntersecting) {
        const el = entry.target;
        const delay = parseFloat(el.style.animationDelay || '0') * 1000;
        setTimeout(() => {
          el.classList.add('revealed');
        }, delay);
        observer.unobserve(el);
      }
    });
  }, {
    threshold: 0.12,
    rootMargin: '0px 0px -40px 0px'
  });

  document.querySelectorAll('.scroll-reveal').forEach(el => {
    observer.observe(el);
  });
})();

/* ======================================================
   STICKY HEADER SCROLL EFFECT
   ====================================================== */
(function initStickyHeader() {
  const header = document.getElementById('app-header');
  if (!header) return;

  let ticking = false;
  window.addEventListener('scroll', () => {
    if (!ticking) {
      requestAnimationFrame(() => {
        if (window.scrollY > 20) {
          header.classList.add('scrolled');
        } else {
          header.classList.remove('scrolled');
        }
        ticking = false;
      });
      ticking = true;
    }
  });
})();

/* ======================================================
   HAMBURGER / MOBILE NAV
   ====================================================== */
window.closeMobileNav = function() {
  const nav = document.getElementById('mobileNav');
  const backdrop = document.getElementById('mobileNavBackdrop');
  const btn = document.getElementById('hamburgerBtn');
  if (nav) nav.classList.remove('open');
  if (backdrop) backdrop.classList.remove('open');
  if (btn) btn.setAttribute('aria-expanded', 'false');
};

(function initHamburger() {
  const btn = document.getElementById('hamburgerBtn');
  const nav = document.getElementById('mobileNav');
  const backdrop = document.getElementById('mobileNavBackdrop');
  if (!btn || !nav) return;

  btn.addEventListener('click', () => {
    const isOpen = nav.classList.contains('open');
    if (isOpen) {
      nav.classList.remove('open');
      if (backdrop) backdrop.classList.remove('open');
      btn.setAttribute('aria-expanded', 'false');
    } else {
      nav.classList.add('open');
      if (backdrop) backdrop.classList.add('open');
      btn.setAttribute('aria-expanded', 'true');
    }
  });
})();

/* ======================================================
   SCROLL TO CATALOG (App section)
   ====================================================== */
window.scrollToCatalog = function() {
  const el = document.getElementById('catalog-section');
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

window.scrollToMatcher = function() {
  const el = document.getElementById('resolution-section');
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    setTimeout(() => {
      if (typeof switchTab === 'function') {
        switchTab('playground');
      }
    }, 600);
  }
};

/* ======================================================
   SCORE RING UPDATE (SVG circular progress)
   Updates the circular ring in the sandbox panel
   Called after runSandboxMatch from app.js
   ====================================================== */
(function patchSandboxScore() {
  // Observe changes to the text content of the score value
  const scoreEl = document.getElementById('sandboxCompositeScore');
  if (!scoreEl) return;

  const ringFill = document.getElementById('scoreRingFill');
  if (!ringFill) return;

  const CIRCUMFERENCE = 150.8; // 2 * π * r where r=24

  function updateRing(pct) {
    const offset = CIRCUMFERENCE - (pct / 100) * CIRCUMFERENCE;
    ringFill.style.strokeDashoffset = offset;

    // Color based on score
    if (pct >= 68) {
      ringFill.style.stroke = '#10b981'; // success green
    } else if (pct >= 40) {
      ringFill.style.stroke = '#f59e0b'; // amber
    } else {
      ringFill.style.stroke = '#f43f5e'; // danger red
    }
  }

  // Initial render
  const initPct = parseInt(scoreEl.textContent) || 95;
  updateRing(initPct);

  // Observe mutations to re-sync ring
  const observer = new MutationObserver(() => {
    const pct = parseInt(scoreEl.textContent) || 0;
    updateRing(pct);

    // Also update verdict badge color on DOM update
    const verdict = document.getElementById('sandboxVerdictBadge');
    if (verdict) {
      verdict.classList.toggle('verdict-match', pct >= 68);
      verdict.classList.toggle('verdict-nomatch', pct < 68);
    }
  });

  observer.observe(scoreEl, { childList: true, characterData: true, subtree: true });
})();

/* ======================================================
   ENTITY VIZ — subtle mouse parallax on hero visual
   ====================================================== */
(function initVizParallax() {
  const viz = document.getElementById('entityViz');
  if (!viz) return;

  document.addEventListener('mousemove', (e) => {
    const cx = window.innerWidth / 2;
    const cy = window.innerHeight / 2;
    const dx = (e.clientX - cx) / cx;
    const dy = (e.clientY - cy) / cy;

    viz.style.transform = `perspective(800px) rotateY(${dx * 4}deg) rotateX(${-dy * 3}deg)`;
  });

  // Reset on mouse leave
  document.addEventListener('mouseleave', () => {
    viz.style.transform = '';
  });
})();

/* ======================================================
   CARD STAGGER ANIMATION
   After catalog renders, animate cards in
   ====================================================== */
(function initCardStagger() {
  const grid = document.getElementById('productCatalogGrid');
  if (!grid) return;

  const cardObserver = new MutationObserver(() => {
    const cards = grid.querySelectorAll('.product-card:not(.staggered)');
    cards.forEach((card, i) => {
      card.classList.add('staggered');
      card.style.opacity = '0';
      card.style.transform = 'translateY(16px)';
      setTimeout(() => {
        card.style.transition = 'opacity 0.4s ease, transform 0.4s cubic-bezier(0.16,1,0.3,1)';
        card.style.opacity = '1';
        card.style.transform = 'translateY(0)';
      }, i * 50);
    });
  });

  cardObserver.observe(grid, { childList: true });
})();

/* ======================================================
   SMOOTH ANCHOR SCROLL FOR NAV LINKS
   ====================================================== */
document.querySelectorAll('a[href^="#"]').forEach(link => {
  link.addEventListener('click', (e) => {
    const href = link.getAttribute('href');
    if (href === '#') return;
    const target = document.querySelector(href);
    if (target) {
      e.preventDefault();
      target.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  });
});

/* ======================================================
   LIVE STATUS INDICATOR — subtle pulse on catalog metrics
   ====================================================== */
(function initLiveStatus() {
  const metrics = [
    document.getElementById('heroResolvedCount'),
    document.getElementById('heroCanonicalCount'),
    document.getElementById('heroSellersCount'),
    document.getElementById('heroTotalSavings')
  ];

  // Add a shimmer pulse every 5s to indicate "live" feel
  setInterval(() => {
    metrics.forEach(el => {
      if (!el) return;
      el.style.transition = 'color 0.3s ease';
      el.style.color = 'rgba(139, 92, 246, 0.9)';
      setTimeout(() => {
        el.style.color = '';
      }, 400);
    });
  }, 6000);
})();
