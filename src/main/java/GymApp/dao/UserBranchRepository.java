package GymApp.dao;

import GymApp.entity.UserBranch;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserBranchRepository extends JpaRepository<UserBranch, UserBranch.Id> {

}
