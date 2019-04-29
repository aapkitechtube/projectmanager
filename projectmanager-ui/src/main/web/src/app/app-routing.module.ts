import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProjectComponent } from './project/project.component'
import { UserComponent } from './user/user.component'
import { TaskComponent } from './task/task.component'
import { ViewtaskComponent } from './viewtask/viewtask.component'

const routes: Routes = [
  {
    path:'project/add',
    component: ProjectComponent
  },
  {
    path:'project/edit/:projectId',
    component: ProjectComponent
  },
  {
    path:'user/add',
    component: UserComponent
  },
  {
    path:'user/edit/:userId',
    component: UserComponent
  },
  {
    path:'task/add',
    component: TaskComponent
  },
  {
    path:'task/edit/:userId',
    component: TaskComponent
  },
  {
    path:'viewtasks',
    component: ViewtaskComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
