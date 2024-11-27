import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import utils.APIConstants;
import utils.APIPayloadConstants;
import utils.DbReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class APIDataBase {
    String baseURI = RestAssured.baseURI = "http://hrm.syntaxtechs.net/syntaxapi/api";
    RequestSpecification request;
    Response response;
    public static String employee_id;
    public static String token;
    public static String empID;


    @Given("a JWT bearer token is generated")
    public void a_jwt_bearer_token_is_generated() {
        request = given().
                header(APIConstants.HEADER_CONTENT_TYPE_KEY,APIConstants.HEADER_CONTENT_TYPE_VALUE).
                body("{\n" +
                        "  \"email\": \"naima@123gmail.com\",\n" +
                        "  \"password\": \"vvddhon2011\"\n" +
                        "}\n");
        response = request.when().post(APIConstants.GENERATE_TOKEN);
        token = "Bearer "+ response.jsonPath().getString("token");
        System.out.println(token);

    }

    @Given("a request is prepared for creating the employee")
    public void a_request_is_prepared_for_creating_the_employee() {
        request = given().
                header(APIConstants.HEADER_CONTENT_TYPE_KEY,APIConstants.HEADER_CONTENT_TYPE_VALUE).
                header(APIConstants.HEADER_AUTHORIZATION_KEY,token).
                body(APIPayloadConstants.createEmployeePayload());
    }
    @When("a POST call is made to create the employee")
    public void a_post_call_is_made_to_create_the_employee() {
        response = request.when().post(APIConstants.CREATE_EMPLOYEE);
    }
    @Then("the status code for this request is {int}")
    public void the_status_code_for_this_request_is(Integer int1) {
        response.then().assertThat().statusCode(int1);
    }
    @Then("the employee id {string} is stored and values are validated")
    public void the_employee_id_is_stored_and_values_are_validated(String empPath) {
        employee_id = response.jsonPath().getString(empPath);
        System.out.println("The employee id is " + employee_id);
        response.then().assertThat().
                body("Employee.emp_firstname",equalTo("Joseph")).
                body("Employee.emp_lastname",equalTo("Johnson"));
    }
    // @api retrieve valid employee ID

    @Given("a valid employee ID exists")
    public void a_valid_employee_id_exists() {
        request = given().
                header(APIConstants.HEADER_CONTENT_TYPE_KEY,APIConstants.HEADER_CONTENT_TYPE_VALUE).
                header(APIConstants.HEADER_AUTHORIZATION_KEY,token).
                queryParam("employee_id",employee_id);
    }
    @When("I send a GET request to retrieve the employee")
    public void i_send_a_get_request_to_retrieve_the_employee() {
        response = request.when().get(APIConstants.GET_ONE_EMPLOYEE);
    }
    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }
    @Then("the employee has same employee id {string} as global empid")
    public void the_employee_has_same_employee_id_as_global_empid(String empId) {
        String temporaryEmpId = response.jsonPath().getString(empId);
        Assert.assertEquals(employee_id, temporaryEmpId);
    }
    @Then("the data coming from the get call should equal to the data used in POST call")
    public void the_data_coming_from_the_get_call_should_equal_to_the_data_used_in_post_call
            (io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> expectedData = dataTable.asMaps();
        Map<String, String> actualData = response.jsonPath().get("employee");
        for (Map<String, String> employeeData: expectedData
        ) {
            Set<String> keys = employeeData.keySet();
            for (String key:keys
            ) {
                String expectedValue  =  employeeData.get(key);
                String actualValue = actualData.get(key);
                Assert.assertEquals(expectedValue, actualValue);
            }
        }
    }
    // @api retrieve invalid employee ID

    @Given("an invalid employee ID {string}")
    public void an_invalid_employee_id(String invalidEmployeeID) {
        empID = invalidEmployeeID;
    }
    @When("I send a GET request to retrieve the invalid employee id")
    public void i_send_a_get_request_to_retrieve_the_invalid_employee_id() {
        response = given()
                .header(APIConstants.HEADER_CONTENT_TYPE_KEY, APIConstants.HEADER_CONTENT_TYPE_VALUE)
                .header(APIConstants.HEADER_AUTHORIZATION_KEY, token)
                .queryParam("employee_id", empID)
                .get(APIConstants.GET_ONE_EMPLOYEE);
    }
    @Then("the invalid employeeID response status code should be {int}")
    public void the_invalid_employee_id_response_status_code_should_be(Integer invalidCode) {
        response.then().assertThat().statusCode(invalidCode);
    }
    @Then("the response should contain an error message {string}")
    public void the_response_should_contain_an_error_message(String expectedMessage) {
        response.then().assertThat().body("massage", equalTo(expectedMessage));
    }

    // @api delete valid employee ID

    @Given("a valid employee ID {string}")
    public void a_valid_employee_id(String validEmployeeID) {
        employee_id=validEmployeeID;

    }
    @Then("the response should contain a success message {string}")
    public void the_response_should_contain_a_success_message(String expectedMessage) {
        response.then().assertThat().body("message", equalTo(expectedMessage)); // Validate success message
        System.out.println("Validated Success Message: " + expectedMessage);

    }
    @Then("the response should include the deleted employee details")
    public void the_response_should_include_the_deleted_employee_details() {
        response.then().assertThat()
                .body("employee.employee_id", equalTo(employee_id))
                .body("employee.emp_firstname", equalTo("Joseph"))
                .body("employee.emp_lastname", equalTo("Johnson"))
                .body("employee.emp_middle_name", equalTo("JJ"))
                .body("employee.emp_gender", equalTo("Male"))
                .body("employee.emp_birthday", equalTo("2001-11-23"));
        System.out.println("Validated Deleted Employee Details for Employee ID: " + employee_id);
    }


    // @api1 retrieve deleted employee with employee ID

    @Given("retrieve deleted employee with employee ID {string}")
    public void retrieve_deleted_employee_with_employee_id(String employeeID) {
        employee_id = employeeID;
    }
    @When("I send a DELETE request to remove the employee")
    public void i_send_a_delete_request_to_remove_the_employee() {
        request = given()
                .header(APIConstants.HEADER_CONTENT_TYPE_KEY, APIConstants.HEADER_CONTENT_TYPE_VALUE)
                .header(APIConstants.HEADER_AUTHORIZATION_KEY, token)
                .queryParam("employee_id", employee_id);

        response = request.delete(APIConstants.DELETE_EMPLOYEE);
    }
    @Then("the deleted response status code should be {int}")
    public void the_deleted_id_response_status_code_should_be(Integer notFound) {
        response.then().assertThat().statusCode(notFound);

    }
    @Then("the deleted employeeID response should contain an error message {string}")
    public void the_deleted_employee_id_response_should_contain_an_error_message(String expectedMessage) {
        response.then().assertThat().body("Massage", equalTo(expectedMessage));
        // Retrieve and print the error message to the console
        String actualMessage = response.jsonPath().getString("Massage");
        System.out.println("Validated Error Message: " + actualMessage);
    }

    // @api1  DELETE request without an employee ID

    @When("I send a DELETE request without an employee ID")
    public void i_send_a_delete_request_without_an_employee_id() {
        request = given().header(APIConstants.HEADER_CONTENT_TYPE_KEY,APIConstants.HEADER_CONTENT_TYPE_VALUE).
                header(APIConstants.HEADER_AUTHORIZATION_KEY,token);
        response = request.delete(APIConstants.DELETE_EMPLOYEE);
    }
    @Then("the response without employee ID status code should be {int}")
    public void the_response_without_employee_id_status_code_should_be(Integer errorCode) {
        response.then().assertThat().statusCode(errorCode);
    }
    @Then("the response without employee ID should contain an error message {string}")
    public void the_response_without_employee_id_should_contain_an_error_message(String expectedMassage) {
        response.then().assertThat().body("Error", equalTo(expectedMassage));
        // Print the error message to the console
        String actualMessage = response.jsonPath().getString("Error");
        System.out.println("Validated Error Message: " + actualMessage);
    }

    @Then("the employee with ID {string} should exist in the database with first name {string}")
    public void the_employee_with_id_should_exist_in_the_database_with_first_name(String employeeId, String expectedFirstName) {
        String query = "SELECT emp_firstname FROM hs_hr_employees WHERE employee_id = '" + employee_id+ "'";
        List<Map<String, String>> result = null;
        result = DbReader.fetch(query);

        if (!result.isEmpty()) {
            String actualFirstName = result.get(0).get("emp_firstname");
            Assert.assertEquals("First name mismatch!", expectedFirstName, actualFirstName);
            System.out.println("Employee creation validated in the database.");
        } else {
            Assert.fail("Employee not found in the database.");
        }
    }


    @Then("the employee with ID {string} should not exist in the database")
    public void the_employee_with_id_should_not_exist_in_the_database(String employeeId) throws IOException {
        String query = "SELECT * FROM hs_hr_employees WHERE employee_id = '" + employeeId + "'";
        List<Map<String, String>> result = null;
        result = DbReader.fetch(query);

        if (result.isEmpty()) {
            System.out.println("Employee successfully deleted from the database.");
        } else {
            Assert.fail("Employee still exists in the database!");
        }

    }

}
