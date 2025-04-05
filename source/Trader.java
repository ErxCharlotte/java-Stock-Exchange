import java.util.*;
import java.io.*;
import java.math.*;

public class Trader{
    String traderID;
    double balance;
    double AmountOfProduct;
    HashMap<String, Double> inventory = new HashMap<String, Double>();
    List<String> productsList = new ArrayList<String>();

    //Creates a new Trader object.
    public Trader(String id, double balance){
        this.traderID = id;
        this.balance = balance;
    }

    //Returns the trader's id.
    public String getID(){
        return this.traderID;
    }

    //Returns the trader's current balance.
    public double getBalance(){
        return this.balance;
    }

    /*Imports product into the trader's inventory. 
      If the product is null or the amount is less than or equal to zero, return -1.0.*/
    public double importProduct(String product, double amount){
        
        if (product == null || amount <=  0){
            return -1.0;
        }
        if (inventory.containsKey(product)){
            double oldAmount = inventory.get(product);
            inventory.replace(product, oldAmount + amount);
        }else{
            inventory.put(product, amount);
        }
        return inventory.get(product);
    }

    /*Exports product out of the trader's inventory. 
      If the product is null or the amount is less than or equal to zero, return -1.0. 
      If the trader does not have the product, or does not have enough of the product, 
      do nothing and return -1.0.*/
    public double exportProduct(String product, double amount){
        if (product == null || amount <= 0){
            return -1.0;
        }
        if (inventory.containsKey(product) == false || inventory.get(product) == 0){
            return -1.0;
        }else if (inventory.get(product) < amount){
            return -1.0;
        }
        double oldAmount = inventory.get(product);
        double nowAmount = oldAmount - amount;
        inventory.replace(product, nowAmount);
        return inventory.get(product);
    }

    /*Returns the amount of the given product the trader has in their inventory. 
      If the product is null, return -1.0.*/
    public double getAmountStored(String product){
        if (product == null){
            return -1.0;
        }else if (inventory.containsKey(product) == false){
            return 0.0;
        }
        return inventory.get(product);
    }

    //Returns a list of all the products in the trader's inventory, ordered alphabetically.
    public List<String> getProductsInInventory(){
        List<String> productsList = new ArrayList<String>();
        for (String product : inventory.keySet()){
            if (inventory.get(product) != 0){
                productsList.add(product);
            }
        }
        productsList.sort(Comparator.naturalOrder());
        return productsList;
    }

    /*Adjusts the trader's balance by summing current balance with the change. 
      Note that balance can be negative.*/
    public double adjustBalance(double change){
        this.balance += change;
        return this.balance;
    }

    /*Returns a string representation of the trader. 
      Format: ID: $BALANCE {PROD1: AMOUNT1, PROD2: AMOUNT2, ...., PRODN: AMOUNTN}*/
    public String toString(){
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append(this.traderID + ": $");
        sbuffer.append(String.format("%.2f", this.balance) + " {");

        List<String> productsList = getProductsInInventory();
        if (productsList.isEmpty()){
            sbuffer.append("}");
            return sbuffer.toString();
        }else{
            for (int i = 0; i < productsList.size(); i++){
                sbuffer.append(productsList.get(i) + ": ");
                sbuffer.append(String.format("%.2f", (inventory.get(productsList.get(i)))));
            
                if (i != (productsList.size() - 1)){
                    sbuffer.append(", ");
                }else{
                    sbuffer.append("}");
                }
            }
            return sbuffer.toString();
        }
    }

    /*Writes the list of traders to file in ASCII encoding. For every trader in the 
      provided list, write the String representation on a new line in the given file. 
      If either argument is invalid, do nothing.*/
    public static void writeTraders(List<Trader> traders, String path){
        if (traders == null || path == null){
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
            for (int i = 0; i < traders.size(); i ++){
                wr.println((traders.get(i)).toString());
            }
            wr.close();
        }catch (Exception e1){
            return;
        }
    }

    /*Writes the list of traders to file in binary encoding. 
      For every trader in the provided list, write the String representation, 
      followed by the Unit Separator byte. If either argument is invalid, do nothing.*/
    public static void writeTradersBinary(List<Trader> traders, String path){
        if (traders == null || path == null){
            return;
        }

        try{
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream binaryOutput = new DataOutputStream(f);
            for (int i = 0; i < traders.size(); i ++){
                String traderString = (traders.get(i)).toString();
                binaryOutput.writeUTF(traderString);
                
                StringBuffer sbuffer = new StringBuffer();
                sbuffer.append((char) Integer.parseInt("31"));
                binaryOutput.writeUTF(sbuffer.toString());
            }
            binaryOutput.close();
            }catch(IOException e){
                return;
            }
    }

    public static void main(String args[]) {
        Trader trader1 = new Trader("trader1", 100.00);
        Trader trader2 = new Trader("trader2", 100.00);
        trader1.importProduct("ABC", 100);
        trader1.importProduct("OIO", 50);
        trader1.importProduct("POP", 20);
        System.out.println(trader1.getProductsInInventory());
        System.out.println(trader1.toString());
        trader1.exportProduct("ABC", 50);
        trader1.exportProduct("OIO", 50);
        System.out.println(trader1.toString());
        trader2.importProduct("BCD", 200);
    }
}