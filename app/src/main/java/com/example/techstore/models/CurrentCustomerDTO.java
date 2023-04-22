package com.example.techstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CurrentCustomerDTO implements Parcelable {
    public int id;
    public String name;

    public String username;
    public String phone_number;

    public String email;
    public CurrentCustomerDTO() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public CurrentCustomerDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        username = in.readString();
        phone_number = in.readString();
        email = in.readString();
    }

    public static final Creator<CurrentCustomerDTO> CREATOR = new Creator<CurrentCustomerDTO>() {
        @Override
        public CurrentCustomerDTO createFromParcel(Parcel in) {
            return new CurrentCustomerDTO(in);
        }

        @Override
        public CurrentCustomerDTO[] newArray(int size) {
            return new CurrentCustomerDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeString(phone_number);
        parcel.writeString(email);
    }
}
