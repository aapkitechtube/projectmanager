import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private http: HttpClient) { }

  // Http Options
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  getAllProjects(): Observable<Object> {
    return this.http.get(environment.apiURL + "/projects/tasks")
    .pipe(
      catchError(this.handleError('createProject'))
    );
  }

  getProjects(): Observable<Object> {
    return this.http.get(environment.apiURL + "/projects/")
    .pipe(
      catchError(this.handleError('getProjects'))
    );
  }

  createProject(project: any, userId: number): Observable<Object> {
    return this.http.post<Object>(environment.apiURL + "/projects/" + userId, project, this.httpOptions)
      .pipe(
        catchError(this.handleError('createProject'))
      );
  }

  updateProject(project: any, projectId: number, userId: number): Observable<Object> {
    return this.http.put<Object>(environment.apiURL + "/projects/" + projectId + "/" + userId, project, this.httpOptions)
    .pipe(
      catchError(this.handleError('updateProject'))
    );
  }

  deleteProject(id: number) {
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
