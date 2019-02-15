package com.tspolice.htplive.models;

import com.google.gson.annotations.SerializedName;

public class DestinationAddrModel {

    @SerializedName("destination_addresses")
    private String[] destinationAddresses;

    @SerializedName("origin_addresses")
    private String[] originAddresses;

    private Rows[] rows;

    private String status;

    public DestinationAddrModel() {
        // default constructor
    }

    public String[] getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(String[] destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public String[] getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(String[] originAddresses) {
        this.originAddresses = originAddresses;
    }

    public Rows[] getRows() {
        return rows;
    }

    public void setRows(Rows[] rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [destination_addresses = " + destinationAddresses + ", origin_addresses = " + originAddresses + ", rows = " + rows + ", status = " + status + "]";
    }
}
