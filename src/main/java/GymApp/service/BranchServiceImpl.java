package GymApp.service;

import GymApp.dao.BranchRepository;
import GymApp.entity.Branch;
import GymApp.exception.BranchNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public Branch findBranchById(Long branchId) {
        return branchRepository
                .findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Couldn't find branch with id: " +
                                                               branchId));
    }
}
