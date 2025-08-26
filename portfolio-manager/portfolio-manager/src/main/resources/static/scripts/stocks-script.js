const base = "http://localhost:8080"

document.querySelector('.hamburger').addEventListener('click', () => {
  alert('Open portfolio selector or add new portfolio');
});
document.querySelector('.user').addEventListener('click', () => {
  alert('Open user settings or profile');
});
document.getElementById('nextBtn').addEventListener('click', () => {
  if (currentPage * itemsPerPage < stocks.length) {
    currentPage++;
    renderListPage(currentPage);
  }
});
document.getElementById('prevBtn').addEventListener('click', () => {
  if (currentPage > 1) {
    currentPage--;
    renderListPage(currentPage);
  }
});

let stocks = [];
async function loadStocks() {
    const res = await fetch(base + `/stocks`);
    const data = await res.json();
    stocks = data;
    console.log(stocks);
}

const itemsPerPage = 10;
let currentPage = 1;

renderPage();

async function renderPage(portfolioId = 1) {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    pageTitle.textContent = currentPortfolio.name;

    await loadStocks();

    renderListPage(currentPage);
}

function renderListPage(page) {
    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageStocks = stocks.slice(start, end);
    console.log(pageStocks);

    const container = document.getElementById('stockList');
    container.innerHTML = ''; // clear previous items
    pageStocks.forEach(stock => {
    const li = document.createElement('li');
    li.className = 'stock-item';
    li.textContent = stock.stockSymbol +' '+ stock.stockName +' $'+ stock.currentPrice;
    container.appendChild(li);
    });

    document.getElementById('pageInfo').textContent = `Page ${page}`;

    const stockItems = document.querySelectorAll('.stock-item');
    const modal = document.getElementById('stockModal');
    const closeBtn = document.querySelector('.close-button');
    const stockTitle = document.getElementById('stockTitle');
    const stockChart = document.getElementById('stockChart');
    stockItems.forEach(item => {
      item.addEventListener('click', () => {
        const symbol = item.dataset.symbol;
        stockTitle.textContent = `${item.textContent}`;
        showModal(symbol);
      });
    });
    closeBtn.addEventListener('click', () => {
      modal.classList.add('hidden');
    });
}

async function fetchPortfolios() {
    const res = await fetch(base + `/portfolios`);
    const portfolios = await res.json();
    return portfolios;
}

function showModal(symbol) {
  modal.classList.remove('hidden');
  renderChart(symbol); // Load chart data dynamically
}

function renderChart(symbol) {
  // Example using Chart.js
  const ctx = stockChart.getContext('2d');
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
      datasets: [{
        label: `${symbol} Performance`,
        data: [120, 125, 130, 128, 135],
        borderColor: '#3498db',
        fill: false
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: { beginAtZero: false }
      }
    }
  });
}
