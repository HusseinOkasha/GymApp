package GymApp.service;

import GymApp.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> findAll();
    Optional<Client> findById(long id);
    Optional<Client> findByAccountId(long accountId);
    Optional<Client> save(Client client);
    void deleteById(long Id);
    void deleteByAccount_Email(String email);
    void deleteByAccount_PhoneNumber(String phoneNumber);

    void deleteByAccountId(long accountId);
    void deleteAll();

}
