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

function renderChart(histories) {
const ctx = document.getElementById('portfolio-chart').getContext('2d');
const nwData = histories.map(pair => ({x: pair.date, y: pair.networth}));
if (networthChart) networthChart.destroy();
networthChart = new Chart(ctx, {
  type: 'line',

  // 1. Data: time-series points for net worth
  data: {
    datasets: [{
      label: 'Portfolio Net Worth',
      data: nwData,
      fill: 'origin',                  // area under the line
      borderColor: 'rgba(75,192,192,1)',
      backgroundColor: 'rgba(75,192,192,0.2)',
      pointRadius: 3,
      tension: 0.2                    // smooth curves
    }]
  },

  // 2. Options: time axis, currency formatting, responsive layout
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
