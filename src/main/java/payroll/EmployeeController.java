package payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController // the data returned by each method will be written straight into the response body instead of rendering a template
class EmployeeController {
    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    //INJECTION by constructor (could be done via @Autowired annotation as well)
    EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
        //creates really RESTful representation of an object with links
    EntityModel<Employee> getOne(@PathVariable Long id) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployeeFromBody) { // body data gets typecast as Employee object
        EntityModel<Employee> employeeEntityModel = assembler.toModel(repository.save(newEmployeeFromBody));
        return ResponseEntity
                .created(employeeEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(employeeEntityModel);
    }

    //    @PostMapping("/employees")
//    Employee newEmployee(@RequestBody Employee newEmployeeFromBody) { // body data gets typecast as Employee object
//        return repository.save(newEmployeeFromBody);
//    }
    @PutMapping("/employees/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        Employee updatedEmployee = repository.findById(id)
                .map(e -> {
                    e.setName(newEmployee.getName());
                    e.setRole(newEmployee.getRole());
                    return repository.save(e);
                }).orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee); // wrapping Entity into EntityModel
        return ResponseEntity // wrapper to get more detailed HTTP response code than 200 OK, + it fills a Location header
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //retrieve the Link created by the EmployeeModelAssembler, then convert ot URI
                .body(entityModel);
    }

    //    @PutMapping("/employees/{id}")
//    Employee replaceEmployee(@RequestBody Employee enteredEmployee,@PathVariable Long id){
//        return repository.findById(id).map(e -> {
//            e.setName(enteredEmployee.getName());
//            e.setRole(enteredEmployee.getRole());
//            return repository.save(e);
//        }).orElseGet(()->{
//            enteredEmployee.setId(id);
//            return repository.save(enteredEmployee);
//        });
//    }
    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build(); // returns an HTTP 204 No Content
    }
//    @DeleteMapping("/employees/{id}")
//    void deleteEmployee(@PathVariable Long id) {
//        repository.deleteById(id);
//    }

}
