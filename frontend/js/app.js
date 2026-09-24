/**
 * E-Commerce Product Entity Resolution and Matching Platform - Interactive Application Logic
 * Implements real-time filtering, multi-seller resolution views,
 * INR price comparison matrix, and live string-matching algorithms.
 */

// Application State
const state = {
  currentTab: 'catalog',
  activeCategory: 'All',
  activeBrand: 'All',
  searchQuery: '',
  sortBy: 'featured',
  wishlist: new Set(),
  compareList: new Set()
};

// DOM References
let dom = {};

document.addEventListener('DOMContentLoaded', () => {
  initDomReferences();
  initEventListeners();
  updateMetricsDisplay();
  renderCurrentView();
  initSandbox();
});

function initDomReferences() {
  dom = {
    // Nav tabs
    tabBtns: document.querySelectorAll('.tab-btn'),
    viewPanes: document.querySelectorAll('.view-pane'),
    // Filters & Search
    categoryPills: document.querySelectorAll('.category-pill'),
    brandSelect: document.getElementById('brandSelect'),
    sortSelect: document.getElementById('sortSelect'),
    searchInput: document.getElementById('mainSearchInput'),
    searchBtn: document.getElementById('searchBtn'),
    resultsCount: document.getElementById('resultsCountText'),
    // Views containers
    catalogGrid: document.getElementById('productCatalogGrid'),
    duplicatesList: document.getElementById('duplicatesGroupList'),
    matrixTableBody: document.getElementById('matrixTableBody'),
    // Stats
    totalSavingsDisplay: document.getElementById('heroTotalSavings'),
    resolvedCountDisplay: document.getElementById('heroResolvedCount'),
    sellersCountDisplay: document.getElementById('heroSellersCount'),
    // Modal
    modalOverlay: document.getElementById('sellerModalOverlay'),
    modalCloseBtn: document.getElementById('modalCloseBtn'),
    modalProductImg: document.getElementById('modalProductImg'),
    modalProductTitle: document.getElementById('modalProductTitle'),
    modalProductSubtitle: document.getElementById('modalProductSubtitle'),
    modalSellerList: document.getElementById('modalSellerList'),
    // Badges
    wishlistCountBadge: document.getElementById('wishlistCountBadge')
  };
}

function initEventListeners() {
  // Tab switching
  dom.tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetTab = btn.getAttribute('data-tab');
      switchTab(targetTab);
    });
  });

  // Category filter pills
  dom.categoryPills.forEach(pill => {
    pill.addEventListener('click', () => {
      dom.categoryPills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      state.activeCategory = pill.getAttribute('data-category');
      renderCurrentView();
    });
  });

  // Brand dropdown
  if (dom.brandSelect) {
    dom.brandSelect.addEventListener('change', (e) => {
      state.activeBrand = e.target.value;
      renderCurrentView();
    });
  }

  // Sort dropdown
  if (dom.sortSelect) {
    dom.sortSelect.addEventListener('change', (e) => {
      state.sortBy = e.target.value;
      renderCurrentView();
    });
  }

  // Live search input
  if (dom.searchInput) {
    dom.searchInput.addEventListener('input', (e) => {
      state.searchQuery = e.target.value.trim().toLowerCase();
      renderCurrentView();
    });
  }

  // Modal close handlers
  if (dom.modalCloseBtn) {
    dom.modalCloseBtn.addEventListener('click', closeModal);
  }
  if (dom.modalOverlay) {
    dom.modalOverlay.addEventListener('click', (e) => {
      if (e.target === dom.modalOverlay) closeModal();
    });
  }
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && dom.modalOverlay.classList.contains('open')) {
      closeModal();
    }
  });
}

function switchTab(tabName) {
  state.currentTab = tabName;
  dom.tabBtns.forEach(btn => {
    btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
  });
  dom.viewPanes.forEach(pane => {
    pane.classList.toggle('active', pane.id === `${tabName}ViewPane`);
  });
  renderCurrentView();
}

function updateMetricsDisplay() {
  const metrics = computeCatalogMetrics();
  if (dom.totalSavingsDisplay) dom.totalSavingsDisplay.textContent = formatINR(metrics.totalPotentialSavings);
  if (dom.resolvedCountDisplay) dom.resolvedCountDisplay.textContent = metrics.totalListings;
  if (dom.sellersCountDisplay) dom.sellersCountDisplay.textContent = metrics.sellersCount;
}

// Filter and Sort Data
function getFilteredCanonicalProducts() {
  return CANONICAL_CATALOG.filter(item => {
    // Category match
    const categoryMatch = (state.activeCategory === 'All') || (item.category === state.activeCategory);
    // Brand match
    const brandMatch = (state.activeBrand === 'All') || (item.brand === state.activeBrand);
    // Search query match (in canonical name, subtitle, brand, or raw listing titles)
    let searchMatch = true;
    if (state.searchQuery) {
      const q = state.searchQuery;
      const listings = item.listings.map(lid => RAW_LISTINGS.find(l => l.listingId === lid));
      const hasListingMatch = listings.some(l => l.title.toLowerCase().includes(q) || l.sellerName.toLowerCase().includes(q));
      searchMatch = item.canonicalName.toLowerCase().includes(q) ||
                          item.subtitle.toLowerCase().includes(q) ||
                          item.brand.toLowerCase().includes(q) ||
                          hasListingMatch;
    }
    return categoryMatch && brandMatch && searchMatch;
  }).sort((a, b) => {
    const pricesA = a.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id).price);
    const minA = Math.min(...pricesA);
    const maxA = Math.max(...pricesA);
    const savingsA = maxA - minA;

    const pricesB = b.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id).price);
    const minB = Math.min(...pricesB);
    const maxB = Math.max(...pricesB);
    const savingsB = maxB - minB;

    switch (state.sortBy) {
      case 'price-low': return minA - minB;
      case 'price-high': return maxB - maxA;
      case 'savings-high': return savingsB - savingsA;
      case 'sellers-high': return b.listings.length - a.listings.length;
      default: return 0;
    }
  });
}

function renderCurrentView() {
  const items = getFilteredCanonicalProducts();
  if (dom.resultsCount) {
    dom.resultsCount.innerHTML = `Showing <strong>${items.length}</strong> resolved electronics (${state.activeCategory})`;
  }

  if (state.currentTab === 'catalog') {
    renderStorefrontCatalog(items);
  } else if (state.currentTab === 'duplicates') {
    renderDuplicateGroups(items);
  } else if (state.currentTab === 'matrix') {
    renderPriceMatrix(items);
  }
}

// -------------------------------------------------------------
// View 1: Render Storefront Product Grid
// -------------------------------------------------------------
function renderStorefrontCatalog(items) {
  if (!dom.catalogGrid) return;

  if (items.length === 0) {
    dom.catalogGrid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 60px 20px;">
        <h3 style="font-size: 20px; color: var(--text-primary); margin-bottom: 8px;">No matching electronics found</h3>
        <p style="color: var(--text-muted); font-size: 14px;">Try searching for "iPhone", "Sony", "Laptop", or clear filters.</p>
      </div>
    `;
    return;
  }

  dom.catalogGrid.innerHTML = items.map(item => {
    const rawOffers = item.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id));
    const prices = rawOffers.map(l => l.price);
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);
    const savings = maxPrice - minPrice;
    const hasSavings = savings > 1;

    const formattedPrice = formatINR(minPrice);
    const formattedSavings = formatINR(savings);
    const isWishlisted = state.wishlist.has(item.id);

    return `
      <div class="product-card" data-id="${item.id}">
        <div class="card-top-badges">
          <span class="card-tag ${rawOffers.length > 1 ? 'seller-count' : ''}">${rawOffers.length > 1 ? `${rawOffers.length} Verified Sellers` : item.badge}</span>
          <button class="btn-wishlist ${isWishlisted ? 'active' : ''}" onclick="toggleWishlist('${item.id}', event)" title="Save to wishlist">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="${isWishlisted ? '#e11d48' : 'none'}" stroke="${isWishlisted ? '#e11d48' : 'currentColor'}" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
          </button>
        </div>

        <div class="product-media-container" onclick="openSellerModal('${item.id}')">
          <img src="${item.image}" alt="${item.canonicalName}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600&auto=format&fit=crop&q=80'" />
        </div>

        <div class="product-info">
          <div class="product-header-row">
            <h4 class="product-title" onclick="openSellerModal('${item.id}')" style="cursor:pointer;">${item.canonicalName}</h4>
            <div class="product-price">${formattedPrice}</div>
          </div>

          <p class="product-subtitle">${item.subtitle}</p>

          <div class="product-specs-pill">${item.highlightSpecs}</div>

          <div class="product-rating-row">
            <div class="rating-stars">★★★★★</div>
            <span class="rating-count">(${item.reviewCount})</span>
            ${hasSavings ? `<span class="price-variance-badge">Save up to ${formattedSavings}</span>` : ''}
          </div>

          <button class="card-action-btn" onclick="openSellerModal('${item.id}')">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z"></path>
            </svg>
            Compare ${rawOffers.length} Seller Offers
          </button>
        </div>
      </div>
    `;
  }).join('');
}

// -------------------------------------------------------------
// View 2: Render Duplicate Resolution Groups
// -------------------------------------------------------------
function renderDuplicateGroups(items) {
  if (!dom.duplicatesList) return;

  dom.duplicatesList.innerHTML = items.map(item => {
    const rawOffers = item.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id));
    const minPrice = Math.min(...rawOffers.map(l => l.price));

    return `
      <div class="canonical-group-card">
        <div class="group-card-header">
          <div class="group-title-block">
            <img class="group-thumbnail" src="${item.image}" alt="${item.canonicalName}" />
            <div>
              <span class="brand-tag">${item.brand} • ${item.category}</span>
              <h4>${item.canonicalName}</h4>
            </div>
          </div>
          <div class="group-badge-meta">
            <span class="confidence-badge">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
              ${rawOffers.length > 1 ? `${rawOffers.length} Listings Merged` : 'Single Listing Catalogued'}
            </span>
            <button class="cta-pill-btn" onclick="openSellerModal('${item.id}')" style="padding: 6px 16px; font-size: 13px;">View Price Delta</button>
          </div>
        </div>

        <div class="raw-listings-list">
          ${rawOffers.map(listing => {
            const isCheapest = listing.price === minPrice;
            const seller = SELLERS[listing.sellerId];
            return `
              <div class="raw-listing-row ${isCheapest ? 'cheapest-row' : ''}">
                <span class="raw-listing-id">${listing.listingId}</span>
                <span class="raw-seller-name">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                  ${listing.sellerName}
                </span>
                <span class="raw-title-diff" title="${listing.description}">
                  "${listing.title}"
                </span>
                <span class="raw-price">${formatINR(listing.price)}</span>
                <span class="raw-tag-badge ${isCheapest ? 'tag-best-deal' : 'tag-match'}">
                  ${isCheapest ? '★ Lowest' : 'Matched'}
                </span>
              </div>
            `;
          }).join('')}
        </div>
      </div>
    `;
  }).join('');
}

// -------------------------------------------------------------
// View 3: Render Price Comparison Matrix Table
// -------------------------------------------------------------
function renderPriceMatrix(items) {
  if (!dom.matrixTableBody) return;

  dom.matrixTableBody.innerHTML = items.map(item => {
    const rawOffers = item.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id));
    const prices = rawOffers.map(l => l.price);
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);
    const savings = maxPrice - minPrice;
    const cheapestListing = rawOffers.find(l => l.price === minPrice);

    return `
      <tr>
        <td>
          <div class="matrix-prod-cell">
            <img src="${item.image}" alt="${item.canonicalName}" />
            <div>
              <div class="matrix-prod-name">${item.canonicalName}</div>
              <div class="matrix-prod-cat">${item.brand} • ${item.category}</div>
            </div>
          </div>
        </td>
        <td>
          <span class="matrix-price-cell cheapest">
            ${formatINR(minPrice)}
          </span>
        </td>
        <td>
          <strong>${cheapestListing.sellerName}</strong>
          <div style="font-size:11px; color:var(--text-muted);">${SELLERS[cheapestListing.sellerId]?.shipping || 'Verified'}</div>
        </td>
        <td>
          <span class="matrix-price-cell">${formatINR(maxPrice)}</span>
        </td>
        <td>
          <span class="matrix-savings-cell">${savings > 0 ? `+${formatINR(savings)}` : '—'}</span>
        </td>
        <td>
          <div style="display:flex; gap:6px; flex-wrap:wrap;">
            ${rawOffers.map(l => `
              <span style="font-size:11px; background:#f3f4f6; padding:3px 8px; border-radius:4px;">
                ${l.sellerName}: <strong>${formatINR(l.price)}</strong>
              </span>
            `).join('')}
          </div>
        </td>
        <td>
          <button class="cta-pill-btn" onclick="openSellerModal('${item.id}')" style="padding: 6px 14px; font-size: 12px;">Compare</button>
        </td>
      </tr>
    `;
  }).join('');
}

// -------------------------------------------------------------
// Modal Dialog for Multi-Seller Breakdown
// -------------------------------------------------------------
function openSellerModal(canonicalId) {
  const item = CANONICAL_CATALOG.find(c => c.id === canonicalId);
  if (!item) return;

  const rawOffers = item.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id));
  rawOffers.sort((a, b) => a.price - b.price);
  const minPrice = rawOffers[0].price;

  dom.modalProductImg.src = item.image;
  dom.modalProductTitle.textContent = item.canonicalName;
  dom.modalProductSubtitle.textContent = `${item.brand} • ${item.category} • ${item.highlightSpecs}`;

  dom.modalSellerList.innerHTML = rawOffers.map((listing, index) => {
    const isCheapest = listing.price === minPrice;
    const seller = SELLERS[listing.sellerId] || { rating: 4.8, reviews: 1000, shipping: "Standard Delivery" };
    return `
      <div class="seller-offer-row ${isCheapest ? 'best-price' : ''}">
        <div class="offer-seller-info">
          <div class="offer-seller-title">
            ${listing.sellerName}
            ${isCheapest ? '<span class="card-tag" style="background:#003d29; color:#fff;">Best Price Deal</span>' : ''}
          </div>
          <div class="offer-seller-meta">
            ★ ${seller.rating} (${seller.reviews} reviews) • <span style="color:#059669;">${seller.shipping}</span>
          </div>
          <div class="offer-listing-title">Listing Title: "${listing.title}"</div>
        </div>

        <div class="offer-price-action">
          <div class="offer-price">${formatINR(listing.price)}</div>
          <button class="btn-buy-seller" onclick="showPurchaseFeedback('${listing.sellerName}', ${listing.price})">
            Select Seller
          </button>
        </div>
      </div>
    `;
  }).join('');

  dom.modalOverlay.classList.add('open');
  document.body.style.overflow = 'hidden';
}

function closeModal() {
  if (dom.modalOverlay) {
    dom.modalOverlay.classList.remove('open');
  }
  document.body.style.overflow = '';
}

function showPurchaseFeedback(sellerName, price) {
  alert(`Great choice! Redirecting to verified checkout with ${sellerName} at the best price of ${formatINR(price)}.`);
  closeModal();
}

function toggleWishlist(canonicalId, event) {
  if (event) event.stopPropagation();
  if (state.wishlist.has(canonicalId)) {
    state.wishlist.delete(canonicalId);
  } else {
    state.wishlist.add(canonicalId);
  }
  if (dom.wishlistCountBadge) {
    dom.wishlistCountBadge.textContent = state.wishlist.size;
  }
  renderCurrentView();
}

// -------------------------------------------------------------
// View 4: Live Resolution Playground / Sandbox Logic
// -------------------------------------------------------------
const PRESETS = [
  {
    name: "iPhone 15 Variation (High Match)",
    s1: "Apple iPhone 15 128GB Black",
    s2: "iPhone 15 Apple 128 GB - Midnight Black"
  },
  {
    name: "Sony ANC Headphones (Different Seller Format)",
    s1: "Sony WH-1000XM5 Wireless Headphones",
    s2: "Sony WH1000XM5/B Wireless Over-Ear Headphone Active Noise Cancelling"
  },
  {
    name: "Samsung Galaxy S24 (Minor Order Shift)",
    s1: "Samsung Galaxy S24 256GB Onyx Black",
    s2: "Galaxy S24 Samsung 256 GB Smartphone 5G"
  },
  {
    name: "Completely Different Electronics (No Match)",
    s1: "Apple MacBook Pro 16-inch M3 Max 36GB 1TB",
    s2: "Sony PlayStation 5 PS5 Slim Console Digital Edition 1TB"
  }
];

function initSandbox() {
  const presetSelect = document.getElementById('sandboxPresetSelect');
  const str1 = document.getElementById('sandboxStr1');
  const str2 = document.getElementById('sandboxStr2');
  const runBtn = document.getElementById('btnRunSandboxMatch');

  if (!presetSelect || !str1 || !str2 || !runBtn) return;

  // Populate presets
  presetSelect.innerHTML = PRESETS.map((p, idx) => `
    <option value="${idx}">${p.name}</option>
  `).join('');

  // Handle preset change
  presetSelect.addEventListener('change', (e) => {
    const p = PRESETS[e.target.value];
    str1.value = p.s1;
    str2.value = p.s2;
    runSandboxMatch();
  });

  // Default initial values
  str1.value = PRESETS[0].s1;
  str2.value = PRESETS[0].s2;

  runBtn.addEventListener('click', runSandboxMatch);
  runSandboxMatch();
}

// Core String Matching Algorithms for Sandbox
function computeLevenshtein(a, b) {
  const matrix = [];
  for (let i = 0; i <= b.length; i++) {
    matrix[i] = [i];
  }
  for (let j = 0; j <= a.length; j++) {
    matrix[0][j] = j;
  }
  for (let i = 1; i <= b.length; i++) {
    for (let j = 1; j <= a.length; j++) {
      if (b.charAt(i - 1).toLowerCase() === a.charAt(j - 1).toLowerCase()) {
        matrix[i][j] = matrix[i - 1][j - 1];
      } else {
        matrix[i][j] = Math.min(
          matrix[i - 1][j - 1] + 1, // substitution
          matrix[i][j - 1] + 1,     // insertion
          matrix[i - 1][j] + 1      // deletion
        );
      }
    }
  }
  return matrix[b.length][a.length];
}

function computeTokenJaccard(s1, s2) {
  const tokenize = s => s.toLowerCase().replace(/[^a-z0-9 ]/g, ' ').split(/\s+/).filter(Boolean);
  const t1 = new Set(tokenize(s1));
  const t2 = new Set(tokenize(s2));

  const intersection = new Set([...t1].filter(x => t2.has(x)));
  const union = new Set([...t1, ...t2]);

  return union.size === 0 ? 0 : (intersection.size / union.size);
}

function computeSubstringMatch(s1, s2) {
  const s1Norm = s1.toLowerCase().replace(/[^a-z0-9]/g, '');
  const s2Norm = s2.toLowerCase().replace(/[^a-z0-9]/g, '');
  if (!s1Norm || !s2Norm) return 0;
  if (s1Norm.includes(s2Norm) || s2Norm.includes(s1Norm)) return 1.0;

  // Substring overlap ratio
  let maxCommon = 0;
  for (let i = 0; i < s1Norm.length; i++) {
    for (let j = i + 3; j <= s1Norm.length; j++) {
      const sub = s1Norm.substring(i, j);
      if (s2Norm.includes(sub) && sub.length > maxCommon) {
        maxCommon = sub.length;
      }
    }
  }
  return Math.min(1, (maxCommon * 2) / (s1Norm.length + s2Norm.length));
}

function runSandboxMatch() {
  const s1 = document.getElementById('sandboxStr1').value.trim();
  const s2 = document.getElementById('sandboxStr2').value.trim();

  const jaccardScore = computeTokenJaccard(s1, s2);
  const dist = computeLevenshtein(s1, s2);
  const maxLen = Math.max(s1.length, s2.length);
  const editScore = maxLen === 0 ? 1 : Math.max(0, 1 - (dist / maxLen));
  const subScore = computeSubstringMatch(s1, s2);

  // Weighted score (40% Token Overlap, 35% Edit Sim, 25% Substring)
  const compositeScore = Math.round((jaccardScore * 0.40 + editScore * 0.35 + subScore * 0.25) * 100);
  const isMatch = compositeScore >= 68;

  // Update UI
  const scoreNum = document.getElementById('sandboxCompositeScore');
  const verdict = document.getElementById('sandboxVerdictBadge');
  const barToken = document.getElementById('sandboxBarToken');
  const barEdit = document.getElementById('sandboxBarEdit');
  const barSub = document.getElementById('sandboxBarSub');
  const valToken = document.getElementById('sandboxValToken');
  const valEdit = document.getElementById('sandboxValEdit');
  const valSub = document.getElementById('sandboxValSub');

  if (scoreNum) scoreNum.textContent = `${compositeScore}%`;
  if (verdict) {
    if (isMatch) {
      verdict.className = 'verdict-badge verdict-match';
      verdict.textContent = 'MATCH DETECTED: RESOLVED TO SAME PRODUCT';
    } else {
      verdict.className = 'verdict-badge verdict-nomatch';
      verdict.textContent = 'DISTINCT ENTITIES: SEPARATE PRODUCTS';
    }
  }

  if (barToken) barToken.style.width = `${Math.round(jaccardScore * 100)}%`;
  if (valToken) valToken.textContent = `${Math.round(jaccardScore * 100)}%`;

  if (barEdit) barEdit.style.width = `${Math.round(editScore * 100)}%`;
  if (valEdit) valEdit.textContent = `${Math.round(editScore * 100)}% (Distance: ${dist})`;

  if (barSub) barSub.style.width = `${Math.round(subScore * 100)}%`;
  if (valSub) valSub.textContent = `${Math.round(subScore * 100)}%`;
}
