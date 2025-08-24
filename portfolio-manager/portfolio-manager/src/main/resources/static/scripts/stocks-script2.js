const stocks = [
  { name: "Apple", symbol: "AAPL", gain: 2.5, owned: true },
  { name: "Tesla", symbol: "TSLA", gain: -1.2, owned: true },
  { name: "Amazon", symbol: "AMZN", gain: 0.8, owned: false },
  { name: "Microsoft", symbol: "MSFT", gain: 3.1, owned: true },
  { name: "Zoom", symbol: "ZM", gain: -4.1, owned: false }
];

const stockList = document.getElementById("stockList");
const searchBar = document.getElementById("searchBar");
const filterOwned = document.getElementById("filterOwned");
const sortOrder = document.getElementById("sortOrder");


function renderStocks() {
  const searchTerm = searchBar.value.toLowerCase();
  const filter = filterOwned.value;
  const sort = sortOrder.value;

  let filtered = stocks.filter(stock => {
    const matchesSearch = stock.name.toLowerCase().includes(searchTerm);
    const matchesOwned = filter === "all" || stock.owned;
    return matchesSearch && matchesOwned;
  });

  if (sort === "name") {
    filtered.sort((a, b) => a.name.localeCompare(b.name));
  } else if (sort === "gain") {
    filtered.sort((a, b) => b.gain - a.gain);
  } else if (sort === "loss") {
    filtered.sort((a, b) => a.gain - b.gain);
  }

  stockList.innerHTML = "";
  filtered.forEach(stock => {
    const li = document.createElement("li");
    li.innerHTML = `<span>${stock.name} (${stock.symbol})</span><span>${stock.gain >= 0 ? "+" : ""}${stock.gain}%</span>`;
    li.style.color = stock.gain >= 0 ? "var(--gain-color)" : "var(--loss-color)";
    stockList.appendChild(li);
  });
}

searchBar.addEventListener("input", renderStocks);
filterOwned.addEventListener("change", renderStocks);
sortOrder.addEventListener("change", renderStocks);

renderStocks();


