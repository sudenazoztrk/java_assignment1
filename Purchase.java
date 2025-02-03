import java.util.List;

public class Purchase {
    private String Cash;
    private List<Integer> Money;
    private String Choice;
    private int Value;
    public Purchase(String cash, List<Integer> money, String choice, int value) {
        Cash = cash;
        Money = money;
        Choice = choice;
        Value = value;
    }
    public void setMoney(List<Integer> money) {
        Money = money;
    }

    public String getCash() {
        return Cash;
    }

    public void setCash(String cash) {
        Cash = cash;
    }

    public List<Integer> getMoney() {
        return Money;
    }

    public void setMoney(int totalMoney) {
        Money = Money;
    }

    public String getChoice() {
        return Choice;
    }

    public void setChoice(String choice) {
        Choice = choice;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }
}
