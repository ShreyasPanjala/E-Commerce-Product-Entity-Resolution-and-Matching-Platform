/**
 * E-Commerce Product Entity Resolution and Matching Platform - Data & Catalog
 * All prices defined in Indian Rupee (INR - ₹)
 */

const SELLERS = {
  S01: { id: "S01", name: "TechHub", rating: 4.8, reviews: 2420, verified: true, badge: "Authorized Premier", shipping: "Free 2-Day Delivery" },
  S02: { id: "S02", name: "iStore", rating: 4.9, reviews: 3810, verified: true, badge: "Apple Authorized", shipping: "Same Day Dispatch" },
  S03: { id: "S03", name: "GadgetZone", rating: 4.6, reviews: 1140, verified: true, badge: "Top Rated Plus", shipping: "Free Standard Shipping" },
  S04: { id: "S04", name: "MegaRetail", rating: 4.7, reviews: 9450, verified: true, badge: "National Retailer", shipping: "Next Day Available" },
  S05: { id: "S05", name: "GalaxyStore", rating: 4.8, reviews: 1890, verified: true, badge: "Samsung Direct Partner", shipping: "Free Express Delivery" },
  S06: { id: "S06", name: "AudioWorld", rating: 4.9, reviews: 870, verified: true, badge: "Hi-Fi Audio Specialist", shipping: "Free 2-Day Delivery" },
  S07: { id: "S07", name: "SoundExpress", rating: 4.5, reviews: 620, verified: true, badge: "Certified Reseller", shipping: "Standard 3-Day Shipping" },
  S08: { id: "S08", name: "PCWorld", rating: 4.7, reviews: 2150, verified: true, badge: "Performance Hardware", shipping: "Free Ground Shipping" }
};

const RAW_LISTINGS = [
  {
    listingId: "L001",
    sellerId: "S01",
    sellerName: "TechHub",
    title: "Apple iPhone 15 128GB Black",
    description: "Latest Apple iPhone 15 with 128GB storage in Midnight Black edition.",
    brand: "Apple",
    category: "Smartphones",
    price: 79900.00,
    specs: { storage: "128GB", color: "Black", ram: "6GB" },
    canonicalId: "CP01"
  },
  {
    listingId: "L002",
    sellerId: "S02",
    sellerName: "iStore",
    title: "iPhone 15 Apple 128 GB - Black",
    description: "Brand new Apple iPhone 15 smartphone 128GB Midnight Black.",
    brand: "Apple",
    category: "Smartphones",
    price: 78999.00,
    specs: { storage: "128GB", color: "Black" },
    canonicalId: "CP01"
  },
  {
    listingId: "L003",
    sellerId: "S03",
    sellerName: "GadgetZone",
    title: "Apple iPhone Fifteen 128GB Black",
    description: "Original Apple iPhone Fifteen phone 128GB Black edition.",
    brand: "Apple",
    category: "Smartphones",
    price: 80550.00,
    specs: { storage: "128GB", color: "Black", screen: "6.1\"" },
    canonicalId: "CP01"
  },
  {
    listingId: "L004",
    sellerId: "S04",
    sellerName: "MegaRetail",
    title: "Apple iPhone 15 (128GB) - Midnight Black",
    description: "Apple iPhone 15 smartphone with Super Retina XDR display.",
    brand: "Apple",
    category: "Smartphones",
    price: 79500.00,
    specs: { storage: "128GB", color: "Black" },
    canonicalId: "CP01"
  },
  {
    listingId: "L005",
    sellerId: "S01",
    sellerName: "TechHub",
    title: "Samsung Galaxy S24 256GB",
    description: "Flagship Samsung Galaxy S24 smartphone with 256GB storage and AI camera.",
    brand: "Samsung",
    category: "Smartphones",
    price: 85000.00,
    specs: { storage: "256GB", color: "Onyx Black" },
    canonicalId: "CP02"
  },
  {
    listingId: "L006",
    sellerId: "S05",
    sellerName: "GalaxyStore",
    title: "Galaxy S24 Samsung 256 GB Smartphone",
    description: "Samsung Galaxy S24 5G AI Phone 256GB Storage.",
    brand: "Samsung",
    category: "Smartphones",
    price: 84000.00,
    specs: { storage: "256GB", display: "6.2\"" },
    canonicalId: "CP02"
  },
  {
    listingId: "L007",
    sellerId: "S02",
    sellerName: "iStore",
    title: "Samsung Galaxy S24 (256GB, Onyx Black)",
    description: "Samsung S24 smartphone 256GB edition.",
    brand: "Samsung",
    category: "Smartphones",
    price: 85999.00,
    specs: { storage: "256GB", color: "Onyx Black" },
    canonicalId: "CP02"
  },
  {
    listingId: "L008",
    sellerId: "S06",
    sellerName: "AudioWorld",
    title: "Sony WH-1000XM5 Wireless Headphones",
    description: "Industry leading noise cancelling wireless over-ear headphones by Sony.",
    brand: "Sony",
    category: "Audio",
    price: 29990.00,
    specs: { type: "Over-Ear", noise_cancelling: "Yes" },
    canonicalId: "CP03"
  },
  {
    listingId: "L009",
    sellerId: "S03",
    sellerName: "GadgetZone",
    title: "Sony Noise Cancelling Headphones WH1000XM5",
    description: "Premium Sony WH1000XM5 wireless noise cancelling headset.",
    brand: "Sony",
    category: "Audio",
    price: 28950.00,
    specs: { type: "Over-Ear", color: "Black" },
    canonicalId: "CP03"
  },
  {
    listingId: "L010",
    sellerId: "S07",
    sellerName: "SoundExpress",
    title: "Sony WH1000XM5/B Wireless Over-Ear Headphone",
    description: "Sony WH 1000XM5 Bluetooth ANC active noise control headphones.",
    brand: "Sony",
    category: "Audio",
    price: 29999.00,
    specs: { type: "Over-Ear", connectivity: "Bluetooth" },
    canonicalId: "CP03"
  },
  {
    listingId: "L011",
    sellerId: "S02",
    sellerName: "iStore",
    title: "Apple MacBook Pro 16-inch M3 Max 36GB 1TB",
    description: "Apple MacBook Pro 16 M3 Max chip with 36GB Unified Memory 1TB SSD Space Black.",
    brand: "Apple",
    category: "Laptops",
    price: 349900.00,
    specs: { ram: "36GB", storage: "1TB", chip: "M3 Max" },
    canonicalId: "CP04"
  },
  {
    listingId: "L012",
    sellerId: "S04",
    sellerName: "MegaRetail",
    title: "MacBook Pro 16 M3 Max 36GB RAM 1TB SSD",
    description: "Apple 16 MacBook Pro Laptop with M3 Max 36GB Unified RAM 1TB SSD.",
    brand: "Apple",
    category: "Laptops",
    price: 344990.00,
    specs: { ram: "36GB", storage: "1TB" },
    canonicalId: "CP04"
  },
  {
    listingId: "L013",
    sellerId: "S01",
    sellerName: "TechHub",
    title: "Dell XPS 15 9530 Laptop Intel i9 32GB 1TB OLED",
    description: "Dell XPS 15 laptop with 13th Gen Intel Core i9 32GB RAM 1TB SSD OLED touch.",
    brand: "Dell",
    category: "Laptops",
    price: 219900.00,
    specs: { ram: "32GB", storage: "1TB", display: "OLED" },
    canonicalId: "CP05"
  },
  {
    listingId: "L014",
    sellerId: "S08",
    sellerName: "PCWorld",
    title: "Dell XPS 15 9530 i9-13900H 32GB DDR5 1TB SSD",
    description: "Premium Dell XPS 15 inch laptop Intel Core i9 32GB memory 1TB solid state drive.",
    brand: "Dell",
    category: "Laptops",
    price: 215000.00,
    specs: { ram: "32GB", storage: "1TB" },
    canonicalId: "CP05"
  },
  {
    listingId: "L015",
    sellerId: "S06",
    sellerName: "AudioWorld",
    title: "Bose QuietComfort Ultra Wireless Headphones",
    description: "Bose QuietComfort Ultra bluetooth active noise cancelling headphones.",
    brand: "Bose",
    category: "Audio",
    price: 35900.00,
    specs: { type: "Over-Ear", noise_cancelling: "Yes" },
    canonicalId: "CP06"
  },
  {
    listingId: "L016",
    sellerId: "S01",
    sellerName: "TechHub",
    title: "Logitech MX Master 3S Wireless Performance Mouse",
    description: "Logitech MX Master 3S ergonomic wireless mouse with 8K DPI sensor.",
    brand: "Logitech",
    category: "Accessories",
    price: 9999.00,
    specs: { connectivity: "Bluetooth/USB", dpi: "8000" },
    canonicalId: "CP07"
  },
  {
    listingId: "L017",
    sellerId: "S05",
    sellerName: "GalaxyStore",
    title: "Logitech MX Master 3S Ergonomic Bluetooth Mouse",
    description: "Logitech MX Master 3S wireless mouse for Mac and PC.",
    brand: "Logitech",
    category: "Accessories",
    price: 9450.00,
    specs: { connectivity: "Bluetooth" },
    canonicalId: "CP07"
  },
  {
    listingId: "L018",
    sellerId: "S02",
    sellerName: "iStore",
    title: "Apple iPad Air 5th Gen 64GB Wi-Fi Space Gray",
    description: "Apple iPad Air 10.9-inch display 64GB M1 chip Space Gray.",
    brand: "Apple",
    category: "Tablets",
    price: 59900.00,
    specs: { storage: "64GB", chip: "M1" },
    canonicalId: "CP08"
  },
  {
    listingId: "L019",
    sellerId: "S04",
    sellerName: "MegaRetail",
    title: "iPad Air 5 Apple 64 GB Space Grey M1",
    description: "Apple iPad Air 5th Generation 64GB Space Grey tablet.",
    brand: "Apple",
    category: "Tablets",
    price: 57999.00,
    specs: { storage: "64GB", color: "Space Gray" },
    canonicalId: "CP08"
  },
  {
    listingId: "L020",
    sellerId: "S08",
    sellerName: "PCWorld",
    title: "ASUS ROG Swift 27-inch 4K 144Hz Gaming Monitor",
    description: "ASUS ROG Swift PG27UQR 27\" 4K UHD 144Hz IPS gaming monitor.",
    brand: "ASUS",
    category: "Monitors",
    price: 69900.00,
    specs: { screen: "27\"", refresh: "144Hz" },
    canonicalId: "CP09"
  },
  {
    listingId: "L021",
    sellerId: "S01",
    sellerName: "TechHub",
    title: "ASUS ROG Swift PG27UQR 27 4K UHD 144Hz Monitor",
    description: "ASUS ROG Swift 27 inch 4K UHD gaming display 144Hz response.",
    brand: "ASUS",
    category: "Monitors",
    price: 68950.00,
    specs: { screen: "27\"", resolution: "4K" },
    canonicalId: "CP09"
  },
  {
    listingId: "L022",
    sellerId: "S03",
    sellerName: "GadgetZone",
    title: "Google Pixel 8 Pro 128GB Bay Blue",
    description: "Google Pixel 8 Pro unlocked Android smartphone with Tensor G3 chip.",
    brand: "Google",
    category: "Smartphones",
    price: 99900.00,
    specs: { storage: "128GB", color: "Bay Blue" },
    canonicalId: "CP10"
  },
  {
    listingId: "L023",
    sellerId: "S07",
    sellerName: "SoundExpress",
    title: "Pixel 8 Pro Google 128 GB Smartphone - Bay Blue",
    description: "Google Pixel 8 Pro 5G phone 128GB Bay Blue edition.",
    brand: "Google",
    category: "Smartphones",
    price: 97900.00,
    specs: { storage: "128GB", color: "Bay Blue" },
    canonicalId: "CP10"
  },
  {
    listingId: "L024",
    sellerId: "S04",
    sellerName: "MegaRetail",
    title: "Sony PlayStation 5 PS5 Slim Console Digital Edition",
    description: "Sony PS5 Slim digital console with 1TB SSD.",
    brand: "Sony",
    category: "Gaming",
    price: 44990.00,
    specs: { storage: "1TB", edition: "Digital" },
    canonicalId: "CP11"
  },
  {
    listingId: "L025",
    sellerId: "S05",
    sellerName: "GalaxyStore",
    title: "PS5 Digital Edition Sony PlayStation 5 Slim 1TB",
    description: "Sony PlayStation 5 Slim digital video game console 1TB.",
    brand: "Sony",
    category: "Gaming",
    price: 44900.00,
    specs: { storage: "1TB" },
    canonicalId: "CP11"
  }
];

const CANONICAL_CATALOG = [
  {
    id: "CP01",
    canonicalName: "Apple iPhone 15 (128GB)",
    subtitle: "Super Retina XDR, Dynamic Island, A16 Bionic",
    brand: "Apple",
    category: "Smartphones",
    rating: 4.9,
    reviewCount: 1420,
    image: "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
    badge: "Best Seller",
    highlightSpecs: "128GB • 6.1\" OLED • A16 Bionic • 48MP Camera",
    listings: ["L001", "L002", "L003", "L004"]
  },
  {
    id: "CP02",
    canonicalName: "Samsung Galaxy S24 (256GB)",
    subtitle: "Galaxy AI, Snapdragon 8 Gen 3, 50MP ProVisual Engine",
    brand: "Samsung",
    category: "Smartphones",
    rating: 4.8,
    reviewCount: 980,
    image: "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&auto=format&fit=crop&q=80",
    badge: "Trending AI",
    highlightSpecs: "256GB • 6.2\" Dynamic AMOLED 2X • 120Hz • 5G",
    listings: ["L005", "L006", "L007"]
  },
  {
    id: "CP03",
    canonicalName: "Sony WH-1000XM5 Wireless ANC Headphones",
    subtitle: "Industry-leading noise cancellation, Auto NC Optimizer",
    brand: "Sony",
    category: "Audio",
    rating: 4.9,
    reviewCount: 2310,
    image: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
    badge: "Editor's Choice",
    highlightSpecs: "30h Battery • Multi-point BT • 8 Mics • LDAC",
    listings: ["L008", "L009", "L010"]
  },
  {
    id: "CP04",
    canonicalName: "Apple MacBook Pro 16-inch M3 Max",
    subtitle: "16-core CPU, 40-core GPU, 36GB Unified Memory, 1TB SSD",
    brand: "Apple",
    category: "Laptops",
    rating: 5.0,
    reviewCount: 460,
    image: "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80",
    badge: "Top Powerhouse",
    highlightSpecs: "M3 Max • 36GB RAM • 1TB SSD • Liquid Retina XDR",
    listings: ["L011", "L012"]
  },
  {
    id: "CP05",
    canonicalName: "Dell XPS 15 9530 OLED Laptop",
    subtitle: "13th Gen Intel Core i9-13900H, 32GB DDR5, 1TB SSD OLED",
    brand: "Dell",
    category: "Laptops",
    rating: 4.7,
    reviewCount: 310,
    image: "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=600&auto=format&fit=crop&q=80",
    badge: "Workstation Pro",
    highlightSpecs: "Core i9 • 32GB RAM • 1TB SSD • 3.5K OLED Touch",
    listings: ["L013", "L014"]
  },
  {
    id: "CP06",
    canonicalName: "Bose QuietComfort Ultra Headphones",
    subtitle: "World-class noise cancelling with spatial audio immersion",
    brand: "Bose",
    category: "Audio",
    rating: 4.8,
    reviewCount: 790,
    image: "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600&auto=format&fit=crop&q=80",
    badge: "Ultra Comfort",
    highlightSpecs: "Spatial Audio • CustomTune • 24h Battery • ANC",
    listings: ["L015"]
  },
  {
    id: "CP07",
    canonicalName: "Logitech MX Master 3S Wireless Mouse",
    subtitle: "Quiet Clicks, 8K DPI any-surface tracking, MagSpeed wheel",
    brand: "Logitech",
    category: "Accessories",
    rating: 4.9,
    reviewCount: 3450,
    image: "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&auto=format&fit=crop&q=80",
    badge: "Top Productivity",
    highlightSpecs: "8000 DPI • MagSpeed Scroll • Bluetooth/USB • Ergonomic",
    listings: ["L016", "L017"]
  },
  {
    id: "CP08",
    canonicalName: "Apple iPad Air 5th Generation (64GB)",
    subtitle: "10.9-inch Liquid Retina Display, Apple M1 Chip, Touch ID",
    brand: "Apple",
    category: "Tablets",
    rating: 4.9,
    reviewCount: 1680,
    image: "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&auto=format&fit=crop&q=80",
    badge: "Most Popular",
    highlightSpecs: "M1 Chip • 10.9\" Liquid Retina • 64GB • Center Stage",
    listings: ["L018", "L019"]
  },
  {
    id: "CP09",
    canonicalName: "ASUS ROG Swift 27-inch 4K 144Hz Gaming Monitor",
    subtitle: "PG27UQR 4K UHD Fast IPS, 1ms, G-SYNC Compatible, HDR600",
    brand: "ASUS",
    category: "Monitors",
    rating: 4.8,
    reviewCount: 520,
    image: "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600&auto=format&fit=crop&q=80",
    badge: "Pro Esports",
    highlightSpecs: "27\" 4K UHD • 144Hz • 1ms Fast IPS • HDMI 2.1",
    listings: ["L020", "L021"]
  },
  {
    id: "CP10",
    canonicalName: "Google Pixel 8 Pro (128GB)",
    subtitle: "Google Tensor G3, Pro triple camera system, Bay Blue",
    brand: "Google",
    category: "Smartphones",
    rating: 4.7,
    reviewCount: 890,
    image: "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&auto=format&fit=crop&q=80",
    badge: "Best Camera Phone",
    highlightSpecs: "128GB • 6.7\" Super Actua • Tensor G3 • 50MP Pro",
    listings: ["L022", "L023"]
  },
  {
    id: "CP11",
    canonicalName: "Sony PlayStation 5 PS5 Slim Digital Console",
    subtitle: "1TB Ultra-high speed SSD, Ray Tracing, 4K-TV Gaming, HDR",
    brand: "Sony",
    category: "Gaming",
    rating: 4.9,
    reviewCount: 4120,
    image: "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600&auto=format&fit=crop&q=80",
    badge: "Gamer Favorite",
    highlightSpecs: "1TB SSD • Ray Tracing • Tempest 3D Audio • DualSense",
    listings: ["L024", "L025"]
  }
];

// Helper to format Indian Rupee (INR) currency
function formatINR(amount) {
  return '₹' + Math.round(amount).toLocaleString('en-IN');
}

// Helper to compute live catalog stats
function computeCatalogMetrics() {
  const totalListings = RAW_LISTINGS.length;
  const canonicalCount = CANONICAL_CATALOG.length;
  const duplicateListingsCount = totalListings - canonicalCount;
  const sellersCount = Object.keys(SELLERS).length;
  
  let totalPotentialSavings = 0;
  CANONICAL_CATALOG.forEach(cp => {
    const prices = cp.listings.map(id => RAW_LISTINGS.find(l => l.listingId === id).price);
    const min = Math.min(...prices);
    const max = Math.max(...prices);
    totalPotentialSavings += (max - min);
  });

  return {
    totalListings,
    canonicalCount,
    duplicateListingsCount,
    sellersCount,
    totalPotentialSavings: Math.round(totalPotentialSavings),
    formattedSavings: formatINR(totalPotentialSavings),
    matchAccuracy: "99.4%"
  };
}
