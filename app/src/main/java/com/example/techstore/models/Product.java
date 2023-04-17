package com.example.techstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Product implements Parcelable {
    private Integer productId;
    private String productName;
    private Double price;
    private String color;
    private String description;
    private String image;
    private String size;
    private String manufacturer;
    private int quantity;
    private int numberSell;

    public Product() {
    }

    public Product(Integer productId, String productName, Double price, String color, String description, String image, String size, String manufacturer, int quantity, int numberSell, Category category) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.color = color;
        this.description = description;
        this.image = image;
        this.size = size;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.numberSell = numberSell;
        this.category = category;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getNumberSell() {
        return numberSell;
    }

    public void setNumberSell(int numberSell) {
        this.numberSell = numberSell;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    private Category category;

    protected Product(Parcel in) {
        if (in.readByte() == 0) {
            productId = null;
        } else {
            productId = in.readInt();
        }
        productName = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        color = in.readString();
        description = in.readString();
        image = in.readString();
        size = in.readString();
        manufacturer = in.readString();
        quantity = in.readInt();
        numberSell = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (productId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(productId);
        }
        parcel.writeString(productName);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
        parcel.writeString(color);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(size);
        parcel.writeString(manufacturer);
        parcel.writeInt(quantity);
        parcel.writeInt(numberSell);
        parcel.writeParcelable(category, i);
    }
}
