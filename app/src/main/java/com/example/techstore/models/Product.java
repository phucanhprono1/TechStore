package com.example.techstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Product implements Parcelable {
    private int productId;
    private String productName;
    private float price;
    private String color;
    private String description;
    private String image;
    private String size;
    private String manufacturer;
    private int numberSell;

    public Product() {
    }

    public Product(int productId, String productName, float price, String color, String description, String image, String size, String manufacturer, int numberSell, Category category) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.color = color;
        this.description = description;
        this.image = image;
        this.size = size;
        this.manufacturer = manufacturer;

        this.numberSell = numberSell;
        this.category = category;
    }


    protected Product(Parcel in) {
        productId = in.readInt();
        productName = in.readString();
        price = in.readFloat();
        color = in.readString();
        description = in.readString();
        image = in.readString();
        size = in.readString();
        manufacturer = in.readString();
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(productId);
        parcel.writeString(productName);
        parcel.writeFloat(price);
        parcel.writeString(color);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(size);
        parcel.writeString(manufacturer);
        parcel.writeInt(numberSell);
        parcel.writeParcelable(category, i);
    }
}
