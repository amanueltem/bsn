import { Component } from '@angular/core';
import { RegistrationRequest } from '../../services/models';
import { AuthenticationService } from '../../services/services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

   registerRequest: RegistrationRequest={
    email:'',
    firstname:'',
    lastname:'',
    password:''
   }
   errorMsg: Array<string> =[];

   constructor(
    private router: Router,
    private authService: AuthenticationService
   ){

   }
   register() {
    this.errorMsg=[];
    this.authService.register(
      {
        body: this.registerRequest
      }
    ).subscribe(
      {
        next:(): void =>{
            this.router.navigate(['activate-account']);
        },
        error: (err): void =>{
        if(err.error.validationErrors){
          this.errorMsg=err.error.validationErrors;
          }
          else{
            this.errorMsg.push(err.error.businessErrorDescription);
          }
        }
      }
    )
}
login() {
 this.router.navigate(['login'])
}
}
