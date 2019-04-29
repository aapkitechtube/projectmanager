package com.projectmanager.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "project_id", "task_id"})}
)
@EntityListeners(AuditingEntityListener.class)
public class User {

	private long id;
	private String firstName;
	private String lastName;
	private String employeeId;
	private long projectId;
	private long taskId;
	
	/**
	 * 
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param employeeId
	 * @param projectId
	 * @param taskId
	 */
	public User(long id, String firstName, String lastName, String employeeId, long projectId, long taskId) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeId = employeeId;
		this.projectId = projectId;
		this.taskId = taskId;
	}
	
	/**
	 * 
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param employeeId
	 */
	public User(long id, String firstName, String lastName, String employeeId) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeId = employeeId;
	}
	
	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @param employeeId
	 * @param projectId
	 * @param taskId
	 */
	public User(String firstName, String lastName, String employeeId, long projectId, long taskId) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeId = employeeId;
		this.projectId = projectId;
		this.taskId = taskId;
	}
	
	public User() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id", nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "first_name", nullable = false)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(name = "last_name", nullable = false)
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name = "employee_id", nullable = false)
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	@Column(name = "project_id")
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	
	@Column(name = "task_id")
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
}
