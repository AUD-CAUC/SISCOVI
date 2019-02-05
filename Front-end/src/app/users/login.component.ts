import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { ConfigService } from '../_shared/config.service';
import { UserService } from './user.service';

@Component(
  {
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  public username = '';
  public password = '';
  errorMsg = '';
  carregando = false;
  constructor(
    private userService: UserService,
    private config: ConfigService,
    private router: Router
  ) {
     this.userService.logout();
    }
    onSubmit(username, password) {
    this.errorMsg = '';
    this.carregando = true;
    this.userService.login(username, password)
      .subscribe((result) => {
      this.carregando = false;
      if (result.success) {
        this.router.navigate(['']);
      }else {
        this.errorMsg = result.data.msg;
        this.carregando = false;
      }
      },
          err => {
      const result = err.json();
      this.errorMsg = result.errorMsg;
      this.carregando = false;
    }
    );
  }
}
