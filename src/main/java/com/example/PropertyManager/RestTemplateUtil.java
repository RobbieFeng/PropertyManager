package com.example.PropertyManager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestTemplateUtil {
	RestTemplate rt = new RestTemplate();
    @GetMapping("/getUserInfo")
    public Account getUserList(String username, String password, String email) {
    	Account account = new Account(username,password,email);
        return account;
    }

    @PostMapping("/postUserInfo")
    public Account postUserInfo(@RequestBody String str) throws JSONException {
    	JSONObject jsonObject = new JSONObject(str);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        Account account = new Account(username,password,email);
        return account;
    }
    
    @GetMapping("/1")
    public Account postUserInfoRest() {
    	System.out.println("hello");
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("username", "myname");
        requestBody.put("password", "mypassword");
        requestBody.put("email", "12@mail.com");
        JSONObject json = new JSONObject(requestBody);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> r = new HttpEntity<String>(json.toString(), requestHeaders);
        String url = "http://localhost:8080/postUserInfo";
        Account result = rt.postForObject(url, r, Account.class);
        
        System.out.println(result);
        return result;
    }


}
