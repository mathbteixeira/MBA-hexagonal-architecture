<<<<<<<< Updated upstream:infrastructure/src/main/java/br/com/fullcycle/models/Customer.java
package br.com.fullcycle.hexagonal.models;

========
package br.com.fullcycle.infrastructure.jpa.entities;

import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerId;
>>>>>>>> Stashed changes:infrastructure/src/main/java/br/com/fullcycle/infrastructure/jpa/entities/CustomerEntity.java
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

<<<<<<<< Updated upstream:infrastructure/src/main/java/br/com/fullcycle/models/Customer.java
import static jakarta.persistence.GenerationType.*;

@Entity
========
@Entity(name = "Customer")
>>>>>>>> Stashed changes:infrastructure/src/main/java/br/com/fullcycle/infrastructure/jpa/entities/CustomerEntity.java
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    private String cpf;

    private String email;

    public Customer() {
    }

    public Customer(Long id, String name, String cpf, String email) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
