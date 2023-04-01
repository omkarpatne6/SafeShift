package com.example.safeshift;

import com.google.firebase.firestore.PropertyName;

public class PreviousEmployeeOrders {
    @PropertyName("id")
    String order_name;
    String pickup_address, destination_address, pickup_date, status;

    // Use @PropertyName annotation to specify Firestore field name
    @PropertyName("id")
    public String getOrder_name() {
        return order_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public void setDestination_address(String destination_address) {
        this.destination_address = destination_address;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public String getDestination_address() {
        return destination_address;
    }

    public String getPickup_date() {
        return pickup_date;
    }
}
