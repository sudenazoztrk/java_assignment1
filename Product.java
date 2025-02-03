/**
 * Represents a product with nutritional information and pricing. This class is used to model products for a vending machine
 * that dispenses gym-related food items such as protein bars, shakes, etc. It includes details such as name, price, protein,
 * carbohydrate, fat content, and calculated calorie value based on nutritional components.
 */
public class Product {
    private String name;
    private int price;
    private float protein;
    private float carbonhydrate;
    private float fat;
    private float calorie;
    private int amount;


    /**
     * Constructs a new Product with specified nutritional information and initial amount.
     * The calorie value is calculated based on the formula: 4 * protein + 4 * carbohydrate + 9 * fat.
     *
     * @param name          The name of the product.
     * @param price         The price of the product.
     * @param protein       The protein content of the product in grams.
     * @param carbonhydrate The carbohydrate content of the product in grams.
     * @param fat           The fat content of the product in grams.
     * @param amount        The initial amount of the product available.
     */
    public Product(String name, int price, float protein, float carbonhydrate, float fat,int amount){
        this.name = name;
        this.price = price;
        this.protein = protein;
        this.carbonhydrate = carbonhydrate;
        this.fat = fat;
        this.calorie = 4 * protein + 4 * carbonhydrate + 9 * fat;
        this.amount = 1;
}

    // Getter and setter methods for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarbonhydrate() {
        return carbonhydrate;
    }

    public void setCarbonhydrate(float carbonhydrate) {
        this.carbonhydrate = carbonhydrate;
    }

    public float getFat() {
        return fat;
    }

    public float getCalorie() { return calorie; }

    public void setCalorie(float calorie) { this.calorie = calorie; }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Updates the amount of the product by adding the specified number.
     * This can be used to increment or decrement the stock of the product.
     *
     * @param num The number of units to add to the current amount.
     */
    public void updateAmount(int num) {
        this.amount += num;
    }

    /**
     * Calculates and returns the rounded calorie value of the product based on its nutritional content.
     *
     * @return The rounded calorie value of the product.
     */
    public int getNewCalorie() {
        return Math.round(this.calorie);
    }

}
