package program;

import entity.Donuts;
import entity.DonutsDAO;

public class MenuSeed {
    public static void main(String[] args)
    {
        DonutsDAO dao = new DonutsDAO();

        add(dao, 1, "Glazed Donut", "Classic glazed donut", 1.49, "Donuts", null);
        add(dao, 2, "Chocolate Sprinkles", "Chocolate donut with sprinkles", 1.79, "Donuts", null);
        add(dao, 3, "Boston Cream", "Chocolate-topped donut filled with custard", 1.99, "Donuts", null);
        add(dao, 4, "Cookie Butter Coffee", "Seasonal Cookie Butter coffee", 2.75, "Coffee", "Fall");
        add(dao, 5, "Honey Bear Latte", "Espresso with steamed milk", 3.00, "Coffee", null);
        add(dao, 6, "English Breakfast Tea", "Black breakfast tea", 2.00, "Tea", null);
        add(dao, 7, "Cinnamon Bagel", "Toasted cinnamon bagel", 2.25, "Bagels", null);
        add(dao, 8, "Apple Cider Donut", "Apple Cider donut", 2.25, "Donuts", "Fall");
        add(dao, 9, "Peppermint Donut", "Peppermint donut", 2.25, "Donuts", "Winter");
        add(dao, 10, "Strawberry Spring Donut", "Strawberry spring donut", 2.25, "Donuts", "Spring");
        add(dao, 11, "Apple Donut", "Apple donut", 2.25, "Donuts", "Summer");
        System.out.println("Menu items added.");
    }

    private static void add(DonutsDAO dao, int id, String name, String description,
                            double price, String category, String season) {
        Donuts item = new Donuts();
        item.setDonutId(id);
        item.setDonutName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setAvailable(true);
        item.setCategory(category);
        item.setSeason(season);
        dao.insert(item);
    }
}