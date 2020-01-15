package com.android.mvpauth.data.storage.realm;

import com.android.mvpauth.data.storage.dto.UserAddressDTO;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AddressRealm extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String street;
    private String house;
    private String apartment;
    private int floor;
    private String comment;
    private boolean favorite;

    public AddressRealm() {
    }

    public AddressRealm(UserAddressDTO mAddressDTO) {
        this.id = mAddressDTO.getId();
        this.name = mAddressDTO.getName();
        this.street = mAddressDTO.getStreet();
        this.house = mAddressDTO.getHouse();
        this.apartment = mAddressDTO.getApartment();
        this.floor = mAddressDTO.getFloor();
        this.comment = mAddressDTO.getComment();
        this.favorite = mAddressDTO.isFavorite();
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

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getApartment() {
        return apartment;
    }

    public int getFloor() {
        return floor;
    }

    public String getComment() {
        return comment;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
