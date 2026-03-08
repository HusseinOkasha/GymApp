package GymApp.service;

import GymApp.entity.Branch;

import java.util.List;

public interface BranchService {
    Branch findBranchById(Long branchId);
    List<Branch> findAllByIds(List<Long>branches);
}
