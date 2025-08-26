const base = "http://localhost:8080"

document.querySelector('.hamburger').addEventListener('click', () => {
  alert('Open portfolio selector or add new portfolio');
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

let portfolioId = 1;
let networthChart;

renderPage();

async function renderPage() {
    const portfolios = await fetchPortfolios();
    const currentPortfolio = portfolios[portfolioId-1];

    const pageTitle = document.querySelector('.portfolio-name');
    const pageDate = document.querySelector('.portfolio-date');
    pageTitle.textContent = currentPortfolio.name;
    pageDate.textContent = currentPortfolio.currentDate.slice(0,10);

    const performanceTitle = document.querySelector('.portfolio h1');
    const portfolioNetworth = await fetchPortfolioNetworth(portfolioId);
    performanceTitle.textContent = 'Networth: $' + portfolioNetworth;
    renderChart();
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

function renderChart() {
const ctx = document.getElementById('portfolio-chart').getContext('2d');
if (networthChart) {
    networthChart.destroy();
}
networthChart = new Chart(ctx, {
  type: 'line',

  // 1. Data: time-series points for net worth
  data: {
    datasets: [{
      label: 'Portfolio Net Worth',
      data: [
        { x: '2025-01-01', y: 100000 },
        { x: '2025-02-01', y: 105250 },
        { x: '2025-03-01', y: 98000 },
        // …more monthly snapshots
      ],
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
        text: 'Portfolio Net Worth Over Time'
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
