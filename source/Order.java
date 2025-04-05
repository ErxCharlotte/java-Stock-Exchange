public class Order{

    boolean orderClosed = false; 
    String product;
    boolean buy;
    double amount;
    double price;
    Trader trader;
    String orderID;

    //Creat a new Order object.
    public Order(String product, boolean buy, double amount, double price, Trader trader, String id){
        this.product = product;
        this.buy = buy;
        this.amount = amount;
        this.price = price;
        this.trader = trader;
        this.orderID = id;
    }

    //Returns the product sting.
    public String getProduct(){
        return this.product;
    }

    //Returns true if the order is to buy, false if sell.
    public boolean isBuy(){
        return this.buy;
    }

    //Returns the amount of product requested.
    public double getAmount(){
        return this.amount;
    }

    //Returns the invoking trader.
    public Trader getTrader(){
        return this.trader;
    }

    //Closes the order.
    public void close(){
        orderClosed = true;
        return;
    }

    //Returns true if the order has been closed.
    public boolean isClosed(){
        return orderClosed;
    }

    //Returns the limiting price of the order.
    public double getPrice(){
        return this.price;
    }

    //Returns the id of the order.
    public String getID(){
        return this.orderID;
    }

    /*Adjusts the amount requested in the order by adding the change. 
      Only changes if the new amount is greater than zero.*/
    public void adjustAmount(double change){
        double nowAmount = this.getAmount();
        if ((nowAmount += change) > 0){
            this.amount += change;
        }
        return;
    }

    /*Returns the string representation of the order.
      Format: ID: [BUY/SELL] AMOUNTxPRODUCT @ $PRICE*/
    public String toString(){
        String state;
        if (this.isBuy()){
            state = "BUY";
        }else{
            state = "SELL";
        }
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append(this.orderID + ": ");
        sbuffer.append(state + " ");
        sbuffer.append(String.format("%.2f", this.amount) + "x" + this.product + " ");
        sbuffer.append("@ $" + String.format("%.2f", this.price));
        return sbuffer.toString();
    }
}