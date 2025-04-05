import java.util.*;
import java.io.*;
import java.math.*;

public class Exchange{
    List<Trader> tradersList = new ArrayList<Trader>();
    List<String> tradersIDList = new ArrayList<String>();
    Market market = new Market();
    List<String> ordersIDList = new ArrayList<String>();

    public Exchange(){
    }


    //Handles input and delegates tasks to relevant objects.
    public void run(){
        Scanner userIn = new Scanner(System.in);
        while (true){
            String command = userIn.nextLine();
            String[] commandArray = command.split(" ");

            //EXIT ------------------------
            if (commandArray[0].equalsIgnoreCase("EXIT")){
                System.out.println("$ Have a nice day.");
                return;
            }

            //ADD [id] [balance] ------------------------
            if (commandArray[0].equalsIgnoreCase("ADD")){
                String traderID = commandArray[1];
                double balance = Double.valueOf(commandArray[2]);

                //initial balance is negative
                if (balance < 0){
                    System.out.println("$ Initial balance cannot be negative.");
                    continue;
                }

                //Check the trader whether exist.
                boolean isExist = traderIsExist(tradersList, traderID);
                
                //given ID already exists
                if (isExist == true){
                    System.out.println("$ Trader with given ID already exists.");
                    continue;
                }else{
                    Trader trader = new Trader(traderID, balance);
                    tradersList.add(trader);
                    tradersIDList.add(traderID);
                    System.out.println("$ Success.");
                    continue;
                } 
            }

            //BALANCE [id] ------------------------
            if (commandArray[0].equalsIgnoreCase("BALANCE")){
                String traderID = commandArray[1];

                //Check the trader whether exist.
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }

                Trader trader = getTrader(tradersList, traderID);
                double balance = trader.getBalance();
                System.out.println("$ $" + String.format("%.2f", balance));
                continue;
            }

            //INVENTORY [id] ------------------------
            if (commandArray[0].equalsIgnoreCase("INVENTORY")){
                String traderID = commandArray[1];

                //Check the trader whether exist.
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }
                //Get the trader by the id.
                Trader trader = getTrader(tradersList, traderID);
                List<String> productsList = new ArrayList<String>();
                for (String product : trader.inventory.keySet()){
                    if (trader.inventory.get(product) != 0){
                        productsList.add(product);
                    }
                }
                //Chceck the inventory whether empty.
                if (productsList.size() == 0){
                    System.out.println("$ Trader has an empty inventory.");
                    continue;
                }

                System.out.print("$ ");
                for (String s : productsList){
                    System.out.println(s);
                }
                continue;
            }

            //AMOUNT [id] [product] ------------------------
            if (commandArray[0].equalsIgnoreCase("AMOUNT")){
                String traderID = commandArray[1];
                String product = commandArray[2];

                //Check the trader whether exist.
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }

                Trader trader = getTrader(tradersList, traderID);
                HashMap<String, Double> inventory = trader.inventory;
                if (inventory.containsKey(product) && inventory.get(product) != 0){
                    double amount = inventory.get(product);
                    System.out.println("$ " + String.format("%.2f", amount));
                }else{
                    System.out.println("$ Product not in inventory.");
                }
                continue;
            }

            //SELL [id] [product] [amount] [price] ------------------------
            if (commandArray[0].equalsIgnoreCase("SELL")){
                String traderID = commandArray[1];
                String product = commandArray[2];
                double amount = Double.valueOf(commandArray[3]);
                double price = Double.valueOf(commandArray[4]);

                //Other reasons that can not put the order onto the market.
                if (amount <= 0 || price < 0){
                    System.out.println("$ Order could not be placed onto the market.");
                    continue;
                }
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }
                Trader trader = getTrader(tradersList, traderID);

                //Check the product whether exist.
                if (trader.inventory.containsKey(product) == false){
                    System.out.println("$ Order could not be placed onto the market.");
                    continue;
                }
                if (trader.getAmountStored(product) < amount){
                    System.out.println("$ Order could not be placed onto the market.");
                    continue;
                }

                String orderID = setOrderID(ordersIDList);
                ordersIDList.add(orderID);
                Order sellOrder = new Order(product, false, amount, price, trader, orderID);
                List<Trade> tradeList = market.placeSellOrder(sellOrder);

                //Check the status of the order
                if (sellOrder.isClosed()){
                    System.out.println("$ Product sold in entirety, trades as follows:");
                    for (Trade t : tradeList){
                        System.out.println(t.toString());
                    }
                }else{
                    if (tradeList.size() == 0){
                        System.out.println("$ No trades could be made, order added to sell book.");
                    }else{
                        System.out.println("$ Product sold in part, trades as follows:");
                        for (Trade t : tradeList){
                            System.out.println(t.toString());
                        }
                    }
                }
                continue;
            }

            //BUY [id] [product] [amount] [price]
            if (commandArray[0].equalsIgnoreCase("BUY")){
                String traderID = commandArray[1];
                String product = commandArray[2];
                double amount = Double.valueOf(commandArray[3]);
                double price = Double.valueOf(commandArray[4]);
                //Other reasons that can not put the order onto the market.
                if (amount <= 0 || price < 0){
                    System.out.println("$ Order could not be placed onto the market.");
                    continue;
                }
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }
                Trader trader = getTrader(tradersList, traderID);

                String orderID = setOrderID(ordersIDList);
                ordersIDList.add(orderID);
                Order buyOrder = new Order(product, true, amount, price, trader, orderID);
                List<Trade> tradeList = market.placeBuyOrder(buyOrder);

                //Check the status of the order
                if (buyOrder.isClosed()){
                    System.out.println("$ Product bought in entirety, trades as follows:");
                    for (Trade t : tradeList){
                        System.out.println(t.toString());
                    }
                }else{
                    if (tradeList.size() == 0){
                        System.out.println("$ No trades could be made, order added to buy book.");
                    }else{
                        System.out.println("$ Product bought in part, trades as follows:");
                        for (Trade t : tradeList){
                            System.out.println(t.toString());
                        }
                    }
                }
                continue;
            }

            //IMPORT [id] [product] [amount]
            if (commandArray[0].equalsIgnoreCase("IMPORT")){
                String traderID = commandArray[1];
                String product = commandArray[2];
                double amount = Double.valueOf(commandArray[3]);

                if (amount <= 0){
                    System.out.println("$ Could not import product into market.");
                    continue;
                }
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }
                Trader trader = getTrader(tradersList, traderID);
                trader.importProduct(product, amount);
                double totalAmount = trader.getAmountStored(product);
                System.out.println("$ Trader now has " + String.format("%.2f", totalAmount) + " units of " + product +".");
                continue;
            }

            //EXPORT [id] [product] [amount]
            if (commandArray[0].equalsIgnoreCase("EXPORT")){
                String traderID = commandArray[1];
                String product = commandArray[2];
                double amount = Double.valueOf(commandArray[3]);

                if (amount <= 0){
                    System.out.println("$ Could not export product out of market.");
                    continue;
                }
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }
                Trader trader = getTrader(tradersList, traderID);
                
                boolean productExist = false;
                Set<String> keySet = trader.inventory.keySet();
                for (String key : keySet) {
                    if (key.equals(product)){
                        productExist = true;
                    }
                }
                if (productExist == false){
                    System.out.println("$ Could not export product out of market.");
                    continue;
                }

                double nowAmount = trader.getAmountStored(product);
                if (nowAmount < amount){
                    System.out.println("$ Could not export product out of market.");
                    continue;
                }

                trader.exportProduct(product, amount);
                double totalAmount = trader.getAmountStored(product);
                if (totalAmount == 0){
                    System.out.println("$ Trader now has no units of " + product +".");
                    continue;
                }
                System.out.println("$ Trader now has " + String.format("%.2f", totalAmount) + " units of " + product +".");
                continue;
            }

            //CANCEL SELL [order]
            if (commandArray[0].equalsIgnoreCase("CANCEL") && commandArray[1].equalsIgnoreCase("SELL")){
                String orderID = commandArray[2];
                boolean isExist = orderIsExist(false, orderID);
                if (isExist == false){
                    System.out.println("$ No such order in sell book.");
                    continue;
                }
                market.cancelSellOrder(orderID);
                System.out.println("$ Order successfully cancelled.");
                continue;
            }

            //CANCEL BUY [order]
            if (commandArray[0].equalsIgnoreCase("CANCEL") && commandArray[1].equalsIgnoreCase("BUY")){
                String orderID = commandArray[2];
                boolean isExist = orderIsExist(true, orderID);
                if (isExist == false){
                    System.out.println("$ No such order in buy book.");
                    continue;
                }
                market.cancelBuyOrder(orderID);
                System.out.println("$ Order successfully cancelled.");
                continue;
            }

            //ORDER [order]
            if (commandArray[0].equalsIgnoreCase("ORDER")){
                String orderID = commandArray[1];
                //No order in the market
                if (market.sellBook.size() == 0 && market.buyBook.size() == 0){
                    System.out.println("$ No orders in either book in the market.");
                    continue;
                }
                //No order has the given ID
                boolean buyBookExist = orderIsExist(true, orderID);
                boolean sellBookExist = orderIsExist(false, orderID);
                boolean isExist = buyBookExist || sellBookExist;
                if (isExist == false){
                    System.out.println("$ Order is not present in either order book.");
                    continue;
                }

                List<Order> book = market.getSellBook();
                if (buyBookExist == true){
                    book = market.getBuyBook();
                }

                for (int u = 0; u < book.size(); u ++){
                    if (book.get(u).getID().equals(orderID)){
                        Order order = book.get(u);
                        System.out.println("$ " + order.toString());
                        }
                    }
                continue;
            }

            //TRADERS
            if (commandArray[0].equalsIgnoreCase("TRADERS")){
                if (tradersIDList.size() == 0){
                    System.out.println("$ No traders in the market.");
                    continue;
                }
                tradersIDList.sort(Comparator.naturalOrder());
                System.out.print("$ ");
                for (String id : tradersIDList){
                    System.out.println(id);
                }
                continue;
            }
            
            //TRADES
            if (commandArray[0].equalsIgnoreCase("TRADES") && commandArray.length == 1){
                if (market.completedTrades.size() == 0){
                    System.out.println("$ No trades have been completed.");
                    continue;
                }
                System.out.print("$ ");
                for (int i = 0; i < market.completedTrades.size(); i++){
                    Trade trade = market.completedTrades.get(i);
                    System.out.println(trade.toString());
                }
                continue;
            }

            //TRADES TRADER [id]
            if (commandArray[0].equalsIgnoreCase("TRADES") && commandArray[1].equalsIgnoreCase("TRADER")){
                String traderID = commandArray[2];
                boolean isExist = traderIsExist(tradersList, traderID);
                if (isExist == false){
                    System.out.println("$ No such trader in the market.");
                    continue;
                }

                Trader trader = getTrader(tradersList, traderID);
                List<Trade> filterTrades = market.filterTradesByTrader(market.completedTrades, trader);
                if (filterTrades.size() == 0){
                    System.out.println("$ No trades have been completed by trader.");
                    continue;
                }

                System.out.print("$ ");
                for (Trade t : filterTrades){
                    System.out.println(t.toString());
                }
                continue;
            }

            //TRADES PRODUCT [product]
            if (commandArray[0].equalsIgnoreCase("TRADES") && commandArray[1].equalsIgnoreCase("PRODUCT")){
                String product = commandArray[2];
                List<Trade> filterTrades = market.filterTradesByProduct(market.completedTrades, product);
                if (filterTrades.size() == 0){
                    System.out.println("$ No trades have been completed with given product.");
                    continue;
                }

                System.out.print("$ ");
                for (Trade t : filterTrades){
                    System.out.println(t.toString());
                }
                continue;

            }
                
            //BOOK SELL
            if (commandArray[0].equalsIgnoreCase("BOOK") && commandArray[1].equalsIgnoreCase("SELL")){
                List<Order> book = market.getSellBook();
                if (book.size() == 0){
                    System.out.println("$ The sell book is empty.");
                    continue;
                }

                System.out.print("$ ");
                for (Order o : book){
                    System.out.println(o.toString());
                }
                continue;
            }

            //BOOK BUY
            if (commandArray[0].equalsIgnoreCase("BOOK") && commandArray[1].equalsIgnoreCase("BUY")){
                List<Order> book = market.getBuyBook();
                if (book.size() == 0){
                    System.out.println("$ The buy book is empty.");
                    continue;
                }

                System.out.print("$ ");
                for (Order o : book){
                    System.out.println(o.toString());
                }
                continue;
            }

            //SAVE [trader-path] [trades-path]
            if (commandArray[0].equalsIgnoreCase("SAVE")){
                if (commandArray.length != 3){
                    System.out.println("$ Unable to save logs to file.");
                    continue;
                }
                try{
                    String traderPath = commandArray[1];
                    String tradePath = commandArray[2];
                    Trader.writeTraders(tradersList, traderPath);
                    Trade.writeTrades(market.completedTrades, tradePath);
                    System.out.println("$ Success.");
                    continue;
                }catch (Exception e1){
                    System.out.println("$ Unable to save logs to file.");
                    continue;
                }
            }

            //BINARY [trader-path] [trades-path]
            if (commandArray[0].equalsIgnoreCase("BINARY")){
                if (commandArray.length != 3){
                    System.out.println("$ Unable to save logs to file.");
                    continue;
                }
                try{
                    String traderPath = commandArray[1];
                    String tradePath = commandArray[2];
                    Trader.writeTradersBinary(tradersList, traderPath);
                    Trade.writeTradesBinary(market.completedTrades, tradePath);
                    System.out.println("$ Success.");
                    continue;
                }catch (Exception e1){
                    System.out.println("$ Unable to save logs to file.");
                    continue;
                }
            }

            return;
        }
    }

    //Get the trader depend on the traderID
    public Trader getTrader(List<Trader> tradersList, String traderID){
        for (int i = 0; i < tradersList.size(); i ++){
            if (tradersList.get(i).getID().equals(traderID)){
                Trader trader = tradersList.get(i);
                return trader;
            }
        }
        return null;
    }

    //Check the trader wheter exist
    public boolean traderIsExist(List<Trader> tradersList, String traderID){
        boolean isExist = false;
        if (tradersList.size() == 0){
            return false;
        }
        for (int i = 0; i < tradersList.size(); i ++){
            if (tradersList.get(i).getID().equals(traderID)){
                isExist = true;
            }
        }
        return isExist;
    }

    //Check the order wheter exist
    public boolean orderIsExist(boolean isBuy, String orderID){
        boolean isExist = false;
        List<Order> book = market.sellBook;
        if (isBuy == true){
            book = market.buyBook;
        }

        if (book.size() == 0){
            return false;
        }
        for (int i = 0; i < book.size(); i ++){
            if (book.get(i).getID().equals(orderID)){
                isExist = true;
            }
        }
        return isExist;
    }

    //Set the ID to the Order
    public String setOrderID(List<String> ordersIDList){
        int totalNumber = ordersIDList.size();
        StringBuffer sbuffer = new StringBuffer("0000");

        //0-16:
        String hexString = (Integer.toHexString(totalNumber)).toUpperCase();
        int numOfString = hexString.length();
        sbuffer.append(hexString);
        sbuffer.delete(3-numOfString, 3);
        return sbuffer.toString();
    }


    //Runs the program.
    public static void main(String[] args){
        Exchange exchange = new Exchange();
        exchange.run();
        
    }
}