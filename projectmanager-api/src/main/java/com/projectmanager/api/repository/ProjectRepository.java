package com.projectmanager.api.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projectmanager.api.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	
	@Query("select distinct new map(p.id as projectId, p.project as project, p.priority as priority, "
			+ "DATE_FORMAT(p.startDate, '%Y-%m-%d') as startDate, "
			+ "DATE_FORMAT(p.endDate, '%Y-%m-%d') as endDate, (select count(1) from Task t1 "
			+ "where t1.projectId = p.id) as totalTasks, (select count(1) from Task t2 "
			+ "where t2.projectId = p.id and t2.status = 'complete') as taskCompleted) "
			+ "from Project p left outer join Task t on p.id = t.projectId")
	public List<Map<String, String>> getProjectAndTaskDetails ();
}
