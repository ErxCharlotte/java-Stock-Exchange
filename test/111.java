import java.util.*;
import java.io.*;
import java.math.*;

public class Market{
    List<Order> sellBook = new ArrayList<Order>();
    List<Order> buyBook = new ArrayList<Order>();
    List<Trade> inCompletedTrades = new ArrayList<Trade>();
    List<Trade> completedTrades = new ArrayList<Trade>();

    //Creates a new Market object.
    public Market(){ 
    }

    //---Creat a new Comparator for the Price-Time Priority.
    public List<Order> ptpComparator(List<Order> sameProductList){
        if (sameProductList.get(0).isBuy() == false){
            Collections.sort(sameProductList, Comparator.comparing(Order::getPrice).thenComparing(Order::getID));
        }else{
            Collections.sort(sameProductList, Comparator.comparing(Order::getPrice).reversed().thenComparing(Order::getID));
        }
        return sameProductList;

    }

    /*---Used to compare two orders. 
      o1 is the buyer, o2 is the seller*/
    public void dealWithBuyOrders(Order o1, Order o2){
        double o1Amount = o1.getAmount();
        double o1Price = o1.getPrice();
        double o2Amount = o2.getAmount();
        double o2Price = o2.getPrice();
        Trader o1Trader = o1.getTrader();
        Trader o2Trader = o2.getTrader();
        String product = o1.getProduct();
        //Determine whether the price is sufficient
        if (o1Price < o2Price){
            return;
        }
        if (o1Amount > o2Amount){
            Trade trade = new Trade(product, o2Amount, o2Price, o2, o1);
            double totalPrice = o2Amount * o2Price;
            o1Trader.balance -= totalPrice;
            o2Trader.balance += totalPrice;
            o1Trader.importProduct(product, o2Amount);
            o2Trader.exportProduct(product, o2Amount);
            o2.close();
            sellBook.remove(o2);
            //The remainAmount to the order
            o1.amount = o1Amount - o2Amount;
            completedTrades.add(trade);
            return;
        }else if (o1Amount < o2Amount){
            Trade trade = new Trade(product, o1Amount, o2Price, o2, o1);
            double totalPrice = o1Amount * o2Price;
            o1Trader.balance -= totalPrice;
            o2Trader.balance += totalPrice;
            o1Trader.importProduct(product, o2Amount);
            o2Trader.exportProduct(product, o2Amount);
            o1.close();
            buyBook.remove(o1);
            //The remainAmount to the order
            o2.amount = o2Amount - o1Amount;
            completedTrades.add(trade);
            return;
        }else{
            Trade trade = new Trade(product, o1Amount, o2Price, o2, o1); double totalPrice = o1Amount * o2Price;
            o1Trader.balance -= totalPrice;
            o2Trader.balance += totalPrice;
            o1Trader.importProduct(product, o2Amount);
            o2Trader.exportProduct(product, o2Amount);
            o1.close();
            o2.close();
            buyBook.remove(o1);
            sellBook.remove(o2);
            completedTrades.add(trade);
            return;
        }
    }

    /*---Used to compare two orders. 
      o1 is the seller, o2 is the buyer*/
    public void dealWithSellOrders(Order o1, Order o2){
        double o1Amount = o1.getAmount();
        double o1Price = o1.getPrice();
        double o2Amount = o2.getAmount();
        double o2Price = o2.getPrice();
        Trader o1Trader = o1.getTrader();
        Trader o2Trader = o2.getTrader();
        String product = o1.getProduct();
        //Determine whether the price is sufficient
        if (o1Price > o2Price){
            return;
        }
        if (o1Amount > o2Amount){
            Trade trade = new Trade(product, o2Amount, o2Price, o1, o2);
            double totalPrice = o2Amount * o2Price;
            o1Trader.balance += totalPrice;
            o2Trader.balance -= totalPrice;
            o1Trader.exportProduct(product, o2Amount);
            o2Trader.importProduct(product, o2Amount);
            o2.close();
            buyBook.remove(o2);
            //The remainAmount to the order
            o1.amount = o1Amount - o2Amount;
            completedTrades.add(trade);
            return;
        }else if (o1Amount < o2Amount){
            Trade trade = new Trade(product, o1Amount, o2Price, o1, o2);
            double totalPrice = o1Amount * o2Price;
            o1Trader.balance += totalPrice;
            o2Trader.balance -= totalPrice;
            o1Trader.exportProduct(product, o2Amount);
            o2Trader.importProduct(product, o2Amount);
            o1.close();
            sellBook.remove(o1);
            //The remainAmount to the order
            o2.amount = o2Amount - o1Amount;
            completedTrades.add(trade);
            return;
        }else{
            Trade trade = new Trade(product, o1Amount, o2Price, o1, o2); 
            double totalPrice = o1Amount * o2Price;
            o1Trader.balance += totalPrice;
            o2Trader.balance -= totalPrice;
            o1Trader.exportProduct(product, o2Amount);
            o2Trader.importProduct(product, o2Amount);
            o1.close();
            o2.close();
            sellBook.remove(o1);
            buyBook.remove(o2);
            completedTrades.add(trade);
            return;
        }
    }

    /*Processes a sell order using Price-Time Priority. If the order is null or is a buy order, do nothing and return null. 
      If the trader does not have sufficient amount of product, do nothing and return null. 
      For every trade, close its finished orders, remove finished orders from their respective book, 
      increase the balance of the seller (amount of product * price of unit), decrease the balance of the buyer, 
      send the product to the buyer, update the amount for any unfinished orders, and add the trade to the returned list.*/
    public List<Trade> placeSellOrder(Order order){
        List<Trade> completedSellTrades = new ArrayList<Trade>();        
        //The order is null or is a buy order
        if (order == null || order.isBuy() == true){
            return null;
        }
        //Add the order to the sellBook
        sellBook.add(order);
        String product = order.getProduct();
        //Determine whether the book is empty.
        if (buyBook.size() == 0){
            return completedSellTrades;
        }
        //Find the same product in the book.
        List<Order> sameProductInBuyBook = new ArrayList<Order>();
        for (int i = 0; i < buyBook.size(); i ++){
            if ((buyBook.get(i)).getProduct().equals(product)){
                sameProductInBuyBook.add(buyBook.get(i));
            }        
        }
        //Determine whether the sameProductList is empty
        if (sameProductInBuyBook.size() == 0){
            return completedSellTrades;
        }
        //Sort the sameProductList.
        sameProductInBuyBook = ptpComparator(sameProductInBuyBook);
        //Match the product within the book.
        for (int u = 0; u < sameProductInBuyBook.size(); u ++){
            if (order.isClosed()){
                break;
            }
            dealWithSellOrders(order, sameProductInBuyBook.get(u));
        }
        return completedSellTrades;

    }

    /*Processes a buy order using Price-Time Priority. If the order is null or is a sell order, do nothing and return null. 
      For every trade, close its finished orders, remove finished orders from their respective book, increase the balance of the 
      seller (amount of product * price of unit), decrease the balance of the buyer, send the product to the buyer, update the 
      amount for any unfinished orders, and add the trade to the returned list.*/
    public List<Trade> placeBuyOrder(Order order){
        List<Trade> completedBuyTrades = new ArrayList<Trade>();
        //The order is null or is a Sell order
        if (order == null || order.isBuy() == false){
            return null;
        }

        //Add the order to the buyBook
        buyBook.add(order);
        String product = order.getProduct();
        //Determine whether the book is empty.
        if (sellBook.size() == 0){
            return completedBuyTrades;
        }
        //Find the same product in the book.
        List<Order> sameProductInSellBook = new ArrayList<Order>();
        for (int i = 0; i < sellBook.size(); i ++){
            if ((sellBook.get(i)).getProduct().equals(product)){
                sameProductInSellBook.add(sellBook.get(i));
            }        
        }
        //Determine whether the sameProductList is empty
        if (sameProductInSellBook.size() == 0){
            return completedBuyTrades;
        }
        //Sort the sameProductList.
        sameProductInSellBook = ptpComparator(sameProductInSellBook);
        //Match the product within the book.
        for (int u = 0; u < sameProductInSellBook.size(); u ++){
            if (order.isClosed()){
                break;
            }
            dealWithBuyOrders(order, sameProductInSellBook.get(u));
        }
        return completedBuyTrades;
    }

    /*Cancels and closes the given buy order and removes it from the buy book. 
      If the order is null, return false.*/
    public boolean cancelBuyOrder(String order){
        if (order == null){
            return false;
        }
        for (int i = 0; i < buyBook.size(); i ++){
            if ((buyBook.get(i)).getID().equals(order)){
                (buyBook.get(i)).close();
                buyBook.remove((buyBook.get(i)));
                return true;
            }
        }
        return false;
    }

    /*Cancels and closes the given sell order and removes it from the sell book. 
      If the order is null, return false.*/
    public boolean cancelSellOrder(String order){
        if (order == null){
            return false;
        }
        for (int i = 0; i < sellBook.size(); i ++){
            if ((sellBook.get(i)).getID().equals(order)){
                Order sellOrder = sellBook.get(i);
                sellOrder.close();
                sellBook.remove(sellOrder);
                sellOrder.trader.importProduct(sellOrder.product, sellOrder.amount);
                return true;
            }
        }
        return false;
    }

    //Returns the sell book, in temporal order.
    public List<Order> getSellBook(){
        Collections.sort(this.sellBook, Comparator.comparing(Order::getID));
        return this.sellBook;
    }

    //Returns the buy book, in temporal order.
    public List<Order> getBuyBook(){
        Collections.sort(this.buyBook, Comparator.comparing(Order::getID));
        return this.buyBook;
    }

    //Returns the list of completed trades, in temporal order.
    public List<Trade> getTrades(){
        return this.completedTrades;
    }

    /*Filters the list of trades based on if the given trader was involved. 
      Note that a new list should be created, with the original list untouched. 
      Returns null if either parameter is null.*/
    public static List<Trade> filterTradesByTrader(List<Trade> trades, Trader trader){
        if (trades == null || trader == null){
            return null;
        }
        List<Trade> filterTrades = new ArrayList<Trade>();
        for (int i = 0; i < trades.size(); i ++){
            if ((trades.get(i)).involvesTrader(trader)){
                filterTrades.add(trades.get(i));
            }
        }
        return filterTrades;
    }

    /*Filters the list of trades based on if the given product was traded. 
      Note that a new list should be created, with the original list untouched. 
      Returns null if either parameter is null.*/
    public static List<Trade> filterTradesByProduct(List<Trade> trades, String product){
        if (trades == null || product == null){
            return null;
        }
        List<Trade> filterTrades = new ArrayList<Trade>();
        for (int i = 0; i < trades.size(); i ++){
            if ((trades.get(i)).getProduct().equals(product)){
                filterTrades.add(trades.get(i));
            }
        }
        return filterTrades;
    }

    public static void main(String[] args){
        
    }
}