import { Component, OnInit, LOCALE_ID, Inject, ViewChild, Input } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { ProjectService } from '../service/project.service';
import { UserService } from '../service/user.service';
import { MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { formatDate } from '@angular/common';
import { DateValidators } from '../app.component';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})

export class ProjectComponent implements OnInit {

  projectForm: FormGroup
  submitted = false
  isDateSelected = false
  display = 'none'
  allprojects: any
  project: any
  projectBtnLabel = 'Add'
  filteredProjects: any
  allUsers: any
  user: any
  displayedColumns = ["employeeId", "firstName"]
  dataSource: MatTableDataSource<Object>;
  private _success = new Subject<string>();
  staticAlertClosed = false;
  successMessage: string;
  currentDate = new Date();
  nextDay = new Date();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private formBuilder: FormBuilder, 
    private projectService: ProjectService,
    private userService: UserService) { 

    this.projectForm = formBuilder.group(
      {
        projectId: new FormControl(''),
        project: new FormControl('', [Validators.required]),
        startDate: new FormControl(''),
        endDate: new FormControl(''),
        priority: new FormControl('0'),
        selectDate: new FormControl(''),
        managerId: new FormControl(''),
        manager: new FormControl('', [Validators.required])
      },
      { validator: Validators.compose([
          DateValidators.dateLessThan('startDate', 'endDate', { 'loaddate': true })
        ])
      }
    )
  }

  ngOnInit() {
    this.getAllProjects ()
    this.getAllUsers ()
    this.dataSource = new MatTableDataSource (this.allUsers)
    this.projectForm.controls['priority'].setValue(0);

    setTimeout(() => this.staticAlertClosed = true, 20000);
    this._success.subscribe((message) => this.successMessage = message);
    this._success.pipe(
      debounceTime(5000)
    ).subscribe(() => this.successMessage = null);

    this.projectForm.get('selectDate').valueChanges.subscribe(
      (selectDate: string) => {
          if (selectDate === 'checked') {
            this.projectForm.controls['startDate'].setValidators([Validators.required])
            this.projectForm.controls['endDate'].setValidators([Validators.required])
          }
        }
    )
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  setDate (e: any) {
    if (e.target.checked) {
      this.projectForm.get("startDate").enable();
      this.projectForm.get("endDate").enable();
      this.projectForm.controls['startDate'].setValue(formatDate(this.currentDate, 'yyyy-MM-dd', 'en'));
      this.projectForm.controls['endDate'].setValue(formatDate(this.nextDay.setDate(this.currentDate.getDate() + 1), 'yyyy-MM-dd', 'en'));
    } else {
      this.projectForm.get("startDate").disable();
      this.projectForm.get("endDate").disable();
      this.projectForm.controls['startDate'].setValue('')
      this.projectForm.controls['endDate'].setValue('')
    }
  }

  resetForm () {
    this.projectBtnLabel = 'Add'
    this.projectForm.reset()
    this.projectForm.get("startDate").disable();
    this.projectForm.get("endDate").disable();
    this.projectForm.patchValue({
      priority: 0
    });
  }

  addProject () {
    this.submitted = true
    if (this.projectForm.invalid) {
      return;
    }

    if (this.projectBtnLabel == 'Add') {
      this.projectService.createProject(this.projectForm.value, 
        this.projectForm.controls['managerId'].value).subscribe(
          project =>
          this.changeSuccessMessage('Project created sucessfully!')
      );
    } else {
      this.projectService.updateProject(this.projectForm.value, 
        this.projectForm.controls['projectId'].value, 
        this.projectForm.controls['managerId'].value).subscribe(
          project =>
          this.changeSuccessMessage('Project updated sucessfully!')
      );
    }
  }

  openSearchModalDialog(){
    this.getAllUsers();
    this.dataSource = new MatTableDataSource (this.allUsers)
    this.display='block';
  }

  closeModalDialog(){
    this.display='none';
  }

  getAllProjects() {
    this.projectService.getAllProjects().subscribe(
      data => {
        this.allprojects = data
    });
  }

  getAllUsers() {
    this.userService.getAllUsers().subscribe(
      data => {
        this.allUsers = data
    });
  }

  getUserById(projectId: number) {
    this.userService.getUserById(projectId).subscribe(
      data => {
        this.user = data
    });
  }

  selectUser (id: number, firstName: string, lastName: string) {
    this.projectForm.controls['managerId'].setValue(id);
    this.projectForm.controls['manager'].setValue(firstName + ' ' + lastName);
    this.closeModalDialog()
  }

  public changeSuccessMessage(message: string) {
    this._success.next(message);
    this.getAllProjects ()
  }

  searchProject(searchText: string) {
    if(!searchText) {
      this.getAllProjects ()
    } else {
      this.filteredProjects = this.allprojects.filter(x =>
        x.project.trim().toLowerCase()
          .includes(searchText.trim().toLowerCase()),
      );
    }
    this.allprojects = this.filteredProjects
  }

  sortProject (e: any) {
    
    let arr = e.id.split('_')
    if (arr.length > 1 && arr[1] == 'desc') {
      e.id = arr[0] + "_" + 'asc'
    } else {
      e.id = arr[0] + "_" + 'desc'
    }
    let direction = (arr.length > 1 && arr[1] == 'desc') ? 1 : -1;
    let sortedProjects = this.allprojects

    sortedProjects.sort(function(a, b){
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
    this.allprojects = sortedProjects;
  }

  updateProject(project: Object) {
    this.project = JSON.parse(JSON.stringify(project))
    delete this.project['taskCompleted']
    delete this.project['totalTasks']
    this.project['selectDate'] = true
    this.projectBtnLabel = "Update"

    this.userService.getUserById(this.project['projectId']).subscribe(
      data => {
        this.user = data
        if (this.user['id'] > 0) {
          this.project['manager'] = this.user['firstName'] + ' ' + this.user['lastName']
          this.project['managerId'] = this.user['id']
        } else {
          this.project['manager'] = ''
          this.project['managerId'] = ''
        }
        this.projectForm.get("startDate").enable();
        this.projectForm.get("endDate").enable();
        this.projectForm.setValue(this.project)
    });   
  }

  deleteProject(id: number) {
    this.projectService.deleteProject(id).subscribe(
      project =>
      this.changeSuccessMessage('Project suspended successfully')
    );
    this.getAllProjects()
  }

  suspendProject (projectId: number) {
    this.deleteProject(projectId)
    this.getAllProjects()
    this.getAllUsers()
    this.changeSuccessMessage("Project suspeced sucessfully!")
  }

}