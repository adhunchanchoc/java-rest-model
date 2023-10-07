package payroll;

public class EmployeeNotFoundException extends RuntimeException {
    EmployeeNotFoundException(Long id){
        super("Employee no. "+ id + " has not been found");
    }
}
