const base = "http://localhost:8080"

document.querySelector('.hamburger').addEventListener('click', () => {
  alert('Open portfolio selector or add new portfolio');
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

document.querySelector('.forward-button').addEventListener('click', () => {
    forwardDay();
    renderPage();
});

async function forwardDay() {
    fetch(base + `/portfolios/${portfolioId}/forward-date`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
    })
}

document.getElementById('buyButton').addEventListener('click', async () => {
    const transactionDTO = {
        stockId: currentStockId,
        amount: document.getElementById('quantity').value,
        action: "BUYING"
    };
    await fetch(base + `/portfolios/${portfolioId}/transaction`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(transactionDTO)
    })
    document.getElementById('quantity').value = '';
    modal.classList.add('hidden');
    renderPage();
});

document.getElementById('sellButton').addEventListener('click', async () => {
    const transactionDTO = {
        stockId: currentStockId,
        amount: document.getElementById('quantity').value,
        action: "SELLING"
    };
    await fetch(base + `/portfolios/${portfolioId}/transaction`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(transactionDTO)
    })
    document.getElementById('quantity').value = '';
    modal.classList.add('hidden');
    renderPage();
});


let portfolioId = 1;

let stocks = [];
async function loadStocks() {
    const res = await fetch(base + `/stocks`);
    const data = await res.json();
    stocks = data;
}

const itemsPerPage = 10;
let currentPage = 1;
let currentPageStock = [];
let currentStockId;

let container = document.getElementById('stockList');
let modal = document.getElementById('stockModal');
let closeBtn = document.querySelector('.close-button');
let stockTitle = document.getElementById('stockTitle');
let stockChart = document.getElementById('stockChart');
let stockPerformChart;

renderPage();

async function renderPage() {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    const pageDate = document.querySelector('.portfolio-date');
    pageTitle.textContent = currentPortfolio.name;
    pageDate.textContent = currentPortfolio.currentDate.slice(0,10);

    await loadStocks();

    renderListPage(currentPage);
}

function renderListPage(page) {
    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageStocks = stocks.slice(start, end);

    container.innerHTML = ''; // clear previous items
    pageStocks.forEach(stock => renderStockPage(stock));

    closeBtn.addEventListener('click', () => {
        modal.classList.add('hidden');
    });

    document.getElementById('pageInfo').textContent = `Page ${page}`;
}

async function renderStockPage(stock) {
    const li = document.createElement('li');
    li.className = 'stock-item';
    li.textContent = stock.stockSymbol +' '+ stock.stockName +' $'+ stock.currentPrice;
    li.addEventListener('click', () => {
        currentStockId = stock.id;
        stockTitle.textContent = stock.stockName +' ('+stock.stockSymbol+') ';
        showModal(stock.stockSymbol);
    });
    container.appendChild(li);
}

async function fetchPortfolios() {
    const res = await fetch(base + `/portfolios`);
    const portfolios = await res.json();
    return portfolios;
}

function showModal(symbol) {
  modal.classList.remove('hidden');
  renderChart(symbol, stockChart); // Load chart data dynamically
}

function renderChart(symbol) {
  // Example using Chart.js
  const ctx = stockChart.getContext('2d');
  if (stockPerformChart) {
    stockPerformChart.destroy();
  }
  stockPerformChart = new Chart(ctx, {
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
