const stockItems = document.querySelectorAll('.stock-item');
const modal = document.getElementById('stockModal');
const closeBtn = document.querySelector('.close-button');
const stockTitle = document.getElementById('stockTitle');
const stockChart = document.getElementById('stockChart');

stockItems.forEach(item => {
  item.addEventListener('click', () => {
    const symbol = item.dataset.symbol;
    stockTitle.textContent = `${item.textContent}`;
    showModal(symbol);
  });
});

closeBtn.addEventListener('click', () => {
  modal.classList.add('hidden');
});

function showModal(symbol) {
  modal.classList.remove('hidden');
  renderChart(symbol); // Load chart data dynamically
}

function renderChart(symbol) {
  // Example using Chart.js
  const ctx = stockChart.getContext('2d');
  new Chart(ctx, {
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
