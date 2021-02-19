package vip.testops.coffee.test.entities;

public class OrderCreateRequestEntity {
    private String address;
    private OrderItemRequestEntity[] orderItems;

    public OrderCreateRequestEntity() {
    }

    public OrderCreateRequestEntity(String address, OrderItemRequestEntity[] orderItems) {
        this.address = address;
        this.orderItems = orderItems;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OrderItemRequestEntity[] getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(OrderItemRequestEntity[] orderItems) {
        this.orderItems = orderItems;
    }

}
