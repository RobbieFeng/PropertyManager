package com.example.PropertyManager;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/user")
public class AccountUtil {
	@Resource
	private JdbcController jdbcController;
	@Resource
	private PropertyUtil propertyUtil;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody String str) throws JSONException, NoSuchAlgorithmException {
    	JSONObject jsonObject = new JSONObject(str);
    	String username = jsonObject.getString("username");
    	String password = jsonObject.getString("password");
    	String email = jsonObject.getString("email");
    	if (jdbcController.checkAccount(username, password)) {
    		//response with error
        	JSONObject response = new JSONObject();
        	response.put("status", "failure");
            response.put("message", "Account already exists");
            response.put("data", "");
            return new ResponseEntity<>(response.toString(), new HttpHeaders() ,HttpStatus.BAD_REQUEST);
            
    	}
    	int id = jdbcController.registerAccount(username, password, email);
		if (id > 0) {
			// response with success
			JSONObject response = new JSONObject();
			response.put("status", "success");
			response.put("message", "Account created");
			JSONObject data = new JSONObject();
			data.put("userId", id);
			response.put("data", data);
			return new ResponseEntity<>(response.toString(), new HttpHeaders(), HttpStatus.OK);
			
		} else {
			// response with error
			JSONObject response = new JSONObject();
			response.put("status", "failure");
			response.put("message", "Account creation failed");
			response.put("data", "");
			return new ResponseEntity<>(response.toString(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody String str) throws JSONException, NoSuchAlgorithmException, SQLException {
    	JSONObject jsonObject = new JSONObject(str);
    	String username = jsonObject.getString("username");
    	String password = jsonObject.getString("password");
    	if (jdbcController.login(username, password)) {
    		propertyUtil.setUser(username);
    		//response with success
        	JSONObject response = new JSONObject();
        	response.put("status", "success");
            response.put("message", "Login successful");
            response.put("data", "");
            return new ResponseEntity<>(response.toString(), new HttpHeaders() ,HttpStatus.OK);
            
    	} else {
			// response with error
			JSONObject response = new JSONObject();
			response.put("status", "failure");
			response.put("message", "Login failed");
			response.put("data", "");
			return new ResponseEntity<>(response.toString(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    	}
    }
}
