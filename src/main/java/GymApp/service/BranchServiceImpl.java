package GymApp.service;

import GymApp.dao.BranchRepository;
import GymApp.entity.Branch;
import GymApp.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() -> new NotFoundException("Couldn't find branch with id: " +
                                                         branchId));
    }

    @Override
    public List<Branch> findAllByIds(List<Long> branches) {
        return branchRepository.findAllById(branches);
    }
}
