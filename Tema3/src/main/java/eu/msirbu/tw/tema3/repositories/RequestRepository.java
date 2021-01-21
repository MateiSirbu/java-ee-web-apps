/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.repositories;

import eu.msirbu.tw.tema3.entities.Request;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<Request, Integer> { }