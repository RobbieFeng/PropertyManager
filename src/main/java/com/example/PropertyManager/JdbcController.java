package com.example.PropertyManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;

import jakarta.annotation.Resource;
@Controller
public class JdbcController {
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	public boolean checkAccount(String username,@Nullable String password) throws NoSuchAlgorithmException {
        // Check if account exists in database
		String query = "SELECT * FROM accounts WHERE username = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, new Object[]{username});
		return results.size() > 0;
    }
	
	public int registerAccount(String username, String password, String email) throws NoSuchAlgorithmException {
		// hash password
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
		// Add account to database
		String querry = "INSERT INTO accounts (username, password, email, salt) VALUES ('" + username + "', '"
				+ Base64.getEncoder().encodeToString(hashedPassword) + "', '" + email +"', '" + Base64.getEncoder().encodeToString(salt) + "')";
		try {
            jdbcTemplate.execute(querry);
            //get userId
            String query = "SELECT userId FROM accounts WHERE username = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, new Object[]{username});
            
			return (int) results.get(0).get("userId");}
        catch(Exception e) {
			return -1;}
	}

	public boolean login(String username, String password) throws NoSuchAlgorithmException, SQLException {
		String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
		//get salt
		query = "SELECT salt FROM accounts WHERE username = ?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(query, new Object[] { username } );
		String storedSalt = (String) results.get(0).get("salt");
        byte[] salt = Base64.getDecoder().decode(storedSalt);
		
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);		
		byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
		// Check if account exists in database
		query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
		List<Map<String, Object>> results2 = jdbcTemplate.queryForList(query, new Object[] { username, Base64.getEncoder().encodeToString(hashedPassword) });
		return results2.size() > 0;
	}
	
	public List<Property> getProperties() {
		String query = "SELECT * FROM properties";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
		List<Property> properties = new ArrayList<Property>();
		for (Map<String, Object> result : results) {
			Property property = new Property();
			property.propertyId = result.get("propertyId").toString();
			property.address = (String) result.get("address");
			property.price = (int) result.get("price");
			property.bedrooms = (int) result.get("bedrooms");
			property.bathrooms = (int) result.get("bathrooms");
			properties.add(property);
		}
		return properties;
	}
	
	public String addProperty(Property property) {
		String query = "INSERT INTO properties (address, price, bedrooms, bathrooms) VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(query, property.address, property.price, property.bedrooms, property.bathrooms);
		query = "SELECT LAST_INSERT_ID()";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
		return results.get(0).get("LAST_INSERT_ID()").toString();
	}
	
	public void deleteProperty(String propertyId) {
		int id = Integer.parseInt(propertyId);
		String query = "DELETE FROM properties WHERE propertyId = ?";
		jdbcTemplate.update(query, id);
	}
	
	public void updateProperty(Property property, String propertyId) {
		String query = "UPDATE properties SET address = ?, price = ?, bedrooms = ?, bathrooms = ? WHERE propertyId = ?";
		jdbcTemplate.update(query, property.address, property.price, property.bedrooms, property.bathrooms,
				propertyId);
	}
	
    public List<Property> searchProperties(String address, Integer minPrice, Integer maxPrice, Integer minbedrooms, Integer maxbedrooms, Integer minbathrooms, Integer maxbathrooms) {
        StringBuilder sql = new StringBuilder("SELECT * FROM properties WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (address != null && !address.isEmpty()) {
            sql.append(" AND address LIKE ?");
            params.add("%" + address + "%");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (minbedrooms != null) {
            sql.append(" AND bedrooms >= ?");
            params.add(minbedrooms);
        }
        if (maxbedrooms != null) {
            sql.append(" AND bedrooms <= ?");
            params.add(maxbedrooms);
        }
        if (minbathrooms != null) {
            sql.append(" AND bathrooms >= ?");
            params.add(minbathrooms);
        }
        if (maxbathrooms != null) {
            sql.append(" AND bathrooms <= ?");
            params.add(maxbathrooms);
        }

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        List<Property> properties = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Property property = new Property();
            property.propertyId = result.get("propertyId").toString();
            property.address = (String) result.get("address");
            property.price = (int) result.get("price");
            property.bedrooms = (int) result.get("bedrooms");
            property.bathrooms = (int) result.get("bathrooms");
            properties.add(property);
        }
        return properties;
    }
	
	public List<Property> sortProperties(String sortBy, String order) {
		String query = "SELECT * FROM properties ORDER BY ? ?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(query, sortBy, order);
		List<Property> properties = new ArrayList<>();
		for (Map<String, Object> result : results) {
			Property property = new Property();
			property.propertyId = result.get("propertyId").toString();
			property.address = (String) result.get("address");
			property.price = (int) result.get("price");
			property.bedrooms = (int) result.get("bedrooms");
			property.bathrooms = (int) result.get("bathrooms");
			properties.add(property);
		}
		return properties;
	}
}
