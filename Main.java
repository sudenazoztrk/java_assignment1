/**
 * The Main class serves as the entry point for the Gym Meal Machine (GMM) application.
 * This class reads product and purchase details from files, initializes the machine,
 * processes product fillings and purchases, and writes the machine's state to an output file.
 */
public class Main {
    /**
     * The main method of the application. It reads product and purchase information from input files,
     * initializes a machine with slots, fills the machine with products, processes purchase
     * requests, and finally writes the updated state of the machine to an output file.
     *
     * The method expects three command-line arguments:
     * <ol>
     * <li>args[0]: The path to the product input file, containing details of products to be loaded into the machine.</li>
     * <li>args[1]: The path to the purchase input file, containing details of purchase attempts by users.</li>
     * <li>args[2]: The path to the output file where the machine state and transaction logs will be written.</li>
     * </ol>
     *
     * @param args An array of {@link String} containing command-line arguments. It should contain paths to
     *             the product input file, purchase input file, and output file, in that order.
     */
    public static void main(String[] args) {
        String[] productContent = FileInput.readFile(args[0], false, false);
        String[] purchaseContent = FileInput.readFile(args[1], false, false);

        FileOutput.writeToFile(args[2], "", false, false);

        Slot[][] machine = new Slot[6][4];
        GMMOutput.createSlots(machine);
        GMMOutput.fill(args, productContent, machine);
        GMMOutput.writeMachine(args, machine);
        GMMOutput.purchase(args, machine, purchaseContent);
        GMMOutput.writeMachine(args, machine);
    }
}


