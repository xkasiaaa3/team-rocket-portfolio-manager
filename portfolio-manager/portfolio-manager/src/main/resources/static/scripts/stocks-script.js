const base = "http://localhost:8080"

document.querySelector('.hamburger').addEventListener('click', () => {
    document.getElementById('dropdownMenu').style.display =
        document.getElementById('dropdownMenu').style.display === 'block' ? 'none' : 'block';
});

document.addEventListener('click', (e) => {
  if (!document.querySelector('.hamburger').contains(e.target) &&
    !document.getElementById('dropdownMenu').contains(e.target)) {
        document.getElementById('dropdownMenu').style.display = 'none';
  }
});

document.getElementById('dropdownMenu').addEventListener('click', async (e) => {
  if (e.target.tagName === 'LI') {
    if (e.target.classList.contains('new')) {
        const portfolio_name = prompt('Name of new portfolio');
        await fetch(base + `/portfolios`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: portfolio_name
        });
        renderPage();
    } else {
        const id = e.target.textContent;
        const idx = id.indexOf(":");
        const beforeColon = idx !== -1 ? id.slice(0, idx) : id;
        portfolioId = Number(beforeColon);
        renderPage();
    }
    document.getElementById('dropdownMenu').style.display = 'none'; // close menu
  }
});

document.getElementById('nextBtn').addEventListener('click', () => {
  if (currentPage * itemsPerPage < filteredStocks.length) {
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

document.querySelector('.forward-button').addEventListener('click', async () => {
    await forwardDay();
    loadStocks();
});

async function forwardDay() {
    await fetch(base + `/portfolios/${portfolioId}/forward-date`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
    })
}

let portfolioId = 1;
let currentPortfolio;

let stocks = [];
let filteredStocks = [];
async function loadStocks() {
    const res = await fetch(base + `/stocks`);
    const data = await res.json();
    stocks = data;
    filteredStocks = stocks;
    renderPage();
}

const itemsPerPage = 10;
let currentPage = 1;
let currentPageStock = [];
let currentStock;

const searchBar = document.getElementById("searchBar");
const sortOrder = document.getElementById("sortOrder");

let container = document.querySelector('#stockList tbody');
let modal = document.getElementById('stockModal');
let closeBtn = document.querySelector('.close-button');
let stockTitle = document.getElementById('stockTitle');
let stockChart = document.getElementById('stockChart');
let stockPerformChart;

loadStocks();

searchBar.addEventListener("input", async () => sortStocks());
sortOrder.addEventListener("change", async () => sortStocks());

document.getElementById('buyButton').addEventListener('click', async () => {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios.find(p => p.id === portfolioId);
    const amount = document.getElementById('quantity').value;
    if (confirm(
        "Are you sure you want to buy "+amount+" shares of "+currentStock.stockName+"?\n"+
        "This will cost $"+amount*currentStock.currentPrice+"\n"+
        "Your balance is currently $"+currentPortfolio.balance
        )) {
        const transactionDTO = {
            stockId: currentStock.id,
            amount: amount,
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
    } else {
        document.getElementById('quantity').value = '';
        modal.classList.add('hidden');
    }
});

document.getElementById('sellButton').addEventListener('click', async () => {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios.find(p => p.id === portfolioId);
    const amount = document.getElementById('quantity').value;
    if (confirm(
        "Are you sure you want to sell "+amount+" shares of "+currentStock.stockName+"?\n"+
        "This will add $"+amount*currentStock.currentPrice+" to your balance\n"+
        "Your balance is currently $"+currentPortfolio.balance
        )) {
        const transactionDTO = {
            stockId: currentStock.id,
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
    } else {
        document.getElementById('quantity').value = '';
        modal.classList.add('hidden');
    }
});

async function renderPage() {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios.find(p => p.id === portfolioId);

    const dropdownMenu = document.querySelector('#dropdownMenu ul');
    dropdownMenu.innerHTML = '';
    for (const p of portfolios) {
        dropdownMenu.innerHTML += '<li>'+p.id+': '+p.name+'</li>';
    }
    dropdownMenu.innerHTML += '<li class="new">+ Create New Portfolio</li>'

    const pageTitle = document.querySelector('.portfolio-name');
    const pageDate = document.querySelector('.portfolio-date');
    pageTitle.textContent = currentPortfolio.name;
    pageDate.textContent = new Date(currentPortfolio.currentDate).
        toLocaleDateString('en-US', {weekday: 'short', month: 'short', day: 'numeric', year: 'numeric'});

    await renderListPage(currentPage);

    const amountInvested = await fetchPortfolioAmountInvested();
    const profit = await fetchPortfolioProfit();
    const portfolioNetworth = await fetchPortfolioNetworth();

    await renderRight(portfolioNetworth, currentPortfolio.balance, amountInvested, profit);
}

function sortStocks() {
    const searchTerm = searchBar.value.toLowerCase();
    const sort = sortOrder.value;

    filteredStocks = stocks.filter(stock => {
        return stock.stockName.toLowerCase().includes(searchTerm);
    });

    if (sort === "name") {
        filteredStocks.sort((a, b) => a.stockName.localeCompare(b.stockName));
    } else if (sort === "gain") {
        filteredStocks.sort((a, b) => b.change - a.change);
    } else if (sort === "loss") {
        filteredStocks.sort((a, b) => a.change - b.change);
    }

    renderListPage(currentPage);
}

function renderListPage(page) {
    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageStocks = filteredStocks.slice(start, end);

    container.innerHTML = '';
    pageStocks.forEach(stock => renderStockPage(stock));

    closeBtn.addEventListener('click', () => {
        modal.classList.add('hidden');
    });

    document.getElementById('pageInfo').textContent = `Page ${page}`;
}

async function renderStockPage(stock) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    tr.className = 'stock-item';
    if (stock.change > 0) {
        td.style.color = 'green';
        td.textContent = '+'+(stock.change*100).toFixed(2)+'%';
    } else if (stock.change < 0) {
        td.style.color = 'red';
        td.textContent = (stock.change*100).toFixed(2)+'%';
    } else {
        td.textContent = '+0.00%'
    }
    tr.innerHTML += '<td>'+stock.stockName+' ('+stock.stockSymbol+')'+'</td>';
    tr.innerHTML += '<td>$'+stock.currentPrice+'</td>';
    tr.appendChild(td);
    tr.addEventListener('click', () => {
        currentStock = stock;
        stockTitle.innerHTML = stock.stockName +' ('+stock.stockSymbol+')    '+
            '--- $'+stock.currentPrice;
        showModal(stock);
    });
    container.appendChild(tr);
}

async function renderRight(portfolioNetworth, balance, amountInvested, profit) {
    const networthText = document.querySelector(".market #right-networth");
    const balanceText = document.querySelector(".market #right-balance");
    const investmentText = document.querySelector(".market #right-investment");
    const profitText = document.querySelector(".market #right-profit");

    networthText.textContent = "$" + portfolioNetworth;
    balanceText.textContent = "$" + balance;
    investmentText.textContent = "$" + amountInvested;
    profitText.textContent = "$" + profit;

    const transactions = await fetchPortfolioTransactions();
    renderTransactions(transactions.reverse().slice(0,5));
}

function renderTransactions(transactions) {
    const transactionsList = document.querySelector('#transactions-list');
    transactionsList.innerHTML = "";
    for (const t of transactions) {
        const li = document.createElement("li");
        if (t.action === "BUYING") {
            li.style.color = "green";
            li.textContent = "+";
        } else {
            li.style.color = "red";
            li.textContent = "-";
        }
        li.textContent = li.textContent + t.amount + " " + t.stock.stockSymbol;
        transactionsList.appendChild(li);
    }
}

async function fetchPortfolios() {
    const res = await fetch(base + `/portfolios`);
    const portfolios = await res.json();
    return portfolios;
}

async function fetchPortfolioNetworth() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/networth`)
    const networth = await res.json();
    return networth;
}

async function fetchPortfolioTransactions() {
    const res = await fetch(base +`/portfolios/${portfolioId}/transactions`);
    const transactions = await res.json();
    return transactions;
}

async function fetchPortfolioAmountInvested() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/money-invested`)
    const amountInvested = await res.json();
    return amountInvested;
}

async function fetchPortfolioProfit() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/profit`)
    const profit = await res.json();
    return profit;
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
    const nwData = histories.map(pair => ({x: pair.date, y: pair.price}));
    const ctx = stockChart.getContext('2d');
    if (stockPerformChart) {
        stockPerformChart.destroy();
    }
    stockPerformChart = new Chart(ctx, {
        type: 'line',
        data: {
            datasets: [{
                data: nwData,
                fill: 'origin',
                borderColor: 'rgba(75,192,192,1)',
                backgroundColor: 'rgba(75,192,192,0.2)',
                pointRadius: 3,
                tension: 0.2
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {display: false}
            },
            scales: {
                x: {
                    type: 'time',
                    time: {unit: 'day', tooltipFormat: 'MMM yyyy'}
                },
                y: {
                    beginAtZero: false,
                    ticks: {callback: value => '$' + value.toLocaleString()}
                }
            }
        }
    });
}
