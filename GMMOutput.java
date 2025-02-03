import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is generally where the gym meal machine works. creates slots for the machine,
 * fills these slots with products, makes product purchases, prints the machine status.
 */
public class GMMOutput {

    /**
     * Initializes the machine with empty slots.
     *
     * @param machine A 2D array of {@link Slot} objects representing the structure of the machine.
     */
    public static void createSlots(Slot[][] machine) {
        for (int i = 0; i < machine.length; i++) {
            for (int j = 0; j < machine[i].length; j++) {
                machine[i][j] = new Slot();
            }
        }
    }

    /**
     * Fills the machine with products based on the input product list. this function places the products
     * in the slots in the appropriate order and prints them to the file if there is an error.
     *
     * @param args Command line arguments where args[2] is expected to be the path to the log file.
     * @param productContent An array of strings, each representing a product and its attributes.
     * @param machine A 2D array of {@link Slot} objects representing the machine slots.
     * @return An integer indicating the status of the operation (0 for success, -1 if the machine is full).
     */
    public static int fill(String[] args, String[] productContent, Slot[][] machine) {
        ArrayList<Product> productList = new ArrayList<>();
        int filledSlots = 0; //number of full slots


        for (String line : productContent) {
            String[] parts = line.split("\t");
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            String[] foodValues = parts[2].split(" ");
            float protein = Float.parseFloat(foodValues[0]);
            float carbohydrate = Float.parseFloat(foodValues[1]);
            float fat = Float.parseFloat(foodValues[2]);

            Product product = productTest(name, price, protein, carbohydrate, fat, productList); //Testing to see if the product has been formed before


            boolean placed = false;
            loop:
            for (int i = 0; i < machine.length && !placed; i++) {
                for (int j = 0; j < machine[i].length && !placed; j++) {
                    if (machine[i][j].product == null) {
                        machine[i][j].product = product;
                        machine[i][j].capacity = 1;
                        product.updateAmount(1);
                        placed = true;
                        break loop;

                    } else if (machine[i][j].product.getName().equals(product.getName()) && machine[i][j].capacity < 10) {
                        machine[i][j].capacity += 1;
                        product.updateAmount(1);
                        if (machine[i][j].capacity == 10) {
                            filledSlots += 1;
                        }
                        placed = true;
                        break loop;
                    }
                }
            }
            if (!placed) {
                FileOutput.writeToFile(args[2], "INFO: There is no available place to put " + product.getName(), true, true);
            }
            if (!placed && filledSlots == 24) {
                FileOutput.writeToFile(args[2], "INFO: The machine is full!", true, true);
                return -1;
            }


        }
        return 0;
    }

    /**
     * Creates or retrieves a product based on the provided parameters. If the product already exists in the productList,
     * it returns that product; otherwise, it creates a new product, adds it to the productList, and returns it.
     *
     * @param name The name of the product.
     * @param price The price of the product.
     * @param protein The protein content of the product.
     * @param carbohydrate The carbohydrate content of the product.
     * @param fat The fat content of the product.
     * @param productList An ArrayList of {@link Product} objects that have been added to the machine.
     * @return The {@link Product} object created or found in the productList.
     */
    public static Product productTest(String name, int price, float protein, float carbohydrate, float fat, ArrayList<Product> productList) {
        for (Product prod : productList) {
            if (prod.getName().equals(name)) {
                return prod;
            }
        }
        Product newProduct = new Product(name, price, protein, carbohydrate, fat, 1);
        productList.add(newProduct);
        return newProduct;
    }

    /**
     * Writes the current state of the machine to a file. This includes the products in each slot and their quantities.
     *
     * @param args Command line arguments where args[2] is expected to be the path to the output file.
     * @param machine A 2D array of {@link Slot} objects representing the vending machine slots.
     */
    public static void writeMachine(String[] args, Slot[][] machine) {
        FileOutput.writeToFile(args[2], "-----Gym Meal Machine-----", true, true);

        for (int i = 0; i < machine.length; i++) {
            for (int j = 0; j < machine[0].length; j++) {
                if (machine[i][0].product == null) {
                    FileOutput.writeToFile(args[2], "___(0, 0)______(0, 0)______(0, 0)______(0, 0)___\n", true, false);
                    break;
                }else if ((machine[i][j].product == null || machine[i][j].capacity == 0) && j != 3) {
                    FileOutput.writeToFile(args[2], "___(0, 0)___", true, false);
                } else if ((machine[i][j].product == null || machine[i][j].capacity == 0) && j == 3) {
                    FileOutput.writeToFile(args[2], "___(0, 0)___\n", true, false);
                } else if (machine[i][j].product != null && j != 3 ) {
                    FileOutput.writeToFile(args[2], machine[i][j].product.getName() + "(" + machine[i][j].product.getNewCalorie() + ", " + machine[i][j].capacity + ")___", true, false);
                } else if (machine[i][j].product != null && j == 3) {
                    FileOutput.writeToFile(args[2], machine[i][j].product.getName() + "(" + machine[i][j].product.getNewCalorie() + ", " + machine[i][j].capacity + ")___\n", true, false);
                }
            }
        }
        FileOutput.writeToFile(args[2], "----------", true, true);


    }

    /**
     * Handles the purchase process for the vending machine. It processes user input to perform transactions based on the product
     * choice (by protein, carb, fat, calorie, or slot number) and handles money transactions including validation of inserted money
     * and returning change.
     *
     * @param args Command line arguments where args[2] is expected to be the path to the log file.
     * @param machine A 2D array of {@link Slot} objects representing the vending machine slots.
     * @param purchaseContent An array of strings, each representing a purchase attempt and its details.
     * @return An integer indicating the status of the operation (0 for success, -1 for any error during purchase).
     */
    public static int purchase(String[] args, Slot[][] machine, String[] purchaseContent) {
        boolean errorMessage = true;
        List<Integer> validValues = Arrays.asList(1, 5, 10, 20, 50, 100, 200);

        for (String line : purchaseContent) {
            String[] parts = line.split("\t");
            String type = parts[0];
            String[] money = parts[1].split(" ");
            List<Integer> validValue = new ArrayList<>();
            List<Integer> invalidValue = new ArrayList<>();

            int validTotalMoney = 0;
            for (String part : money) {
                int part2 = Integer.parseInt(part);
                if (validValues.contains(part2)) { //If the given money value is applied, it adds it to the list and
                    // calculates the total of valid moneys.
                    validValue.add(part2);
                    validTotalMoney += part2;
                }
                else if(!validValues.contains(part2)){ //It also collects invalid moneys in a list.
                    invalidValue.add(part2);
                }
            }

            String choice = parts[2];
            int value = Integer.parseInt(parts[3]);

            Purchase purchase = new Purchase(type, validValue, choice, value);  //Creates an object for all purchases and assigns the specified properties

            FileOutput.writeToFile(args[2], "INPUT: " + line, true, true);


            if (purchase.getChoice().equals("PROTEIN")) {
                boolean output = false;
                loop:
                for (int i = 0; i < machine.length; i++) {
                    for (int j = 0; j < machine[i].length; j++) {
                        float upperBound = purchase.getValue() + 5;
                        float lowerBound = purchase.getValue() - 5;
                        if(invalidValue.size() != 0){
                            for (int k : invalidValue){
                                FileOutput.writeToFile(args[2], "INFO: The machine does not accept " + k + " TL." , true, true);
                                errorMessage = false;
                            }
                            invalidValue.clear(); //Invalid money errors if there are any, it will empty the list after printing so that it does not print again every loop
                        }
                        if (machine[i][j].product != null &&  machine[i][j].product.getProtein()<= upperBound && lowerBound <= machine[i][j].product.getProtein() && validTotalMoney >= machine[i][j].product.getPrice() && machine[i][j].capacity > 0) {
                            FileOutput.writeToFile(args[2], "PURCHASE: You have bought one " + machine[i][j].product.getName(), true, true);
                            int lastMoney = validTotalMoney - machine[i][j].product.getPrice();
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + lastMoney + " TL", true, true);
                            machine[i][j].capacity -= 1;
                            output = true;
                            break loop;
                        }
                        else if (machine[i][j].product != null && machine[i][j].product.getProtein()<= upperBound && lowerBound <= machine[i][j].product.getProtein() && machine[i][j].product.getPrice() > validTotalMoney) {
                            FileOutput.writeToFile(args[2], "INFO: Insufficient money, try again with more money.", true, true);
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                            errorMessage = false;
                            output = true;
                            break loop;
                        }
                    }
                }
                if (output == false) {
                    FileOutput.writeToFile(args[2], "INFO: Product not found, your money will be returned.", true, true);
                    FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                    errorMessage = false;
                }
            }
            else if (purchase.getChoice().equals("CARB")) {
                boolean output = false;
                loop:
                for (int i = 0; i < machine.length; i++) {
                    for (int j = 0; j < machine[i].length; j++) {
                        float upperBound = purchase.getValue() + 5;
                        float lowerBound = purchase.getValue() - 5;
                        if(invalidValue.size() != 0){
                            for (int k : invalidValue){
                                FileOutput.writeToFile(args[2], "INFO: The machine does not accept " + k + " TL." , true, true);
                                errorMessage = false;
                            }
                            invalidValue.clear(); // Invalid money errors if there are any, it will empty the list after printing so that it does not print again every loop
                        }
                        if (machine[i][j].product != null && machine[i][j].product.getCarbonhydrate()<= upperBound && lowerBound <= machine[i][j].product.getCarbonhydrate() && validTotalMoney >= machine[i][j].product.getPrice() && machine[i][j].capacity > 0) {
                            FileOutput.writeToFile(args[2], "PURCHASE: You have bought one " + machine[i][j].product.getName(), true, true);
                            int lastMoney = validTotalMoney - machine[i][j].product.getPrice();
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + lastMoney + " TL", true, true);
                            machine[i][j].capacity -= 1;
                            output = true;
                            break loop;
                        } else if (machine[i][j].product != null && machine[i][j].product.getCarbonhydrate()<= upperBound && lowerBound <= machine[i][j].product.getCarbonhydrate() && machine[i][j].product.getPrice() > validTotalMoney) {
                            FileOutput.writeToFile(args[2], "INFO: Insufficient money, try again with more money.", true, true);
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                            errorMessage = false;
                            output = true;
                            break loop;
                        }
                    }
                }
                if (output == false) {
                    FileOutput.writeToFile(args[2], "INFO: Product not found, your money will be returned.", true, true);
                    FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                    errorMessage = false;
                }
            }
            else if (purchase.getChoice().equals("FAT")) {
                boolean output = false;
                loop:
                for (int i = 0; i < machine.length; i++) {
                    for (int j = 0; j < machine[i].length; j++) {
                        float upperBound = purchase.getValue() + 5;
                        float lowerBound = purchase.getValue() - 5;
                        if(invalidValue.size() != 0){
                            for (int k : invalidValue){
                                FileOutput.writeToFile(args[2], "INFO: The machine does not accept " + k + " TL." , true, true);
                                errorMessage = false;
                            }
                            invalidValue.clear();
                        }
                        if (machine[i][j].product != null && machine[i][j].product.getFat() <= upperBound && lowerBound <= machine[i][j].product.getFat() && validTotalMoney >= machine[i][j].product.getPrice() && machine[i][j].capacity > 0) {
                            FileOutput.writeToFile(args[2], "PURCHASE: You have bought one " + machine[i][j].product.getName(), true, true);
                            int lastMoney = validTotalMoney - machine[i][j].product.getPrice();
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + lastMoney + " TL", true, true);
                            machine[i][j].capacity -= 1;
                            output = true;
                            break loop;
                        } else if (machine[i][j].product != null && machine[i][j].product.getFat() <= upperBound && lowerBound <= machine[i][j].product.getFat() && machine[i][j].product.getPrice() > validTotalMoney) {
                            FileOutput.writeToFile(args[2], "INFO: Insufficient money, try again with more money.", true, true);
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                            errorMessage = false;
                            output = true;
                            break loop;
                        }
                    }
                }
                if (output == false) {
                    FileOutput.writeToFile(args[2], "INFO: Product not found, your money will be returned.", true, true);
                    FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                    errorMessage = false;
                }
            }
            else if (purchase.getChoice().equals("CALORIE")) {
                boolean output = false;
                loop:
                for (int i = 0; i < machine.length; i++) {
                    for (int j = 0; j < machine[i].length; j++) {
                        float upperBound = purchase.getValue() + 5;
                        float lowerBound = purchase.getValue() - 5;
                        if(invalidValue.size() != 0){
                            for (int k : invalidValue){
                                FileOutput.writeToFile(args[2], "INFO: The machine does not accept " + k + " TL." , true, true);

                                errorMessage = false;
                            }
                            invalidValue.clear();
                        }
                        if (machine[i][j].product != null && machine[i][j].product.getCalorie()<= upperBound && lowerBound <= machine[i][j].product.getCalorie() && validTotalMoney >= machine[i][j].product.getPrice() && machine[i][j].capacity > 0) {
                            FileOutput.writeToFile(args[2], "PURCHASE: You have bought one " + machine[i][j].product.getName(), true, true);
                            int lastMoney = validTotalMoney - machine[i][j].product.getPrice();
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + lastMoney + " TL", true, true);
                            machine[i][j].capacity -= 1;
                            output = true;
                            break loop;
                        } else if (machine[i][j].product != null && machine[i][j].product.getCalorie()<= upperBound && lowerBound <= machine[i][j].product.getCalorie() && machine[i][j].product.getPrice() > validTotalMoney) {
                            FileOutput.writeToFile(args[2], "INFO: Insufficient money, try again with more money.", true, true);
                            FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                            errorMessage = false;
                            output = true;
                            break loop;
                        }
                    }
                }
                if (output == false){
                    FileOutput.writeToFile(args[2], "INFO: Product not found, your money will be returned.", true, true);
                    FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                    errorMessage = false;
                }
            }
            else if (purchase.getChoice().equals("NUMBER")) {
                if(invalidValue.size() != 0){
                    for (int k : invalidValue){
                        FileOutput.writeToFile(args[2], "INFO: The machine does not accept " + k + " TL." , true, true);
                        errorMessage = false;
                    }
                    invalidValue.clear();
                }
                if (purchase.getValue() > 23 | purchase.getValue() < 0){
                    FileOutput.writeToFile(args[2],"INFO: Number cannot be accepted. Please try again with another number." , true, true);
                    FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                    errorMessage = false;
                }
                else{
                    int row = purchase.getValue() / 4;
                    int col = purchase.getValue() % 4 ;
                    if (machine[row][col].product != null && machine[row][col].product.getPrice() <= validTotalMoney && machine[row][col].capacity > 0){
                        FileOutput.writeToFile(args[2], "PURCHASE: You have bought one " + machine[row][col].product.getName(), true, true);
                        int lastMoney = validTotalMoney - machine[row][col].product.getPrice();
                        FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + lastMoney + " TL", true, true);
                        machine[row][col].capacity -= 1;
                    } else if (machine[row][col].product == null | machine[row][col].capacity == 0 ) {
                        FileOutput.writeToFile(args[2], "INFO: This slot is empty, your money will be returned.", true, true);
                        FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                        errorMessage = false;
                    } else if (machine[row][col].product.getPrice() > validTotalMoney) {
                        FileOutput.writeToFile(args[2], "INFO: Insufficient money, try again with more money.", true, true);
                        FileOutput.writeToFile(args[2], "RETURN: Returning your change: " + validTotalMoney + " TL", true, true);
                        errorMessage = false;
                    }
                }
            }
        }
        if (errorMessage = false){
            return -1;
        }
        else{
            return 0;
        }
    }
}
