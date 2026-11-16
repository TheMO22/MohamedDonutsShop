package entity;

public class ItemOrder
{
    private int orderItemId;
    private int transactionId;
    private int donutId;
    private int quantity;
    private double priceOfItem;
    private String icing;
    private String filling;
    private boolean sprinkles;

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getDonutId() {
        return donutId;
    }

    public void setDonutId(int donutId) {
        this.donutId = donutId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceOfItem() {
        return priceOfItem;
    }

    public void setPriceOfItem(double priceOfItem) {
        this.priceOfItem = priceOfItem;
    }

    public String getIcing() {
        return icing;
    }

    public void setIcing(String icing) {
        this.icing = icing;
    }

    public String getFilling() {
        return filling;
    }

    public void setFilling(String filling) {
        this.filling = filling;
    }

    public boolean isSprinkles() {
        return sprinkles;
    }

    public void setSprinkles(boolean sprinkles) {
        this.sprinkles = sprinkles;
    }
}
