package com.example.tripmodule;

import java.io.Serializable;

/**
 * Created by Pravitha on 16-04-2015.
 */
public class MultipleDestinationArrayList implements Serializable {
    String vnum,sourceOrLastDest,product,qty,destination,distance,route,rent,paymentType,percentageOrPerKM,tripOrder,subVoucher;
    public String getVnum() {
        return vnum;
    }

    public void setVnum(String vnum) {
        this.vnum = vnum;
    }

    public String getpercentageOrPerKM() {
        return percentageOrPerKM;
    }

    public void setpercentageOrPerKM(String percentage) {
        this.percentageOrPerKM = percentage;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String payment) {
        this.paymentType = payment;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDestinations() {
        return destination;
    }

    public void setDestinations(String destinations) {
        this.destination = destinations;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSource() {
        return sourceOrLastDest;
    }

    public void setSource(String source) {
        this.sourceOrLastDest = source;
    }


    public String getTripOrder() {
        return tripOrder;
    }

    public void setTripOrder(String tripOrder) {
        this.tripOrder = tripOrder;
    }


    public String getSubVoucher() {
        return subVoucher;
    }

    public void setSubVoucher(String subVoucher) {
        this.subVoucher = subVoucher;
    }


    public MultipleDestinationArrayList(String vnum, String product,String qty,String destination,String distance, String route,
                                        String rent,String paymentType,String percentageOrPerKM, String sourceOrLastDest,String tripOrder,String subVoucher)
    {
        this.vnum = vnum;
        this.product = product;
        this.qty = qty;
        this.destination = destination;
        this.distance = distance;
        this.route = route;
        this.rent = rent;
        this.paymentType = paymentType;
        this.percentageOrPerKM = percentageOrPerKM;
        this.sourceOrLastDest = sourceOrLastDest;
        this.tripOrder=tripOrder;
        this.subVoucher=subVoucher;
    }

    @Override
    public String toString() {
        return vnum+","+product+","+qty+","+destination+","+distance+","+route+","+rent+","+paymentType+","+percentageOrPerKM+","+sourceOrLastDest+","+tripOrder+","+subVoucher;
    }

}
