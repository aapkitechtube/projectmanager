import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class TaskService {

  editTaskData: any

  constructor(private http: HttpClient) { }

  // Http Options
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  getAllTasks(): Observable<Object> {
    return this.http.get(environment.apiURL + "/tasks")
    .pipe(
      catchError(this.handleError('getAllTasks'))
    );
  }

  getAllParentTasks(): Observable<Object> {
    return this.http.get(environment.apiURL + "/parenttasks")
    .pipe(
      catchError(this.handleError('getAllParentTasks'))
    );
  }

  getTaskById(taskId: number): Observable<Object> {
    return this.http.get(environment.apiURL + "/tasks/" + taskId)
    .pipe(
      catchError(this.handleError('getTaskById'))
    );
  }

  getTasksByProject(projectId: number): Observable<Object> {
    return this.http.get(environment.apiURL + "/tasksbyproject/" + projectId)
    .pipe(
      catchError(this.handleError('getTasksByProject'))
    );
  }

  createTask(task: any, userId: number): Observable<Object> {
    return this.http.post<Object>(environment.apiURL + "/tasks/" + userId, task, this.httpOptions)
      .pipe(
        catchError(this.handleError('createTask'))
      );
  }

  updateTask(task: any, taskId: number, userId: number): Observable<Object> {
    return this.http.put<Object>(environment.apiURL + "/tasks/" + taskId + "/" + userId, task, this.httpOptions)
      .pipe(
        catchError(this.handleError('updateTask'))
      );
  }

  createParentTask(parentTask: any): Observable<Object> {
    return this.http.post<Object>(environment.apiURL + "/parenttasks", parentTask, this.httpOptions)
      .pipe(
        catchError(this.handleError('createParentTask'))
      );
  }

  updateTaskStatus(taskId: number): Observable<Object> {
    return this.http.put<Object>(environment.apiURL + "/tasks/status/" + taskId, null, this.httpOptions)
    .pipe(
      catchError(this.handleError('updateTaskStatus'))
    );
  }

  deleteTask(id: number) {
    return this.http.delete<Object>(environment.apiURL + "/projects/" + id, this.httpOptions)
      .pipe(
        catchError(this.handleError('deleteProject'))
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
