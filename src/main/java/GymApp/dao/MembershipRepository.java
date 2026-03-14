package GymApp.dao;

import GymApp.entity.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Page<Membership> findAllByBranch_Id(Long branchId, Pageable pageable);
}
