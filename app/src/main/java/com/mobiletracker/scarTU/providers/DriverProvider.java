package com.mobiletracker.scarTU.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobiletracker.scarTU.models.Client;
import com.mobiletracker.scarTU.models.Driver;

import java.util.HashMap;
import java.util.Map;

public class DriverProvider {

    DatabaseReference mDatabase;

    public DriverProvider()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver driver)
    {
        return mDatabase.child(driver.getId()).setValue(driver);

    }

    public Task<Void> update(Driver driver)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("name", driver.getName());
        map.put("image", driver.getImage());

        return mDatabase.child(driver.getId()).updateChildren(map);

    }

    public DatabaseReference getDriver(String idDriver) {
        return mDatabase.child(idDriver);
    }

    public DatabaseReference getConductor(String idDriver) {
        return mDatabase.child(idDriver);
    }
}
