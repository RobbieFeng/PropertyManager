package com.example.PropertyManager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * PropertyUtil is a REST controller that provides endpoints to interact with property data.
 */
@RestController
@RequestMapping("/properties")
public class PropertyUtil {

    @Resource
    private JdbcController jdbcController;

    private String username = "";

    protected void setUser(String username) {
        this.username = username;
        System.out.println("User: " + username + " logged in");
    }

    /**
     * Retrieves the list of properties from the database. 
     * Http method: GET /properties
     * 
     * @return A ResponseEntity containing the list of properties in JSON format.
     * @throws JSONException If an error occurs while creating the JSON response.
     */
    @GetMapping("")
    public ResponseEntity<String> getProperties() throws JSONException {
        List<Property> properties = jdbcController.getProperties();
        JSONObject response = new JSONObject();
        response.put("status", "success");
        List<JSONObject> data = new ArrayList<>();
        for (Property property : properties) {
            data.add(property.toJSONObject());
        }
        response.put("data", data);
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }
    
    /**
     * Adds a property to the database.
     * Http method: POST /properties with a JSON in a form of:
     * {
     *    "address": "123 Main St",
     *    "price": 100000,
     *    "bedrooms": 3,
     *    "bathrooms": 2
     * }
     */
    @PostMapping("")
	public ResponseEntity<String> addProperty(@RequestBody String str) throws JSONException {
		JSONObject jsonObject = new JSONObject(str);
		Property property = new Property();
		try {
			property.address = jsonObject.getString("address");
			property.price = jsonObject.getInt("price");
			property.bedrooms = jsonObject.getInt("bedrooms");
			property.bathrooms = jsonObject.getInt("bathrooms");
		} catch (JSONException e) {
			JSONObject response = new JSONObject();
			response.put("status", "failure");
			response.put("message", "Invalid Request Body");
			return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
		}
		String propertyId = jdbcController.addProperty(property);
		JSONObject response = new JSONObject();
		response.put("status", "success");
		response.put("message", "Property added");
		JSONObject data = new JSONObject();
		data.put("propertyId", propertyId);
		response.put("data", data);
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
    
    /* update property with propertyId 
     * Http method: PUT /properties/{propertyId} with a JSON in a form of:
     * {
     *   "address": "123 Main St",
     *   "price": 100000,
     *   "bedrooms": 3,
     *   "bathrooms": 2
     *  }
     */
    @PutMapping("/{propertyId}")
	public ResponseEntity<String> updateProperty(@PathVariable String propertyId, @RequestBody String str)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(str);
		Property property = new Property();
		try {
			property.address = jsonObject.getString("address");
			property.price = jsonObject.getInt("price");
			property.bedrooms = jsonObject.getInt("bedrooms");
			property.bathrooms = jsonObject.getInt("bathrooms");
		} catch (JSONException e) {
			JSONObject response = new JSONObject();
			response.put("status", "failure");
			response.put("message", "Invalid Request Body");
			return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
		}
		jdbcController.updateProperty(property, propertyId);
		JSONObject response = new JSONObject();
		response.put("status", "success");
		response.put("message", "Property updated");
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
    
    /* delete property with propertyId
     * Http method: DELETE /properties/{propertyId}
     */
    @DeleteMapping("/{propertyId}")
	public ResponseEntity<String> deleteProperty(@PathVariable String propertyId) throws JSONException {
		jdbcController.deleteProperty(propertyId);
		JSONObject response = new JSONObject();
		response.put("status", "success");
		response.put("message", "Property deleted");
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
    
	/*
	 * search properties with address, minPrice, maxPrice, minbedrooms, maxbedrooms,
	 * minbathrooms, maxbathrooms Http method: GET /properties/search?address=123St&minPrice=100000&maxPrice=200000&minbedrooms=3&maxbedrooms=4&minbathrooms=2
	 * &maxbathrooms=3
	 */
    @GetMapping("/search")
    public ResponseEntity<String> searchProperties(
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "minBedrooms", required = false) Integer minBedrooms,
            @RequestParam(value = "maxBedrooms", required = false) Integer maxBedrooms,
            @RequestParam(value = "minBathrooms", required = false) Integer minBathrooms,
            @RequestParam(value = "maxBathrooms", required = false) Integer maxBathrooms) throws JSONException {
		List<Property> properties;

		properties = jdbcController.searchProperties(address, minPrice, maxPrice, minBedrooms,
				maxBedrooms, minBathrooms, maxBathrooms);
		JSONObject response = new JSONObject();
		response.put("status", "success");
		List<JSONObject> data = new ArrayList<>();
		for (Property property : properties) {
			data.add(property.toJSONObject());
		}
		response.put("data", data);
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
    
    @GetMapping("/sort")
    public ResponseEntity<String> sortProperties(
            @RequestParam(value = "sortBy", required = true) String sortBy, 
            @RequestParam(value = "order", required = false) String order) throws JSONException {
    	List<Property> properties;
		if (order.equals("desc") || order.equals("DESC")) {
			properties = jdbcController.sortProperties(sortBy, "DESC");
		} else {
			properties = jdbcController.sortProperties(sortBy, "ASC");
		}
    	        JSONObject response = new JSONObject();
    	        response.put("status", "success");
    	        List<JSONObject> data = new ArrayList<>();
				for (Property property : properties) {
					data.add(property.toJSONObject());
				}
				response.put("data", data);
				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		
    }
}
