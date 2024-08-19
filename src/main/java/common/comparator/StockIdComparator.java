package common.comparator;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.exchange.app.stock.model.entity.StockInfo;

@Component
public class StockIdComparator implements Comparator<StockInfo>
{

    @Override
    public int compare(StockInfo stock1, StockInfo stock2)
    {
        return stock1.getName().compareTo(stock2.getName());
    }
}