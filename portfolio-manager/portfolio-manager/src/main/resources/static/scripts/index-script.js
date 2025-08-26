const base = "http://localhost:8080"

document.querySelector('.hamburger').addEventListener('click', () => {
  alert('Open portfolio selector or add new portfolio');
});

document.querySelector('.forward-button').addEventListener('click', () => {
    forwardDay();
    setTimeout(() => {renderPage();}, 5000);
});

async function forwardDay() {
    await fetch(base + `/portfolios/${portfolioId}/forward-date`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
    })
}

let portfolioId = 1;
let networthChart;

renderPage();

async function renderPage() {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    const pageDate = document.querySelector('.portfolio-date');
    pageTitle.textContent = currentPortfolio.name;
    pageDate.textContent = new Date(currentPortfolio.currentDate).
        toLocaleDateString('en-US', {weekday: 'short', month: 'short', day: 'numeric', year: 'numeric'});

    const performanceTitle = document.querySelector('.portfolio h1');
    const performanceChange = document.querySelector('#networth-change');
    const portfolioNetworth = await fetchPortfolioNetworth();
    var portfolioChange = await fetchPortfolioChange();
    portfolioChange *= 100;
    performanceChange.textContent = '';
    if (portfolioChange > 0) {
        performanceChange.textContent = '+';
        performanceChange.style.color = 'green';
    } else if (portfolioChange < 0) {
        performanceChange.style.color = 'red';
    } else {
        performanceChange.style.color = 'black';
    }

    performanceTitle.textContent = 'Networth: $' + portfolioNetworth;
    performanceChange.textContent += portfolioChange.toFixed(2) + "%";
    const histories = await fetchPortfolioHistories();
    renderChart(histories);

    const amountInvested = await fetchPortfolioAmountInvested();
    const profit = await fetchPortfolioProfit();
    document.querySelector("#span-investment").textContent = "$" + amountInvested;
    document.querySelector("#span-networth").textContent = "$" + profit;

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
    const res = await fetch(base + "/portfolios");
    const portfolios = await res.json();
    return portfolios;
}

async function fetchPortfolioNetworth() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/networth`)
    const networth = await res.json();
    return networth;
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

async function fetchPortfolioChange() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/change`)
    const change = await res.json();
    return change;
}

async function fetchPortfolioHistories() {
    const res = await fetch(`${base}/portfolios/${portfolioId}/histories`)
    const histories = await res.json();
    return histories;
}

async function fetchPortfolioTransactions() {
    const res = await fetch(base +`/portfolios/${portfolioId}/transactions`);
    const transactions = await res.json();
    return transactions;
}

function renderChart(histories) {
    const ctx = document.getElementById('portfolio-chart').getContext('2d');
    const nwData = histories.map(pair => ({x: pair.date, y: pair.networth}));
    if (networthChart) networthChart.destroy();
    networthChart = new Chart(ctx, {
      type: 'line',

      data: {
        datasets: [{
          label: 'Portfolio Net Worth',
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
        maintainAspectRatio: true,

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

        plugins: {
          title: {
            display: true,
            text: 'Portfolio Net Worth Over Last Month'
          },
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: ctx => {
                const val = ctx.parsed.y;
                return ` $${val.toLocaleString()}`;
              }
            }
          }
        }
      }
    });
}
