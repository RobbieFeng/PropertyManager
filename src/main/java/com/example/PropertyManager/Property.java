package com.example.PropertyManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Property {
	public String propertyId;
	public String address;
	public int price;
	public int bedrooms;
	public int bathrooms;
	
	public JSONObject toJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("propertyId", propertyId);
		obj.put("address", address);
		obj.put("price", price);
		obj.put("bedrooms", bedrooms);
		obj.put("bathrooms", bathrooms);
		return obj;
	}
}
