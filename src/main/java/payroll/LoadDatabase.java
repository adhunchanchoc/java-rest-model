package payroll;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // class is pre-loaded automatically
class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

        return args -> {
            log.info("Preloading " + employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar")));
            log.info("Preloading " + employeeRepository.save(new Employee("Frodo", "Baggins", "thief")));

            orderRepository.save(new Order("Butterbread",Status.IN_PROGRESS));
            orderRepository.save(new Order("Wine",Status.COMPLETED));
            orderRepository.findAll().forEach(order -> log.info("Preloaded " +order));
        };
    }
}
