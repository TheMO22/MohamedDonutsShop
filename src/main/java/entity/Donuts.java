package entity;

public class Donuts {
    private int DonutId;
    private String DonutName;
    private String description;
    private double price;
    private boolean available;
    private String category;
    private String season;


    public int getDonutId() {
        return DonutId;
    }

    public void setDonutId(int donutId) {
        DonutId = donutId;
    }

    public String getDonutName() {
        return DonutName;
    }

    public void setDonutName(String donutName) {
        DonutName = donutName;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
