package GymApp.service;

import GymApp.dao.OwnerRepository;
import GymApp.entity.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerServiceImpl implements OwnerService{
    @Autowired
    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Override
    public Optional<Owner> findById(long id) {
        return ownerRepository.findById(id);
    }

    @Override
    public Optional<Owner> findByAccountId(long accountId) {
        return ownerRepository.findByAccountId(accountId);
    }

    @Override
    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteById(long id) {
        ownerRepository.deleteById(id);
    }

    @Override
    public void deleteByAccountId(long accountId) {
        ownerRepository.deleteByAccountId(accountId);
    }
    @Override
    public void deleteAll(){
        ownerRepository.deleteAll();
    }
}
