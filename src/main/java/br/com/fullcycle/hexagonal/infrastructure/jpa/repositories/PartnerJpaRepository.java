package br.com.fullcycle.hexagonal.infrastructure.jpa.repositories;

import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.Partner;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PartnerJpaRepository extends CrudRepository<Partner, Long> {

    Optional<Partner> findByCnpj(String cnpj);

    Optional<Partner> findByEmail(String email);
}