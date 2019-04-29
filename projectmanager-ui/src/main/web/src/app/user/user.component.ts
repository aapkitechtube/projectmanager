import { Component, LOCALE_ID, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms'
import { UserService } from '../service/user.service';
import { MatTableDataSource, MatPaginator, MatSort } from '@angular/material';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  userForm: FormGroup
  submitted = false
  allUsers: any
  filterUsers: any
  user:Object
  userBtnLabel: string
  private _success = new Subject<string>();
  staticAlertClosed = false;
  successMessage: string;
  
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  
  constructor(private formBuilder: FormBuilder, private userService: UserService,
    @Inject(LOCALE_ID) protected localeId: string) { 
      this.userForm = formBuilder.group(
        {
          firstName: new FormControl('', [Validators.required]),
          lastName: new FormControl('', [Validators.required]),
          employeeId: new FormControl('', [Validators.required]),
          id: new FormControl('')
        } 
      )
  }

  ngOnInit() {
    this.userBtnLabel = "Add"
    this.getAllUsers ()
    
    setTimeout(() => this.staticAlertClosed = true, 20000);
    this._success.subscribe((message) => this.successMessage = message);
    this._success.pipe(
      debounceTime(5000)
    ).subscribe(() => this.successMessage = null);
  }

  resetForm () {
    this.userBtnLabel = "Add"
    this.userForm.reset()
  }

  addUser () {

    this.submitted = true
    if (this.userForm.invalid) {
      return;
    }

    if (this.userBtnLabel == 'Add') {
      this.userService.createUser(this.userForm.value).subscribe(
        project =>
        this.changeSuccessMessage('User created successfully')
      );
    }  else {
      this.userService.updateUser(this.userForm.value, this.userForm.controls['id'].value).subscribe(
        project =>
        this.changeSuccessMessage('User updated successfully')
      );
    }
  }

  deleteUser(id: number) {
    this.userService.deleteUser(id).subscribe(
      project =>
      this.changeSuccessMessage('User deleted successfully')
    );
    this.getAllUsers ()
  }
  
  getAllUsers() {
    this.userService.getAllUsers().subscribe(
      data => {
        this.allUsers = data
        console.log (data)
    });
  }

  public changeSuccessMessage(message: string) {
    this._success.next(message);
    this.getAllUsers ()
  }

  editUser(user: Object) {
    this.user =  JSON.parse(JSON.stringify(user))
    delete this.user['projectId']
    delete this.user['taskId']
    this.userBtnLabel = "Update"
    this.userForm.setValue(this.user)
  }

  searchUser(searchText: string) {
    if(!searchText) {
      this.getAllUsers ()
    } else {
      this.filterUsers = this.allUsers.filter(x =>
         (x.firstName + x.lastName + x.employeeId).trim().toLowerCase()
          .includes(searchText.trim().toLowerCase()),
      );
    }
    this.allUsers = this.filterUsers
  }

  sortUser (e) {
    
    let arr = e.id.split('_')
    if (arr.length > 1 && arr[1] == 'desc') {
      e.id = arr[0] + "_" + 'asc'
    } else {
      e.id = arr[0] + "_" + 'desc'
    }
    let direction = (arr.length > 1 && arr[1] == 'desc') ? 1 : -1;
    let sortedUsers = this.allUsers

    sortedUsers.sort(function(a, b){
        if (isNaN(a[arr[0]]) && isNaN(b[arr[0]])){
          if (a[arr[0]] < b[arr[0]]){
            return -1 * direction;
          }
          else if ( a[arr[0]] > b[arr[0]]){
            return 1 * direction;
          }
          else {
            return 0;
          }
        } else {
          if (parseInt(a[arr[0]]) < parseInt(b[arr[0]])){
            return -1 * direction;
          }
          else if ( parseInt(a[arr[0]]) > parseInt(b[arr[0]])){
            return 1 * direction;
          }
          else {
            return 0;
          }
        }
       
    });
    this.allUsers = sortedUsers;
  }
}
