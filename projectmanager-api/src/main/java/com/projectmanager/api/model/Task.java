package com.projectmanager.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Task")
@EntityListeners(AuditingEntityListener.class)
public class Task {

	private long id;
	private long parentId;
	private long projectId;
	private String task;
	private Date startDate;
	private Date endDate;
	private int priority;
	private String status;

	/**
	 * 
	 * @param id
	 * @param parentId
	 * @param projectId
	 * @param task
	 * @param startDate
	 * @param endDate
	 * @param priority
	 * @param status
	 */
	public Task(long id, long parentId, long projectId, String task, Date startDate, Date endDate, int priority,
			String status) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.projectId = projectId;
		this.task = task;
		this.startDate = startDate;
		this.endDate = endDate;
		this.priority = priority;
		this.status = status;
	}

	/**
	 * 
	 * @param parentId
	 * @param projectId
	 * @param task
	 * @param startDate
	 * @param endDate
	 * @param priority
	 * @param status
	 */
	public Task(long parentId, long projectId, String task, Date startDate, Date endDate, int priority, String status) {
		super();
		this.parentId = parentId;
		this.projectId = projectId;
		this.task = task;
		this.startDate = startDate;
		this.endDate = endDate;
		this.priority = priority;
		this.status = status;
	}

	public Task() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "task_id", nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "parent_id")
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	@Column(name = "project_id")
	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	@Column(name = "task", nullable = false)
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	@Column(name = "start_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "priority")
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
