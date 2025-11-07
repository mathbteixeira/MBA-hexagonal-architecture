package br.com.fullcycle.hexagonal.infrastructure.jpa.repositories;

import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.PartnerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PartnerJpaRepository extends CrudRepository<PartnerEntity, Long> {

    Optional<PartnerEntity> findByCnpj(String cnpj);

    Optional<PartnerEntity> findByEmail(String email);
}