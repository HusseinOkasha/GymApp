package GymApp.service;

import GymApp.dao.ClientRepository;
import GymApp.entity.Client;
import GymApp.entity.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements  ClientService{
    @Autowired
    private final ClientRepository clientRepository;


    public ClientServiceImpl(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }
    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Client> findById(long id) {
        return clientRepository.findById(id);
    }

    @Override
    public Optional<Client> findByAccountId(long accountId) {
        return clientRepository.findByAccountId(accountId);
    }

    @Override
    public Optional<Client> save(Client client) {
        return Optional.of(clientRepository.save(client));
    }

    @Override
    public void deleteById(long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public void deleteByAccountId(long accountId) {
        clientRepository.deleteByAccountId(accountId);
    }
}
