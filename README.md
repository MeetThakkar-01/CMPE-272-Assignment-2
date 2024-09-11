# AWS Lambda with DynamoDB Integration

This project demonstrates how to implement AWS Lambda functions to interact with DynamoDB, handling CRUD operations via API Gateway based on different HTTP methods (GET, POST, PUT, DELETE).

## Project Overview

The project involves setting up an API Gateway that routes requests to an AWS Lambda function. The Lambda function performs Create, Read, Update, and Delete (CRUD) operations on a DynamoDB table.

## Steps to Create the Project and Deploy

### Prerequisites
- AWS account
- Java 8 or 11 (for Lambda)
- Maven (for managing dependencies and building the JAR)
- DynamoDB table (created via AWS Console or CLI)

### Steps to Create JAR and Upload to AWS Lambda Console

1. **Set Up Maven Project in Eclipse**
   - Create a new Maven project in Eclipse.
   - Add the required AWS SDK dependencies for DynamoDB and Lambda in the `pom.xml` file.
   
   Example `pom.xml`:
   ```xml
   <dependencies>
       <dependency>
           <groupId>software.amazon.awssdk</groupId>
           <artifactId>dynamodb</artifactId>
           <version>2.17.89</version>
       </dependency>
       <dependency>
           <groupId>software.amazon.awssdk</groupId>
           <artifactId>lambda</artifactId>
           <version>2.17.89</version>
       </dependency>
       <!-- Add any other dependencies required for your project -->
   </dependencies>
   
   <build>
       <plugins>
           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-shade-plugin</artifactId>
               <version>3.2.4</version>
               <executions>
                   <execution>
                       <phase>package</phase>
                       <goals>
                           <goal>shade</goal>
                       </goals>
                   </execution>
               </executions>
           </plugin>
       </plugins>
   </build>
   ```

  2. **Build the JAR file**
  
  Run the Maven build command to generate a shaded JAR (which includes all dependencies).
  
  ```bash
  
  mvn clean package
  ```
  This will generate a JAR file in the target/ directory, which can be uploaded to AWS Lambda.

3. **Create a Lambda Function in AWS Console**

    Navigate to the AWS Lambda Console.
    Click on Create Function and choose Author from scratch.
    Name your function, choose the runtime (Java 8 or 11), and set up the execution role (Lambda must have access to DynamoDB).

4. **Upload the JAR to AWS Lambda**

    Under Function code, select Upload from .zip or .jar file and upload the JAR file created in the previous step.

5. **Configure API Gateway**


    In the AWS Console, create a new API in API Gateway.
    Define the methods (GET, POST, PUT, DELETE) and map them to your Lambda function.
    Deploy the API.

5. **Test the API**

    Use Postman or any API testing tool to test your deployed API with CRUD operations.
    - Example POST body for creating an item:

   ```json
   {
      "payload": {
        "Id": "100",
        "Name": "Jenny Santiago",
        "DOB": "12/23/1979",
        "Gender": "Female"
      }
    }
    ```

   - Example GET body for reading an item:

   ```json
   {
      "payload": {
        "Id": "100"
      }
    }
    ```

## Conclusion
This project highlights how to integrate AWS Lambda, API Gateway, and DynamoDB to build a fully functional serverless CRUD application. The implementation involved overcoming challenges like API Gateway integration and method-based routing in Lambda, resulting in a valuable learning experience.
