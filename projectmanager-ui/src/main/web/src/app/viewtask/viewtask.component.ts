import { Component, LOCALE_ID, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import {Router} from '@angular/router';
import { TaskService } from '../service/task.service';
import { ProjectService } from '../service/project.service';
import { MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-viewtask',
  templateUrl: './viewtask.component.html',
  styleUrls: ['./viewtask.component.css']
})

export class ViewtaskComponent implements OnInit {

  viewTaskForm: FormGroup
  submitted = false
  display = 'none'

  headElements = ["Task", "Parent", "Priority", "Start", "End", "", ""]
  projects: any
  tasks: any
  displayedColumns = ["project", "startDate", "endDate"]
  dataSource: MatTableDataSource<Object>;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  private _success = new Subject<string>();
  staticAlertClosed = false;
  successMessage: string;

  constructor(private formBuilder: FormBuilder, private router: Router, 
    private taskService: TaskService,
    private projectService: ProjectService) { 
    this.viewTaskForm = formBuilder.group(
      {
        project: new FormControl({value: '', disabled: true}, [Validators.required]),
        projectId: new FormControl('', [Validators.required])
      } 
    )
  }

  ngOnInit() {
    this.getAllProjects()
    this.dataSource = new MatTableDataSource (this.projects)

    setTimeout(() => this.staticAlertClosed = true, 20000);
    this._success.subscribe((message) => this.successMessage = message);
    this._success.pipe(
      debounceTime(5000)
    ).subscribe(() => this.successMessage = null);
  }

  getAllProjects() {
    this.projectService.getProjects().subscribe(
      data => {
        this.projects = data
    });
  }

  getTasksByProject (projectId: number) {
    this.taskService.getTasksByProject(projectId).subscribe(
      data => {
        this.tasks = data
      });
  }

  openProjectDialog(){
    this.getAllProjects()
    this.dataSource = new MatTableDataSource (this.projects)
    this.display='block';
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  closeModalDialog(){
    this.display='none';
  }

  selectProject (id: number, project: string) {
    this.viewTaskForm.controls['project'].setValue(project);
    this.viewTaskForm.controls['projectId'].setValue(id);
    this.getTasksByProject (id)
    this.closeModalDialog()
  }

  navigateToEditTask (task: any) {
    task ['project'] = this.viewTaskForm.controls['project'].value
    task ['projectId'] = this.viewTaskForm.controls['projectId'].value
    delete task ['status']
    task ['markParentTask'] = false
    this.taskService.editTaskData = task
    this.router.navigateByUrl("/task/add")
  }

  updateTaskStatus(taskId: number) {

    this.taskService.updateTaskStatus(taskId).subscribe(
      data => {
        this.getTasksByProject(this.viewTaskForm.controls['projectId'].value)
        this.changeSuccessMessage('Task status is mark as completed!')
      });
  }

  public changeSuccessMessage(message: string) {
    this._success.next(message);
  }

  sortTasks (e: any) {
    
    let arr = e.id.split('_')
    if (arr.length > 1 && arr[1] == 'desc') {
      e.id = arr[0] + "_" + 'asc'
    } else {
      e.id = arr[0] + "_" + 'desc'
    }
    let direction = (arr.length > 1 && arr[1] == 'desc') ? 1 : -1;
    let sortedTasks = this.tasks

    sortedTasks.sort(function(a, b){
      var aa: any
      var bb: any
      if (!isNaN(a[arr[0]]) && !isNaN(b[arr[0]])){
        aa = parseInt(a[arr[0]])
        bb = parseInt(b[arr[0]])
      } else if (arr[0] == 'startDate' || arr[0] == 'endDate') {
        aa = new Date(a[arr[0]])
        bb = new Date(b[arr[0]])
      } else {
        aa = a
        bb = b
      }

      if (aa < bb){
        return -1 * direction;
      }
      else if (aa > bb){
        return 1 * direction;
      }
      else {
        return 0;
      }
    });
    this.tasks = sortedTasks;
  }
}
