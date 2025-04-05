import java.util.*;
import java.io.*;
import java.math.*;

public class Trade{
    String tradeProduct;
    double tradeAmount;
    double tradePrice;
    Order sellOrder;
    Order buyOrder;
    
    //Creates a new Trade object.
    public Trade(String product, double amount, double price, Order sellOrder, Order buyOrder){
        this.tradeProduct = product;
        this.tradeAmount = amount;
        this.tradePrice = price;
        this.sellOrder = sellOrder;
        this.buyOrder = buyOrder;
    }

    //Returns the product string.
    public String getProduct(){
        return this.tradeProduct;
    }

    //Returns the amount being traded.
    public double getAmount(){
        return this.tradeAmount;
    }

    //Returns the corresponding sell order.
    public Order getSellOrder(){
        return this.sellOrder;
    }

    //Returns the corresponding buy order.
    public Order getBuyOrder(){
        return this.buyOrder;
    }

    //Returns the agreed upon price.
    public double getPrice(){
        return this.tradePrice;
    }

    /*Returns the string representation of the trade. 
      Format: SELLER->BUYER: AMOUNTxPRODUCT for PRICE*/
    public String toString(){
        String sellerID = ((this.sellOrder).getTrader()).getID();
        String buyerID = ((this.buyOrder).getTrader()).getID();

        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append(sellerID + "->" + buyerID + ": ");
        sbuffer.append(String.format("%.2f", this.tradeAmount) + "x");
        sbuffer.append(this.tradeProduct +" for $");
        sbuffer.append(String.format("%.2f", this.tradePrice) + ".");
        return sbuffer.toString();
    }

    //Returns true if the given trader is either the seller or the buyer.
    public boolean involvesTrader(Trader trader){
        Trader seller = (this.sellOrder).getTrader();
        Trader buyer = (this.buyOrder).getTrader();
        boolean isInvolves = false;
        if (trader == seller || trader == buyer){
            isInvolves = true;
        }
        return isInvolves;
    }

    /*Writes the list of trades to file in ASCII encoding. 
    For every trade in the provided list, write the String representation on a new line in the given file. 
    If either argument is invalid, do nothing.*/
    public static void writeTrades(List<Trade> trades, String path){
        if (trades == null || path == null){
            return;
        }
        File f = new File(path);
        if (f.exists() == false){
            try{
                f.createNewFile();
            }catch (Exception e0){
                return;
            }
        }
        try{
            PrintWriter wr = new PrintWriter(new FileWriter((f)));
            for (int i = 0; i < trades.size(); i ++){
                wr.println((trades.get(i)).toString());
            }
            wr.close();
        }catch (Exception e1){
            return;
        }

    }

    /*Writes the list of trades to file in binary encoding. 
    For every trade in the provided list, write the String representation, followed by the Unit Separator byte. 
    If either argument is invalid, do nothing.*/
    public static void writeTradesBinary(List<Trade> trades, String path){
        if (trades == null || path == null){
            return;
        }

        try{
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream binaryOutput = new DataOutputStream(f);
            for (int i = 0; i < trades.size(); i ++){
                String tradeString = (trades.get(i)).toString();
                binaryOutput.writeUTF(tradeString);
                
                StringBuffer sbuffer = new StringBuffer();
                sbuffer.append((char) Integer.parseInt("31"));
                binaryOutput.writeUTF(sbuffer.toString());
            }
            binaryOutput.close();
            }catch(IOException e){
                return;
            }
    }
}
             