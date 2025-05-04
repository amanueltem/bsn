import { Component } from '@angular/core';
import { AutenticationRequest } from '../../services/models/autentication-request';
import { Route, Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { AuthenticationResponse } from '../../services/models';
@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  authRequest: AutenticationRequest={email:'',password:''};
errorMsg: Array<String> =[];
constructor(
  private router: Router,
  private authService: AuthenticationService
  //another service
){
}
register(): void {
 this.router.navigate(['register'])
}
login(): void {
    this.errorMsg=[];
    this.authService.authenticate(
      {
        body:this.authRequest
      }
    ).subscribe(
      {
        next:(res:AuthenticationResponse): void =>{
          //save token
          this.router.navigate(['books'])
        },
        error: (err):void=>{
          console.log(err);
          
        }
      }
    )
}

}
