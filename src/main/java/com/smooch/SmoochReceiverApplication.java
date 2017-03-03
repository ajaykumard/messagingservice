package com.smooch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@RestController
public class SmoochReceiverApplication implements CommandLineRunner {

	List<String> welcome = new ArrayList<>();

	public static void main(String[] args) 
	{
		SpringApplication.run(SmoochReceiverApplication.class, args);
	}
	
	@RequestMapping(value="/messages",method=RequestMethod.POST)
	public void postMessages(@RequestBody Object obj)
	{
		System.out.println("ENTRY INTO SMOOCH MESSAGE RECEIVER"+obj.toString());
		
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			if(obj != null)
			{
				String messageJson = mapper.writeValueAsString(obj);
				replyToMessages(messageJson);
			}
		}
		catch(JsonProcessingException e)
		{
			e.printStackTrace();
		}
		System.out.println("EXIT SMOOCH MESSAGE RECEIVER");
	}
	
	@Override
	public void run(String... arg0) throws Exception 
	{
		welcome.add("Welcome to smooch receiver app");
	}
	
	@RequestMapping("/")
	public List init()
	{
		return welcome;
	}
	
	public void replyToMessages(String jsonMessage)
	{
		System.out.println("ENTRY INTO REPLYTOMESSAGES"+jsonMessage);
		
		try
		{
			if(jsonMessage != null)
			{
		
				JSONObject jsonStr = new JSONObject(jsonMessage);
				JSONArray messages = (JSONArray)jsonStr.get("messages");
				
				
				if(messages !=null && messages.length() > 0)
				{
					for(int i=0;i<messages.length();i++)
					{
						System.out.println("Received message: "+messages.getJSONObject(i).get("text"));
					}
				}
				
				//Fetch app user id from json to reply to message
				
				JSONObject appUser = (JSONObject)jsonStr.get("appUser");
				String appUserId = appUser.getString("_id");
				System.out.println("App User Id:"+appUserId);
				
				if(appUserId != null)
				{
					//Rest post call to smooch api to post the message to user
					
					String uri = "https://app.smooch.io/v1/appusers/"+appUserId+"/messages";
					System.out.println("Reply back URL:"+uri);
					
					//Forming the required headers for authentication
					
					HttpHeaders headers = new HttpHeaders();
					headers.set("authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6ImFwcF81OGE2ZDg4NDVkNWY4MzM5MDBhMjdlNjUifQ.eyJzY29wZSI6ImFwcCIsImlhdCI6MTQ4NzY0Nzk1Mn0.EPVgFzgElhCFg-eceUcV2Z4z7JXQarYukh7vnqYDrA4");
					headers.setContentType(MediaType.APPLICATION_JSON);
					
					JSONObject responseJson = new JSONObject();
					responseJson.put("role", "appMaker");
					responseJson.put("type", "text");
					responseJson.put("text", "Welcome to Innovations !!!");
					System.out.println("Response Json : "+responseJson);
					
					HttpEntity entity = new HttpEntity(responseJson.toString(), headers);
					
					RestTemplate restTemplate = new RestTemplate();
					
					ResponseEntity result = restTemplate.exchange(uri, HttpMethod.POST,entity, String.class);
					System.out.println("Result:"+result.getBody());
				}
			}
				
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		System.out.println("EXIT INTO REPLYTOMESSAGES");
	}
}
