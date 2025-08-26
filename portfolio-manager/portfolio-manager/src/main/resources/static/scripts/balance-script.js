const base = "http://localhost:8080"

document.querySelector('.hamburger').addEventListener('click', () => {
  alert('Open portfolio selector or add new portfolio');
});

document.querySelector('.user').addEventListener('click', () => {
  alert('Open user settings or profile');
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

addSubmitBtn.onclick = async (portfolioId = 1) => {
    fetch(base + `/portfolios/1/balance`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: addInput.value
    })
    addInput.value = '';
    addModal.style.display = 'none';
    renderPage();
}

withdrawSubmitBtn.onclick = async (portfolioId = 1) => {
    fetch(base + `/portfolios/1/balance`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: -withdrawInput.value
    })
    withdrawInput.value = '';
    withdrawModal.style.display = 'none';
    renderPage();
}

renderPage();

async function renderPage(portfolioId = 1) {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    pageTitle.textContent = currentPortfolio.name;

    const funds = document.querySelector('.funds-display');
    funds.textContent = '$ ' + currentPortfolio.balance;

    const transactions = await fetchPortfolioTransactions(portfolioId);
    renderTransactions(transactions);
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
        li.textContent = li.textContent + t.amount + " " + t.stock.stockName;
        transactionsList.appendChild(li);
    }
}

async function fetchPortfolios() {
    const res = await fetch(base + `/portfolios`);
    const portfolios = await res.json();
    return portfolios;
}

async function fetchPortfolioTransactions(portfolioId = 1) {
    const res = await fetch(base +`/portfolios/${portfolioId}/transactions`);
    const transactions = await res.json();
    return transactions;
}