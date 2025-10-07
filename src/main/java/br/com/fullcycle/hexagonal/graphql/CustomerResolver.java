package br.com.fullcycle.hexagonal.graphql;

import br.com.fullcycle.hexagonal.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;

@Controller
public class CustomerResolver {

    private final CustomerService customerService;

    public CustomerResolver(CustomerService customerService) {
        this.customerService = customerService;
    }

    @MutationMapping
    public CustomerDTO createCustomer(@Argument CustomerDTO dto) {
        if (customerService.findByCpf(dto.getCpf()).isPresent()) {
            throw new RuntimeException("Customer already exists");
        }
        if (customerService.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Customer already exists");
        }

        var customer = new Customer();
        customer.setName(dto.getName());
        customer.setCpf(dto.getCpf());
        customer.setEmail(dto.getEmail());

        customer = customerService.save(customer);

        return new CustomerDTO(customer);
    }

    public CustomerDTO customerOfId(@Argument Long id) {
        return customerService.findById(id).map(CustomerDTO::new).orElse(null);
    }
}
