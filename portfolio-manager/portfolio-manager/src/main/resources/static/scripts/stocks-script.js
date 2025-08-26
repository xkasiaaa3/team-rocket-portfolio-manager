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
    setTimeout(() => {renderPage();}, 5000);
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
    pageDate.textContent = new Date(currentPortfolio.currentDate).
        toLocaleDateString('en-US', {weekday: 'short', month: 'short', day: 'numeric', year: 'numeric'});

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
    const change = (stock.change*100).toFixed(2);
    li.innerHTML += '<span>'+stock.stockName+' ('+stock.stockSymbol+')'+'</span>'
    li.innerHTML += '<span>$'+stock.stockName+' ('+stock.stockSymbol+')'+'</span>'
    li.textContent =  +' $'+ stock.currentPrice + ' ' + change;
    li.addEventListener('click', () => {
        currentStockId = stock.id;
        stockTitle.textContent = stock.stockName +' ('+stock.stockSymbol+') ';
        showModal(stock);
    });
    container.appendChild(li);
}

async function fetchPortfolios() {
    const res = await fetch(base + `/portfolios`);
    const portfolios = await res.json();
    return portfolios;
}

async function fetchStockHistory(stock) {
    const res = await fetch(base + `/stocks/${stock.id}/history`);
    const histories = await res.json();
    return histories
}

async function showModal(stock) {
  modal.classList.remove('hidden');
  const histories = await fetchStockHistory(stock)
  renderChart(histories);
}

function renderChart(histories) {
    const nwData = histories.map(pair => ({x: pair.date.slice(5,10), y: pair.price}));
  const ctx = stockChart.getContext('2d');
  if (stockPerformChart) {
    stockPerformChart.destroy();
  }
  stockPerformChart = new Chart(ctx, {
      type: 'line',

      // 1. Data: time-series points for net worth
        data: {
            datasets: [{
              data: nwData,
              fill: 'origin',                  // area under the line
              borderColor: 'rgba(75,192,192,1)',
              backgroundColor: 'rgba(75,192,192,0.2)',
              pointRadius: 3,
              tension: 0.2                    // smooth curves
            }]
        },

        scales: {
          x: {
            type: 'time',
            time: {
              unit: 'day',
              tooltipFormat: 'MMM yyyy'
            },
            title: { display: false, text: 'Date' }
          },
          y: {
            beginAtZero: false,
            title: { display: false, text: 'Net Worth (USD)' },
            ticks: {
              callback: value => '$' + value.toLocaleString()
            }
          }
        },

        options: {
        responsive: true,
          legend: {
            display: false
          },
        scales: {
        y: { beginAtZero: false }
        }
        }
  });
}
