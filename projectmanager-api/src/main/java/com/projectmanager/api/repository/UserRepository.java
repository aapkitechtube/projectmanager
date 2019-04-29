package com.projectmanager.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projectmanager.api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	@Query("select distinct new com.projectmanager.api.entity.User(u.id as id, u.firstName as firstName, "
			+ "u.lastName as lastName, u.employeeId as employeeId) from User u "
			+ " where u.projectId = 0 and u.taskId = 0 order by u.firstName, u.lastName asc")
	public List<User> findAllUsers ();
	
	@Query("select new com.projectmanager.api.entity.User (u.id, u.firstName, u.lastName, u.employeeId) from User u "
			+ "where u.projectId=?1 and u.taskId=0")
	public List<User> findUserByProjectId (long id);
	
	@Query("select new com.projectmanager.api.entity.User (u.id, u.firstName, u.lastName, u.employeeId) from User u "
			+ "where u.projectId=?1 and u.taskId=?2")
	public List<User> findUserByProjectIdAndTaskId (long projectId, long taskId);
	
	@Modifying
	@Transactional
	@Query(value = "delete from User u where u.projectId = ?1")
	public void deleteUsersByProjectId(long projectId);
}
