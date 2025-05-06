import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';

@Component({
  selector: 'app-activate-account',
  standalone: false,
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {


   message:string = '';
   isOkay:boolean = true;
   submitted: boolean = false;
   constructor(
       private router: Router,
       private authService: AuthenticationService
   ){

   }

   onCodeCompleted(toekn: string) {
      this.confirmAccount(toekn)
    }
  confirmAccount(token: string) {
    this.authService.confirm(
      {
        token
      }
    ).subscribe(
      {
        next: ()=>{
          this.message="Your account has been successfuly activated.";
          this.submitted=true;
        },
        error: ()=>{

        }
      }
    )
  }
    redirectToLogin() {
      this.router.navigate(["login"])
      }
}
