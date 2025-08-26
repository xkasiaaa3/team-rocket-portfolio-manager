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
async function loadTransactions() {
    const res = await fetch(base + `/portfolios/${portfolioId}/transactions`);
    const data = await res.json();
    transactions = data.reverse();
}

const itemsPerPage = 10;
let currentPage = 1;

renderPage();

async function renderPage() {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    const pageDate = document.querySelector('.portfolio-date');
    pageTitle.textContent = currentPortfolio.name;
    pageDate.textContent = new Date(currentPortfolio.currentDate).
        toLocaleDateString('en-US', {weekday: 'short', month: 'short', day: 'numeric', year: 'numeric'});

    await loadTransactions();

    renderListPage(currentPage);
}

function renderListPage(page) {
    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageTransactions = transactions.slice(start, end);

    const container = document.getElementById('history-list');
    container.innerHTML = ''; // clear previous items
    pageTransactions.forEach(t => {
        const li = document.createElement('li');
        li.className = 'history-item';

        const sAmount = document.createElement("span");
        sAmount.className = "amount";
        const sDate = document.createElement("span");
        sDate.className = "date";
        sDate.textContent = t.date.slice(0,10);
        const sPrice = document.createElement("span");
        sPrice.className = "price";
        sPrice.textContent = "$" + t.actionPrice;
        const sChange = document.createElement("span");
        sChange.className = "change";
        sChange.textContent = "%CHANGE";

        if (t.action === "BUYING") {
            li.style.color = "green";
            sAmount.textContent = "+" + t.amount + " " + t.stock.stockSymbol;
        } else {
            li.style.color = "red";
            sAmount.textContent = "-" + t.amount + " " + t.stock.stockSymbol;
        }
        li.append(sAmount, sDate, sPrice, sChange);
        container.appendChild(li);
    });

    document.getElementById('pageInfo').textContent = `Page ${page}`;
}

async function fetchPortfolios() {
    const res = await fetch(base + `/portfolios`);
    const portfolios = await res.json();
    return portfolios;
}
