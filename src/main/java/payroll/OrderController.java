package payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {
    private final OrderRepository repository;
    private final OrderModelAssembler assembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/orders")
    CollectionModel<EntityModel<Order>> getAll() {
        List<EntityModel<Order>> orderModels = repository.findAll().stream()
                .map((assembler::toModel))
                .collect(Collectors.toList());
        return CollectionModel.of(orderModels, linkTo(methodOn(OrderController.class).getAll()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    EntityModel<Order> getOne(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    ResponseEntity<?> newOrder(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        EntityModel<Order> orderModel = assembler.toModel(repository.save(order));
        return ResponseEntity
//                .created(orderModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .created(linkTo(methodOn(OrderController.class).getOne(order.getId())).toUri())
                .body(orderModel);
    }

    @DeleteMapping("/orders/{id}/cancel")
    ResponseEntity<?> cancel(@PathVariable Long id) {

        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order))); //needed to save state
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method is not allowed")
                        .withDetail("Order with " + order.getStatus() + " cannot be cancelled"));
    }

    @PutMapping("/orders/{id}/complete")
    ResponseEntity<?> complete(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE,MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method is not allowed")
                        .withDetail("Order with " + order.getStatus() + " cannot be completed"));
    }
}
