public List<Order> ptpComparator(List<Order> sameProductList){
    if (sameProductList.get(0).isBuy() == false){
        Collections.sort(sameProductList, Comparator.comparing(Order::getPrice).thenComparing(Order::getID));
    }else{
        Collections.sort(sameProductList, Comparator.comparing(Order::getPrice).reversed().thenComparing(Order::getID));
    }
    return sameProductList;

}



public List<Trade> placeSellOrder(Order order){
    //Creat a new list to store the trades that completed by this order.
    List<Trade> completedSellList = new ArrayList<Trade>();

    //Judge whether the order is null or is a buy order.
    if (order == null || order.isBuy() == true){
        return null;
    }

    //Get the informations in this order.
    Trader sellTrader = order.getTrader();
    String sellProduct = order.getProduct();
    double sellAmount = order.getAmount();
    double sellPrice = order.getPrice();
    String sellID = order.getID();

    //Get the product amount and judge it whether enough to sell.
    double nowAmount = sellTrader.getAmountStored(sellProduct);
    if (sellAmount > nowAmount){
        return null;
    }

    //Judge whether the BuyBook is empty.
    if (buyBook.size() == 0){
        return completedSellList;
    }

    //Match the same product that in the BuyBook.
    List<Order> sameProductList = new ArrayList<Order>();
    for (int u = 0; u < buyBook.size(); u ++){
        if (buyBook.get(u).getProduct().equals(sellProduct)){
            sameProductList.add(buyBook.get(u));
        }
    }

    //No same product.
    if (sameProductList.size() == 0){
        return completedSellList;
    }

    //Sort the same product list in the price-time order.
    sameProductList = ptpComparator(sameProductList);

    //Export the product in the sell trader's inventory.
    sellTrader.exportProduct(product, sellAmount);

    //Deal the order with these buy orders.
    for (int i = 0; i < sameProductList.size(); i ++){
        //Find the informations in the buy order.
        Order buyOrder = sameProductList.get(i);
        Trader buyTrader = buyOrder.getTrader();
        double buyAmount = buyOrder.getAmount();
        double buyPrice = buyOrder.getPrice();

        //The buyPrice is less than the sellPrice.
        if (buyPrice < sellPrice){
            continue;
        }


        //1: sellAmount > buyAmount (sold in part)
        if (sellAmount > buyAmount){
            //import product to the buyer and deal the money.
            buyTrader.importProduct(sellProduct, buyAmount);
            sellOrder.amount -= buyAmount;
            double tradePrice = buyPrice * buyAmount;
            buyTrader.balance -= tradePrice;
            sellTrader.balance += tradePrice;

            //Close the buy order and delete it in the buy book.
            buyOrder.close();
            buyBook.remove(buyOrder);
            Trade trade = (sellProduct, buyAmount, buyPrice, sellOrder, buyOrder);
            completedSellList.add(trade);
            completedTrades.add(trade);
            continue;

        //2: sellAmount < buyAmount (sold the all)
        }else if (sellAmount < buyAmount){
            //import product to the buyer and deal the money.
            buyTrader.importProduct(sellProduct, sellAmount);
            sellOrder.amount -= sellAmount;
            double tradePrice = buyPrice * sellAmount;
            buyTrader.balance -= tradePrice;
            sellTrader.balance += tradePrice;

            //Close the sell order and delete it in the sell book.
            sellOrder.close();
            sellBook.remove(sellOrder);
            Trade trade = (sellProduct, sellAmount, buyPrice, sellOrder, buyOrder);
            completedSellList.add(trade);
            completedTrades.add(trade);
            continue;

        //3: sellAmount = buyAmount (sold and bought all)
        }else{
            //import product to the buyer and deal the money.
            buyTrader.importProduct(sellProduct, sellAmount);
            sellOrder.amount -= sellAmount;
            double tradePrice = buyPrice * sellAmount;
            buyTrader.balance -= tradePrice;
            sellTrader.balance += tradePrice;

            //Close these order.
            sellOrder.close();
            sellBook.remove(sellOrder);
            buyOrder.close();
            buyBook.remove(buyOrder);
            Trade trade = (sellProduct, sellAmount, buyPrice, sellOrder, buyOrder);
            completedSellList.add(trade);
            completedTrades.add(trade);
            continue;
            }
        }
    }
