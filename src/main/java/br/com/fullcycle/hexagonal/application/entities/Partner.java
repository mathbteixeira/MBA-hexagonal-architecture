package br.com.fullcycle.hexagonal.application.entities;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class Partner {

    private PartnerId partnerId;
    private Name name;
    private Cnpj cnpj;
    private Email email;

    private Partner(PartnerId partnerId, String name, String cnpj, String email) {
        if (partnerId == null) {
            throw new ValidationException("Invalid partnerId for partner");
        }

        this.partnerId = partnerId;
        this.name = new Name(name);
        this.cnpj = new Cnpj(cnpj);
        this.email = new Email(email);
    }

    public static Partner newPartner(String name, String cnpj, String email) {
        return new Partner(PartnerId.unique(), name, cnpj, email);
    }

    public PartnerId partnerId() {
        return partnerId;
    }
    public Name name() {
        return name;
    }
    public Cnpj cnpj() {
        return cnpj;
    }
    public Email email() {
        return email;
    }
}
