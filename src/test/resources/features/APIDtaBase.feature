Feature: API Workflow

  Background:
    Given a JWT bearer token is generated

  @api
  Scenario: create employee
    Given a request is prepared for creating the employee
    When a POST call is made to create the employee
    Then the status code for this request is 201
    Then the employee id "Employee.employee_id" is stored and values are validated

  @api
  Scenario: Retrieve employee details with a valid employee ID
    Given a valid employee ID exists
    When I send a GET request to retrieve the employee
    Then the response status code should be 200
    And the employee has same employee id "employee.employee_id" as global empid
    And the data coming from the get call should equal to the data used in POST call
      | emp_firstname | emp_lastname | emp_middle_name | emp_gender | emp_birthday | emp_status | emp_job_title |
      | Joseph        | Johnson      | JJ              | Male       | 2001-11-23   | Trainee    | Manager       |

  @api
  Scenario: Retrieve employee details with an invalid employee ID
    Given an invalid employee ID "999999"
    When I send a GET request to retrieve the invalid employee id
    Then the invalid employeeID response status code should be 400
    And the response should contain an error message "Employee does not exist or you have provided invalid employee_id"

  @api1
  Scenario: Delete valid employee ID
    Given a valid employee ID "115676A"
    When I send a DELETE request to remove the employee
    Then the response status code should be 200
    And the response should contain a success message "Employee deleted"
    And the response should include the deleted employee details

  @api1
  Scenario: Retrieve Deleted employee with employee ID
    Given retrieve deleted employee with employee ID "115682A"
    When I send a DELETE request to remove the employee
    Then the deleted response status code should be 404
    And the deleted employeeID response should contain an error message "Employee does not exist or you have provided invalid employee_id"

  @api1
  Scenario: Delete request without an employee ID
    When I send a DELETE request without an employee ID
    Then the response without employee ID status code should be 400
    And the response without employee ID should contain an error message "Please provide employee_id"
  @db
  Scenario: Validate employee creation in the database
    Given a request is prepared for creating the employee
    When a POST call is made to create the employee
    Then the status code for this request is 201
    Then the employee id "Employee.employee_id" is stored and values are validated
    And the employee with ID "Employee.employee_id" should exist in the database with first name "Joseph"

  @db
  Scenario: Validate employee deletion in the database
    Given a valid employee ID "115682A"
    When I send a DELETE request to remove the employee
    Then the response should contain a success message "Employee deleted"
    And the employee with ID "115682A" should not exist in the database