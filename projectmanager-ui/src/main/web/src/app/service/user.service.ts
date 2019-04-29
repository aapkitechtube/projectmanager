import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  // Http Options
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }  

  getAllUsers (): Observable<Object> {
    return this.http.get(environment.apiURL + "/users")
  }

  getUserById (projectId: number): Observable<Object> {
    return this.http.get(environment.apiURL + "/userbyproject/" + projectId)
      .pipe(
        catchError(this.handleError('getUserById'))
      );
  }

  getUserByProjectAndTaskId (projectId: number, taskId: number): Observable<Object> {
    return this.http.get(environment.apiURL + "/users/" + projectId + "/" + taskId)
      .pipe(
        catchError(this.handleError('getUserByProjectAndTaskId'))
      );
  }

  createUser(user: any): Observable<Object> {
    return this.http.post<Object>(environment.apiURL + "/users", user, this.httpOptions)
      .pipe(
        catchError(this.handleError('createUser'))
      );
  } 

  updateUser(user: any, id: any): Observable<Object> {
    return this.http.put<Object>(environment.apiURL + "/users/" + id, user, this.httpOptions)
      .pipe(
        catchError(this.handleError('updateUser'))
      );
  }

  deleteUser(id: number) {
    return this.http.delete<Object>(environment.apiURL + "/users/" + id, this.httpOptions)
      .pipe(
        catchError(this.handleError('deleteUser'))
      );
  }

  private handleError(operation: String) {
    return (err: any) => {
      let errMsg = `error in ${operation}()`;
      if (err instanceof HttpErrorResponse) {
      }
      return Observable.throw(errMsg);
    }
  }
}

