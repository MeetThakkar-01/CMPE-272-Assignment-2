package DynamoDBLambdaCRUD.Assignment;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class DynamoDbLambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
	private static final String TABLE_NAME = "StudentRecords";

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> response = new HashMap<>();

		// Extract the "body" field from the event (which is a string)
		String body = (String) event.get("body");

		// Parse the body as JSON
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		try {
			bodyMap = objectMapper.readValue(body, Map.class);
			System.out.println("######### " + event);
			
			Map<String, Object> requestContext = (Map<String, Object>) event.get("requestContext");
			Map<String, Object> http = (Map<String, Object>) requestContext.get("http");
			String httpMethod = (String) http.get("method");
			
			Map<String, Object> payload = (Map<String, Object>) bodyMap.get("payload");

			switch (httpMethod) {
			case "POST":
				return createItem(payload);
			case "GET":
				return readItem(payload);
			case "PUT":
				return updateItem(payload);
			case "DELETE":
				return deleteItem(payload);
			default:
				response.put("statusCode", 405); // Method Not Allowed
				response.put("body", "{\"message\": \"HTTP Method Not Supported\"}");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.put("statusCode", 500);
			response.put("body", "{\"message\": \"Internal Server Error: " + e.getMessage() + "\"}");
		}

		return response;
	}

	private Map<String, Object> createItem(Map<String, Object> payload) {
		Map<String, AttributeValue> item = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		item.put("student_id", AttributeValue.builder().n((String) payload.get("Id")).build());
		item.put("Name", AttributeValue.builder().s((String) payload.get("Name")).build());
		item.put("DOB", AttributeValue.builder().s((String) payload.get("DOB")).build());
		item.put("Gender", AttributeValue.builder().s((String) payload.get("Gender")).build());

		PutItemRequest request = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();
		dynamoDbClient.putItem(request);

		response.put("statusCode", 201);
		response.put("body", "{\"message\": \"Item created\"}");

		return response;
	}

	private Map<String, Object> readItem(Map<String, Object> payload) {
		Map<String, AttributeValue> key = new HashMap<>();
		Map<String, Object> res = new HashMap<>();
		key.put("student_id", AttributeValue.builder().n((String) payload.get("Id")).build());

		GetItemRequest request = GetItemRequest.builder().tableName(TABLE_NAME).key(key).build();
		GetItemResponse response = dynamoDbClient.getItem(request);

		for (String k : response.item().keySet()) {
			res.put(k, response.item().get(k).s());
		}
		return res;
	}

	private Map<String, Object> updateItem(Map<String, Object> payload) {
		Map<String, AttributeValue> key = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		key.put("student_id", AttributeValue.builder().n((String) payload.get("Id")).build());

		Map<String, AttributeValueUpdate> updatedValues = new HashMap<>();
		updatedValues.put("Name",
				AttributeValueUpdate.builder().value(AttributeValue.builder().s((String) payload.get("Name")).build())
						.action(AttributeAction.PUT).build());
		updatedValues.put("DOB",
				AttributeValueUpdate.builder().value(AttributeValue.builder().s((String) payload.get("DOB")).build())
						.action(AttributeAction.PUT).build());
		updatedValues.put("Gender",
				AttributeValueUpdate.builder().value(AttributeValue.builder().s((String) payload.get("Gender")).build())
						.action(AttributeAction.PUT).build());

		UpdateItemRequest request = UpdateItemRequest.builder().tableName(TABLE_NAME).key(key)
				.attributeUpdates(updatedValues).build();
		dynamoDbClient.updateItem(request);

		response.put("statusCode", 200);
		response.put("body", "{\"message\": \"Item updated\"}");

		return response;
	}

	private Map<String, Object> deleteItem(Map<String, Object> payload) {
		Map<String, AttributeValue> key = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		key.put("student_id", AttributeValue.builder().n((String) payload.get("Id")).build());

		DeleteItemRequest request = DeleteItemRequest.builder().tableName(TABLE_NAME).key(key).build();
		dynamoDbClient.deleteItem(request);
		response.put("statusCode", 200);
		response.put("body", "{\"message\": \"Item deleted\"}");
		return response;
	}
}
