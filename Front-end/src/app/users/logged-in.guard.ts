import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { UserService } from './user.service';

@Injectable()
export class LoggedInGuard implements CanActivate {
  constructor(private userService: UserService,
              private router: Router) {
  }

  canActivate() {
    const loggedIn: boolean = this.userService.isLoggedIn();
    if (!loggedIn) {
      this.router.navigate(['login']);
    }
    return loggedIn;
  }
}
