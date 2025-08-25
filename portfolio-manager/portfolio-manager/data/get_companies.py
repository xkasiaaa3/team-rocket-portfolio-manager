import csv

input_csv = "fortune1000_2024.csv"
output_txt = "tickers.txt"

with open(input_csv, "r", encoding="utf-8") as csvfile:
    reader = csv.reader(csvfile)
    header = next(reader)

    tickers = [row[2].strip() for row in reader if len(row) > 2 and row[2].strip()]

with open(output_txt, "w", encoding="utf-8") as txtfile:
    for ticker in tickers:
        txtfile.write(ticker + "\n")

print(f"Extracted {len(tickers)} tickers to {output_txt}")
