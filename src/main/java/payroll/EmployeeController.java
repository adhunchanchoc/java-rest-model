package payroll;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class EmployeeController {

    private final EmployeeRepository repository;
    //INJECTION by constructor
    EmployeeController(EmployeeRepository repository){
        this.repository = repository;
    }
    @GetMapping("/employees")
    List<Employee> all(){
        return repository.findAll();
    }
    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployeeFromBody) { // body data gets typecast as Employee object
        return repository.save(newEmployeeFromBody);
    }
    @GetMapping("/employees/{id}")
    Employee getOne(@PathVariable Long id){
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }
    @PutMapping("/employees/{id}")
    Employee replaceEmployee(@RequestBody Employee enteredEmployee,@PathVariable Long id){
        return repository.findById(id).map(e -> {
            e.setName(enteredEmployee.getName());
            e.setRole(enteredEmployee.getRole());
            return repository.save(e);
        }).orElseGet(()->{
            enteredEmployee.setId(id);
            return repository.save(enteredEmployee);
        });
    }
    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id){
        repository.deleteById(id);
    }

}
