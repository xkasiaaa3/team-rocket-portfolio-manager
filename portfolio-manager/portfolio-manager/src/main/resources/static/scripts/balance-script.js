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

const addModal = document.getElementById('addFundsModal');
const withdrawModal = document.getElementById('withdrawFundsModal');
const addBtn = document.querySelector('.action-button.plus');
const withdrawBtn = document.querySelector('.action-button.minus');
const addSpan = document.querySelector('.close-plus');
const withdrawSpan = document.querySelector('.close-minus');
const addSubmitBtn = document.getElementById('submitAddFunds');
const withdrawSubmitBtn = document.getElementById('submitWithdrawFunds');
const addInput = document.getElementById('addFundsInput');
const withdrawInput = document.getElementById('withdrawFundsInput');
const output = document.getElementById('output');

addBtn.onclick = () => {addModal.style.display = 'block';};
withdrawBtn.onclick = () => {withdrawModal.style.display = 'block';};
addSpan.onclick = () => {addModal.style.display = 'none';};
withdrawSpan.onclick = () => {withdrawModal.style.display = 'none';};

window.onclick = e => {
  if (e.target === addModal) {
    addModal.style.display = 'none';
  } else if (e.target === withdrawModal) {
    withdrawModal.style.display = 'none';
  }
};

addSubmitBtn.onclick = async () => {
    fetch(base + `/portfolios/${portfolioId}/balance`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: addInput.value
    })
    addInput.value = '';
    addModal.style.display = 'none';
    renderPage();
}

withdrawSubmitBtn.onclick = async () => {
    fetch(base + `/portfolios/${portfolioId}/balance`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: -withdrawInput.value
    })
    withdrawInput.value = '';
    withdrawModal.style.display = 'none';
    renderPage();
}

document.querySelector('.forward-button').addEventListener('click', async () => {
    await forwardDay();
    renderPage();
});

async function forwardDay() {
    await fetch(base + `/portfolios/${portfolioId}/forward-date`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
    })
}

let portfolioId = 1;

renderPage();

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

    const funds = document.querySelector('.funds-display');
    funds.textContent = '$ ' + currentPortfolio.balance;

    const amountInvested = await fetchPortfolioAmountInvested();
    const profit = await fetchPortfolioProfit();
    const portfolioNetworth = await fetchPortfolioNetworth();

    renderRight(portfolioNetworth, currentPortfolio.balance, amountInvested, profit);
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