import yfinance as yf
import pandas as pd
import time

with open("tickers.txt", "r") as f:
    symbols = [line.strip() for line in f.readlines()]

start_date = "2015-01-01"
end_date = "2025-01-01"

import os
os.makedirs("stock_data", exist_ok=True)

for i, symbol in enumerate(symbols):
    print(symbol)
    try:
        print(f"[{i+1}/{len(symbols)}] Downloading {symbol}...")
        data = yf.download(symbol, start=start_date, end=end_date)
        if not data.empty:
            data.to_csv(f"stock_data/{symbol}.csv")
        else:
            print(f"No data for {symbol}")
        time.sleep(1)
    except Exception as e:
        print(f"Error with {symbol}: {e}")
