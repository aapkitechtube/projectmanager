import { Component, LOCALE_ID, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { TaskService } from '../service/task.service';
import { ActivatedRoute } from '@angular/router';
import { MatTableDataSource, MatSort } from '@angular/material';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ProjectService } from '../service/project.service';
import { UserService } from '../service/user.service';
import { formatDate } from '@angular/common';
import { DateValidators } from '../app.component';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  taskForm: FormGroup
  submitted = false
  display = 'none'
  addButton = 'Add'
  allProjects: any
  allParentTasks: any
  allUsers: any
  task: any
  isParent = false
  modalHeader = 'Search Project'
  projectColumns: any
  parentTaskColumns: any
  userColumns: any
  displayedColumns = ["project", "startDate", "endDate"]
  dataSource: MatTableDataSource<Object>;

  private _success = new Subject<string>();
  staticAlertClosed = false;
  successMessage: string;

  currentDate = new Date();
  nextDay = new Date();

  isSearchProject = false
  isSearchParent = false
  isSearchUser = false

  @ViewChild(MatSort) matSort: MatSort;

  constructor(private formBuilder: FormBuilder,
    private taskService: TaskService,
    private projectService: ProjectService,
    private userService: UserService,
    private activatedRoute: ActivatedRoute) {
    this.createTaskForm ()
  }

  createTaskForm () {
    this.taskForm = this.formBuilder.group(
      {
        project: new FormControl({ value: '', disabled: true }),
        projectId: new FormControl('', [Validators.required]),
        task: new FormControl('', [Validators.required]),
        id: new FormControl(''),
        markParentTask: new FormControl(''),
        priority: new FormControl(0),
        startDate: new FormControl('', [Validators.required]),
        endDate: new FormControl('', [Validators.required]),
        parentTask: new FormControl({ value: '', disabled: true }),
        parentId: new FormControl(''),
        user: new FormControl({ value: '', disabled: true }),
        userId: new FormControl('', [Validators.required])
      },
      { validator: Validators.compose([
          DateValidators.dateLessThan('startDate', 'endDate', { 'loaddate': true })
        ])
      }
    )
  }

  ngOnInit() {
    this.taskForm.patchValue({
      startDate: formatDate(this.currentDate, 'yyyy-MM-dd', 'en'),
      endDate: formatDate(this.nextDay.setDate(this.currentDate.getDate() + 1), 'yyyy-MM-dd', 'en'),
      priority: 0
    });

    this.activatedRoute.queryParams.subscribe(params => {
      this.task = this.taskService.editTaskData
      this.taskService.editTaskData = null
      if (this.task != null && this.task['id'] > 0) {
        this.addButton = 'Update'
        this.taskForm.controls['markParentTask'].disable()
        this.getUserByProjectAndTask()
      } else {
        this.addButton = 'Add'
        this.taskForm.reset()
      }
    });

    this.handleParent(this.taskForm.controls['markParentTask'])
    this.getAllProjects()
    this.getAllParentTasks()
    this.getAllUsers()

    setTimeout(() => this.staticAlertClosed = true, 20000);
    this._success.subscribe((message) => this.successMessage = message);
    this._success.pipe(
      debounceTime(50000)
    ).subscribe(() => this.successMessage = null);

  }

  resetForm() {
    this.taskForm.controls['markParentTask'].enable()
    this.taskForm.controls['startDate'].enable()
    this.taskForm.controls['endDate'].enable()
    this.taskForm.controls['priority'].enable()
    this.addButton = 'Add'
    this.isParent = false
    this.taskForm.reset()
    this.taskForm.patchValue({
      startDate: formatDate(this.currentDate, 'yyyy-MM-dd', 'en'),
      endDate: formatDate(this.nextDay.setDate(this.currentDate.getDate() + 1), 'yyyy-MM-dd', 'en'),
      priority: 0
    });
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  addTask() {
    this.submitted = true
    if (this.taskForm.invalid) {
      return;
    }

    if (this.taskForm.controls['markParentTask'].value) {
      this.createParentTask({ 'parentTask': this.taskForm.controls['task'].value })
    } else {
      // Handle add edit task
      if (this.addButton == 'Add') {
        //Add flow
        let taskData = this.taskForm.value
        delete taskData['id']
        delete taskData['userId']
        delete taskData['markParentTask']
        if (this.taskForm.controls['parentId'].value === null) {
          delete taskData['parentId']
        }
        this.createTask(taskData, this.taskForm.controls['userId'].value)
      } else {
        // update flow
        let taskData = this.taskForm.value
        delete taskData['id']
        delete taskData['markParentTask']
        if (this.taskForm.controls['parentId'].value === null) {
          delete taskData['parentId']
        }
        this.updateTask(taskData, this.taskForm.controls['id'].value, 
          this.taskForm.controls['userId'].value)
      }
    }
  }

  getTaskById(taskId: number) {
    this.taskService.getTaskById(taskId).subscribe(
      data => {
        this.task = data
      });
  }

  createParentTask(parentTask: any) {
    this.taskService.createParentTask(parentTask).subscribe(
      data => {
        this.changeSuccessMessage('Parent task created successfully!')
        this.getAllParentTasks()
        this.dataSource = new MatTableDataSource(this.allParentTasks)
      }
    )
  }

  createTask(task: any, userId: number) {
    this.taskService.createTask(task, userId).subscribe(
      data => {
        this.changeSuccessMessage('Task created successfully!')
      }
    )
  }

  updateTask(task: any, taskId: number, userId: number) {
    this.taskService.updateTask(task, taskId, userId).subscribe(
      data => {
        this.changeSuccessMessage('Task updated successfully!')
      }
    )
  }

  getUserByProjectAndTask() {
    this.userService.getUserByProjectAndTaskId(this.task['projectId'], this.task['id'])
      .subscribe(
        data => {
          this.task['user'] = data['firstName'] + ' ' + data['lastName']
          this.task['userId'] = data['id']
          this.taskForm.setValue(this.task)
        });
  }

  selectProject(project: any) {
    this.taskForm.controls['projectId'].setValue(project['id'])
    this.taskForm.controls['project'].setValue(project['project'])
    this.closeModalDialog()
  }

  selectParent(parent: any) {
    this.taskForm.controls['parentId'].setValue(parent['id'])
    this.taskForm.controls['parentTask'].setValue(parent['parentTask'])
    this.closeModalDialog()
  }

  selectUser(user: any) {
    this.taskForm.controls['userId'].setValue(user['id'])
    this.taskForm.controls['user'].setValue(user['firstName'] + ' ' + user['lastName'])
    this.closeModalDialog()
  }

  openProjectDialog() {
    this.getAllProjects()
    this.isSearchProject = true
    this.isSearchParent = false
    this.isSearchUser = false
    this.modalHeader = "Select Project"
    this.displayedColumns = this.projectColumns
    this.dataSource = new MatTableDataSource(this.allProjects)
    this.dataSource.sort = this.matSort;
    this.display = 'block';
  }

  getAllProjects() {
    this.projectService.getProjects().subscribe(
      data => {
        this.allProjects = data
        this.projectColumns = ["project", "startDate", "endDate"]
      });
  }

  openParentDialog() {
    this.getAllParentTasks()
    this.isSearchProject = false
    this.isSearchParent = true
    this.isSearchUser = false
    this.modalHeader = "Select Parent Task"
    this.displayedColumns = this.parentTaskColumns
    this.dataSource = new MatTableDataSource(this.allParentTasks)
    this.dataSource.sort = this.matSort;
    this.display = 'block';
  }

  getAllParentTasks() {
    this.taskService.getAllParentTasks().subscribe(
      data => {
        this.allParentTasks = data
        this.parentTaskColumns = ["parentTask"]
      });
  }

  openUserDialog() {
    this.getAllUsers()
    this.isSearchProject = false
    this.isSearchParent = false
    this.isSearchUser = true
    this.modalHeader = "Select User"
    this.displayedColumns = this.userColumns
    this.dataSource = new MatTableDataSource(this.allUsers)
    this.dataSource.sort = this.matSort;
    this.display = 'block';
  }

  getAllUsers() {
    this.userService.getAllUsers().subscribe(
      data => {
        this.allUsers = data
        this.userColumns = ["employeeId", "firstName", "lastName"]
      });
  }

  handleParent(target: any) {
    if (target.checked) {
      this.isParent = true
      this.taskForm.patchValue({
        startDate: '',
        endDate: ''
      });
      this.taskForm.controls['priority'].disable()
      this.taskForm.controls['startDate'].disable()
      this.taskForm.controls['endDate'].disable()
      this.taskForm.controls['userId'].setValidators(null)
      this.taskForm.controls["userId"].updateValueAndValidity();
      this.taskForm.controls['projectId'].setValidators(null)
      this.taskForm.controls["projectId"].updateValueAndValidity();
    } else {
      this.isParent = false
      this.taskForm.controls['priority'].enable()
      this.taskForm.controls['startDate'].enable()
      this.taskForm.controls['endDate'].enable()
      this.taskForm.controls['userId'].setValidators([Validators.required])
      this.taskForm.controls["userId"].updateValueAndValidity()
      this.taskForm.controls['projectId'].setValidators([Validators.required])
      this.taskForm.controls["projectId"].updateValueAndValidity();
      this.taskForm.patchValue({
        startDate: formatDate(this.currentDate, 'yyyy-MM-dd', 'en'),
        endDate: formatDate(this.nextDay.setDate(this.currentDate.getDate() + 1), 'yyyy-MM-dd', 'en'),
        priority: 0
      });
    }
  }

  closeModalDialog() {
    this.display = 'none';
  }

  public changeSuccessMessage(message: string) {
    this._success.next(message);
  }
}
