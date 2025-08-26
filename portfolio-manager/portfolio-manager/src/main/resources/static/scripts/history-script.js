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

let portfolioId = 1;

let transactions = [];
let filteredTransactions = [];
async function loadTransactions() {
    const res = await fetch(base + `/portfolios/${portfolioId}/transactions`);
    const data = await res.json();
    transactions = data.reverse();
    filteredTransactions = transactions;
}

const itemsPerPage = 10;
let currentPage = 1;

renderPage();

const searchBar = document.getElementById("searchBar");
const sortOrder = document.getElementById("sortOrder");
searchBar.addEventListener("input", async () => sortTransactions());
sortOrder.addEventListener("change", async () => sortTransactions());

function sortTransactions() {
    const searchTerm = searchBar.value.toLowerCase();
    const sort = sortOrder.value;

    filteredTransactions = transactions.filter(t => {
        return t.stock.stockSymbol.toLowerCase().includes(searchTerm);
    });

    if (sort === "symbol") {
        filteredTransactions.sort((a, b) => a.stock.stockSymbol.localeCompare(b.stock.stockSymbol));
    } else if (sort === "old") {
        filteredTransactions.sort((a, b) => a.date.localeCompare(b.date));
    }

    renderListPage(currentPage);
}

async function renderPage() {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    const pageDate = document.querySelector('.portfolio-date');
    pageTitle.textContent = currentPortfolio.name;
    pageDate.textContent = new Date(currentPortfolio.currentDate).
        toLocaleDateString('en-US', {weekday: 'short', month: 'short', day: 'numeric', year: 'numeric'});

    await loadTransactions();

    const amountInvested = await fetchPortfolioAmountInvested();
    const profit = await fetchPortfolioProfit();
    const portfolioNetworth = await fetchPortfolioNetworth();

    renderRight(portfolioNetworth, currentPortfolio.balance, amountInvested, profit);

    renderListPage(currentPage);
}

function renderListPage(page) {
    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageTransactions = filteredTransactions.slice(start, end);

    const container = document.querySelector('#history-list tbody');
    container.innerHTML = '';
    pageTransactions.forEach(t => {
        const tr = document.createElement('tr');
        tr.className = 'history-item';

        const change = (t.stock.currentPrice - t.actionPrice) * 100 / t.actionPrice;

        const sChange = document.createElement("td");
        const sAmount = document.createElement("td");
        if (t.action === "BUYING") {
            if (change > 0) {
                sChange.style.color = "green";
                sChange.textContent = "+";
            } else if (change < 0) {
                sChange.style.color = "red";
            }
            sAmount.style.color = "green";
            sAmount.textContent = "+" + t.amount;
        } else {
            if (change < 0) {
                sChange.style.color = "green";
            } else if (change > 0) {
                sChange.style.color = "red";
                sChange.textContent = "+";
            }
            sAmount.style.color = "red";
            sAmount.textContent = "-" + t.amount;
        }
        sChange.textContent += change.toFixed(2)+"%";

        tr.appendChild(sAmount);
        tr.innerHTML += '<td>'+t.stock.stockSymbol+'</td>' + '<td>'+t.date.slice(0,10)+'</td>' +
            '<td>$'+t.actionPrice+'</td>' + '<td>$'+t.stock.currentPrice+'</td>';
        tr.appendChild(sChange);
        container.appendChild(tr);
    });

    document.getElementById('pageInfo').textContent = `Page ${page}`;
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

async function fetchPortfolioTransactions() {
    const res = await fetch(base +`/portfolios/${portfolioId}/transactions`);
    const transactions = await res.json();
    return transactions;
}

async function fetchPortfolioNetworth() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/networth`)
    const networth = await res.json();
    return networth;
}
