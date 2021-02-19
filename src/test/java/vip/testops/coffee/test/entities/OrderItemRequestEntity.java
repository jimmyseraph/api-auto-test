package vip.testops.coffee.test.entities;

public class OrderItemRequestEntity {
    private long coffeeId;
    private int amount;

    public OrderItemRequestEntity(long coffeeId, int amount) {
        this.coffeeId = coffeeId;
        this.amount = amount;
    }

    public OrderItemRequestEntity() {
    }

    public long getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(long coffeeId) {
        this.coffeeId = coffeeId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
