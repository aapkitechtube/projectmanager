import { Component } from '@angular/core';
import {Directive, HostBinding, Self} from '@angular/core';
import {NgControl, AbstractControl, ValidatorFn} from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Project Manager';
}

@Directive({
    selector: '[formControlName],[ngModel],[formControl]',
})
export class BootstrapValidationCssDirective {
    constructor(@Self() private cd: NgControl) {}

    @HostBinding('class.is-invalid')
    get isInvalid(): boolean {
        const control = this.cd.control;
        return control ? control.invalid && control.touched : false;
    }
}

export class DateValidators {
  static dateLessThan(dateField1: string, dateField2: string, validatorField: { 
    [key: string]: boolean }): ValidatorFn {
      return (c: AbstractControl): { [key: string]: boolean } | null => {
          const date1 = c.get(dateField1).value;
          const date2 = c.get(dateField2).value;
          if ((date1 !== null && date2 !== null) && date1 > date2) {
              return validatorField;
          }
          return null;
      };
  }
}