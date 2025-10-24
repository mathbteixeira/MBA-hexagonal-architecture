package br.com.fullcycle.hexagonal.application.entities;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class Customer {

    private CustomerId customerId;
    private String name;
    private String cpf;
    private String email;

    private Customer(CustomerId customerId, String name, String cpf, String email) {
        if (customerId == null) {
            throw new ValidationException("Invalid customerId for customer");
        }
        if (name == null) {
            throw new ValidationException("Invalid name for customer");
        }
        if (cpf == null || !cpf.matches("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$")) {
            throw new ValidationException("Invalid cpf for customer");
        }
        if (email == null || !email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")) {
            throw new ValidationException("Invalid email for customer");
        }
        this.customerId = customerId;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
    }

    public static Customer newCustomer(String name, String cpf, String email) {
        return new Customer(CustomerId.unique(), name, cpf, email);
    }

    public CustomerId customerId() {
        return customerId;
    }
    public String name() {
        return name;
    }
    public String cpf() {
        return cpf;
    }
    public String email() {
        return email;
    }
}
