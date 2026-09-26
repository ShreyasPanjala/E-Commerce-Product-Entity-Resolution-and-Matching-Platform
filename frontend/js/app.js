/**
 * RESOLV • Product Entity Resolution & Price Intelligence
 * Minimalist, high-performance application logic.
 */

// Application State
const state = {
  currentTab: 'catalog',
  activeCategory: 'All',
  activeBrand: 'All',
  searchQuery: '',
  sortBy: 'featured'
};

// DOM Cache
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
    // Navigation
    tabBtns: document.querySelectorAll('.tab-btn'),
    viewPanes: document.querySelectorAll('.view-pane'),

    // Controls
    categoryPills: document.querySelectorAll('.category-pill'),
    brandSelect: document.getElementById('brandSelect'),
    sortSelect: document.getElementById('sortSelect'),
    searchInput: document.getElementById('mainSearchInput'),
    resultsCount: document.getElementById('resultsCountText'),

    // View Containers
    catalogGrid: document.getElementById('productCatalogGrid'),
    duplicatesList: document.getElementById('duplicatesGroupList'),
    matrixTableBody: document.getElementById('matrixTableBody'),

    // Metrics Display
    totalSavingsDisplay: document.getElementById('heroTotalSavings'),
    resolvedCountDisplay: document.getElementById('heroResolvedCount'),
    sellersCountDisplay: document.getElementById('heroSellersCount'),
    canonicalCountDisplay: document.getElementById('heroCanonicalCount'),

    // Modal
    modalOverlay: document.getElementById('sellerModalOverlay'),
    modalCloseBtn: document.getElementById('modalCloseBtn'),
    modalProductImg: document.getElementById('modalProductImg'),
    modalProductTitle: document.getElementById('modalProductTitle'),
    modalProductSubtitle: document.getElementById('modalProductSubtitle'),
    modalSellerList: document.getElementById('modalSellerList')
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
    if (e.key === 'Escape' && dom.modalOverlay && dom.modalOverlay.classList.contains('open')) {
      closeModal();
    }
  });
}

function switchTab(tabName) {
  state.currentTab = tabName;
  dom.tabBtns.forEach(btn => {
    const isTarget = btn.getAttribute('data-tab') === tabName;
    btn.classList.toggle('active', isTarget);
    btn.setAttribute('aria-selected', isTarget ? 'true' : 'false');
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
  if (dom.canonicalCountDisplay) dom.canonicalCountDisplay.textContent = metrics.canonicalCount;
}

// Filter and Sort Data
function getFilteredCanonicalProducts() {
  return CANONICAL_CATALOG.filter(item => {
    const categoryMatch = (state.activeCategory === 'All') || (item.category === state.activeCategory);
    const brandMatch = (state.activeBrand === 'All') || (item.brand === state.activeBrand);
    
    let searchMatch = true;
    if (state.searchQuery) {
      const q = state.searchQuery;
      const listings = item.listings.map(lid => RAW_LISTINGS.find(l => l.listingId === lid));
      const hasListingMatch = listings.some(l => 
        l.title.toLowerCase().includes(q) || 
        l.sellerName.toLowerCase().includes(q) ||
        l.listingId.toLowerCase().includes(q)
      );
      searchMatch = item.canonicalName.toLowerCase().includes(q) ||
                    item.subtitle.toLowerCase().includes(q) ||
                    item.brand.toLowerCase().includes(q) ||
                    item.category.toLowerCase().includes(q) ||
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
    dom.resultsCount.textContent = `${items.length} ${items.length === 1 ? 'device' : 'devices'}`;
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
// View 1: Minimalist Storefront Catalog Grid
// -------------------------------------------------------------
function renderStorefrontCatalog(items) {
  if (!dom.catalogGrid) return;

  if (items.length === 0) {
    dom.catalogGrid.innerHTML = `
      <div class="empty-state">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <h3>No matching electronics found</h3>
        <p>Try searching for a brand like "Apple", "Sony", or reset category filters.</p>
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

    return `
      <article class="product-card" onclick="openSellerModal('${item.id}')" tabindex="0" role="button" aria-label="View offers for ${item.canonicalName}">
        <div class="card-media">
          <img src="${item.image}" alt="${item.canonicalName}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600&auto=format&fit=crop&q=80'" />
          <div class="card-media-tags">
            <span class="pill-sellers">${rawOffers.length} ${rawOffers.length === 1 ? 'Seller' : 'Sellers'}</span>
            ${hasSavings ? `<span class="pill-savings">Save ${formattedSavings}</span>` : ''}
          </div>
        </div>

        <div class="card-body">
          <div class="card-category-brand">${item.brand} • ${item.category}</div>
          <h3 class="card-title">${item.canonicalName}</h3>
          <p class="card-specs">${item.highlightSpecs}</p>
          
          <div class="card-footer">
            <div class="card-price-group">
              <span class="price-prefix">Lowest</span>
              <span class="price-amount">${formattedPrice}</span>
            </div>
            <button class="btn-compare" onclick="openSellerModal('${item.id}'); event.stopPropagation();">
              <span>Compare</span>
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
          </div>
        </div>
      </article>
    `;
  }).join('');
}

// -------------------------------------------------------------
// View 2: Duplicate Resolution Clusters
// -------------------------------------------------------------
function renderDuplicateGroups(items) {
  if (!dom.duplicatesList) return;

  if (items.length === 0) {
    dom.duplicatesList.innerHTML = `
      <div class="empty-state">
        <h3>No matching clusters found</h3>
        <p>Adjust your search filters above.</p>
      </div>
    `;
    return;
  }

  dom.duplicatesList.innerHTML = items.map(item => {
    const rawOffers = item.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id));
    const minPrice = Math.min(...rawOffers.map(l => l.price));

    return `
      <div class="cluster-card">
        <div class="cluster-head">
          <div class="cluster-summary">
            <img class="cluster-thumb" src="${item.image}" alt="${item.canonicalName}" />
            <div>
              <div class="cluster-brand">${item.brand} • ${item.category}</div>
              <h3 class="cluster-name">${item.canonicalName}</h3>
              <p class="cluster-subtitle">${item.highlightSpecs}</p>
            </div>
          </div>
          <div class="cluster-actions">
            <span class="cluster-count-badge">${rawOffers.length} Raw Listings Merged</span>
            <button class="btn-action-small" onclick="openSellerModal('${item.id}')">
              Compare Prices
            </button>
          </div>
        </div>

        <div class="cluster-table-wrap">
          <table class="cluster-table">
            <thead>
              <tr>
                <th style="width: 100px;">Listing ID</th>
                <th style="width: 160px;">Seller</th>
                <th>Raw Scraped Marketplace Title</th>
                <th style="width: 130px;">Price (INR)</th>
                <th style="width: 120px; text-align: right;">Resolution</th>
              </tr>
            </thead>
            <tbody>
              ${rawOffers.map(listing => {
                const isCheapest = listing.price === minPrice;
                return `
                  <tr class="${isCheapest ? 'row-best-price' : ''}">
                    <td><span class="code-id">${listing.listingId}</span></td>
                    <td><span class="seller-name">${listing.sellerName}</span></td>
                    <td class="raw-title-cell" title="${listing.description}">
                      "${listing.title}"
                    </td>
                    <td><strong>${formatINR(listing.price)}</strong></td>
                    <td style="text-align: right;">
                      <span class="badge-status ${isCheapest ? 'status-best' : 'status-merged'}">
                        ${isCheapest ? 'Lowest Price' : 'Matched'}
                      </span>
                    </td>
                  </tr>
                `;
              }).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }).join('');
}

// -------------------------------------------------------------
// View 3: Price Comparison Matrix Table
// -------------------------------------------------------------
function renderPriceMatrix(items) {
  if (!dom.matrixTableBody) return;

  if (items.length === 0) {
    dom.matrixTableBody.innerHTML = `
      <tr>
        <td colspan="7" style="text-align: center; padding: 48px 20px; color: var(--text-muted);">
          No electronics found matching your filters.
        </td>
      </tr>
    `;
    return;
  }

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
              <div class="matrix-prod-meta">${item.brand} • ${item.category}</div>
            </div>
          </div>
        </td>
        <td>
          <span class="price-highlight">${formatINR(minPrice)}</span>
        </td>
        <td>
          <span class="seller-pill">${cheapestListing.sellerName}</span>
        </td>
        <td>
          <span class="price-regular">${formatINR(maxPrice)}</span>
        </td>
        <td>
          <span class="savings-pill">${savings > 0 ? `+${formatINR(savings)}` : '—'}</span>
        </td>
        <td>
          <div class="seller-chips-wrap">
            ${rawOffers.map(l => `
              <span class="seller-chip ${l.price === minPrice ? 'seller-chip-cheapest' : ''}">
                ${l.sellerName}: ${formatINR(l.price)}
              </span>
            `).join('')}
          </div>
        </td>
        <td style="text-align: right;">
          <button class="btn-action-small" onclick="openSellerModal('${item.id}')">Offers</button>
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

  dom.modalSellerList.innerHTML = rawOffers.map((listing) => {
    const isCheapest = listing.price === minPrice;
    const seller = SELLERS[listing.sellerId] || { rating: 4.8, reviews: 1000, shipping: "Standard Delivery" };
    return `
      <div class="seller-offer-card ${isCheapest ? 'best-offer' : ''}">
        <div class="offer-details">
          <div class="offer-seller-title">
            <span class="offer-seller-name">${listing.sellerName}</span>
            ${isCheapest ? '<span class="best-deal-badge">Cheapest Verified Offer</span>' : ''}
          </div>
          <div class="offer-meta">
            ★ ${seller.rating} (${seller.reviews} reviews) • <span class="shipping-tag">${seller.shipping}</span>
          </div>
          <div class="offer-raw-title">Scraped Title: "${listing.title}"</div>
        </div>

        <div class="offer-action-group">
          <div class="offer-price-tag">${formatINR(listing.price)}</div>
          <button class="btn-select-seller" onclick="showPurchaseFeedback('${listing.sellerName}', ${listing.price})">
            Select Offer
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
  closeModal();
  showToast(`Redirecting to verified checkout with ${sellerName} at ${formatINR(price)}`);
}

// Sophisticated Toast Notification
function showToast(message) {
  let toast = document.getElementById('appToast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'appToast';
    toast.className = 'app-toast';
    document.body.appendChild(toast);
  }
  toast.textContent = message;
  toast.classList.add('show');
  clearTimeout(toast._timeout);
  toast._timeout = setTimeout(() => {
    toast.classList.remove('show');
  }, 3200);
}

// -------------------------------------------------------------
// View 4: Live Resolution Playground / Matcher Sandbox
// -------------------------------------------------------------
const PRESETS = [
  {
    name: "Apple iPhone 15 Variation (High Match)",
    s1: "Apple iPhone 15 128GB Black",
    s2: "iPhone 15 Apple 128 GB - Midnight Black"
  },
  {
    name: "Sony ANC Headphones (Format Shift)",
    s1: "Sony WH-1000XM5 Wireless Headphones",
    s2: "Sony WH1000XM5/B Wireless Over-Ear Headphone Active Noise Cancelling"
  },
  {
    name: "Samsung Galaxy S24 (Minor Order Shift)",
    s1: "Samsung Galaxy S24 256GB Onyx Black",
    s2: "Galaxy S24 Samsung 256 GB Smartphone 5G"
  },
  {
    name: "Distinct Electronics (No Match)",
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

  presetSelect.innerHTML = PRESETS.map((p, idx) => `
    <option value="${idx}">${p.name}</option>
  `).join('');

  presetSelect.addEventListener('change', (e) => {
    const p = PRESETS[e.target.value];
    str1.value = p.s1;
    str2.value = p.s2;
    runSandboxMatch();
  });

  str1.value = PRESETS[0].s1;
  str2.value = PRESETS[0].s2;

  runBtn.addEventListener('click', runSandboxMatch);
  runSandboxMatch();
}

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
          matrix[i - 1][j - 1] + 1,
          matrix[i][j - 1] + 1,
          matrix[i - 1][j] + 1
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
  const s1 = (document.getElementById('sandboxStr1')?.value || '').trim();
  const s2 = (document.getElementById('sandboxStr2')?.value || '').trim();

  const jaccardScore = computeTokenJaccard(s1, s2);
  const dist = computeLevenshtein(s1, s2);
  const maxLen = Math.max(s1.length, s2.length);
  const editScore = maxLen === 0 ? 1 : Math.max(0, 1 - (dist / maxLen));
  const subScore = computeSubstringMatch(s1, s2);

  const compositeScore = Math.round((jaccardScore * 0.40 + editScore * 0.35 + subScore * 0.25) * 100);
  const isMatch = compositeScore >= 68;

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
      verdict.className = 'verdict-pill verdict-match';
      verdict.textContent = 'Match: Same Product Entity';
    } else {
      verdict.className = 'verdict-pill verdict-nomatch';
      verdict.textContent = 'Distinct: Different Products';
    }
  }

  if (barToken) barToken.style.width = `${Math.round(jaccardScore * 100)}%`;
  if (valToken) valToken.textContent = `${Math.round(jaccardScore * 100)}%`;

  if (barEdit) barEdit.style.width = `${Math.round(editScore * 100)}%`;
  if (valEdit) valEdit.textContent = `${Math.round(editScore * 100)}% (Dist: ${dist})`;

  if (barSub) barSub.style.width = `${Math.round(subScore * 100)}%`;
  if (valSub) valSub.textContent = `${Math.round(subScore * 100)}%`;
}
