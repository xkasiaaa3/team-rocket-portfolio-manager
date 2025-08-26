package teamrocket.portfolio_manager.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PortfolioNetworthDTO {
    String date;
    BigDecimal networth;

    public PortfolioNetworthDTO(Date date, BigDecimal networth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);
        this.date = formattedDate;
        this.networth = networth;
    }

    public String getDate() {
        return date;
    }

    public BigDecimal getNetworth() {
        return networth;
    }
}
