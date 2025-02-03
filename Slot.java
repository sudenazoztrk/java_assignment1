/**
 * Represents a slot in a vending machine. Each slot can hold a product and tracks the capacity
 * (i.e., the number of units of the product it contains). The capacity is initially set to 0,
 * indicating that the slot is empty. The product within the slot can be set later as products
 * are loaded into the machine.
 */
public class Slot{
    // The product placed in this slot.
    Product product;
    // The number of product in this slot.
    int capacity;

    /**
     * Constructs an empty Slot with no product and a capacity of 0.
     * The product can be assigned later when the slot is filled.
     */
    public Slot() {
        this.product = product; // Initializes the slot with no product.
        this.capacity = 0; // Initializes the slot as empty.
    }
}
