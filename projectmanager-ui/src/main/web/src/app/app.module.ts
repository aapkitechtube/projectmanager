import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { AppComponent, BootstrapValidationCssDirective } from './app.component';
import { UserComponent } from './user/user.component';
import { ProjectComponent } from './project/project.component';
import { TaskComponent } from './task/task.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ViewtaskComponent } from './viewtask/viewtask.component';
import { MatFormFieldModule, MatTableModule, MatSortModule, MatSelectModule, MatInputModule, MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, MatProgressSpinnerModule, MatCardModule, MatPaginatorModule } from '@angular/material';
import { NgbAlertModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [
    AppComponent,
    BootstrapValidationCssDirective,
    UserComponent,
    ProjectComponent,
    TaskComponent,
    ViewtaskComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatDialogModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule,
    MatPaginatorModule,
    HttpClientModule,
    NgbAlertModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})

export class AppModule {
}
