package com.projectmanager.api.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projectmanager.api.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
	@Query(value = "select new map(t.id as id, t.task as task, pt.parentTask as parentTask, pt.id as parentId, "
			+ "DATE_FORMAT(t.startDate, '%Y-%m-%d') as startDate, DATE_FORMAT(t.endDate, '%Y-%m-%d') as endDate, "
			+ "t.priority as priority, t.status as status) from Task t "
			+ " left outer join ParentTask pt on t.parentId = pt.id where t.projectId=?1")
	public List<Map<String, String>> findAllTasksByProjectId (long projectId);
	
	@Modifying
	@Transactional
	@Query(value = "delete from Task t where t.projectId = ?1")
	public void deleteTasksByProjectId(long projectId);
}