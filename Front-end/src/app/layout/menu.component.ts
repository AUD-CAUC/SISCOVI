import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { ConfigService } from '../_shared/config.service';
import { UserService } from '../users/user.service';

@Component(
{
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
}
)
export class MenuComponent {
  admin;
  constructor(
    private config: ConfigService,
    private userService: UserService,
    private router: Router
  ) {
    if (this.config.user.perfil.sigla === 'ADMINISTRADOR') {
      this.admin = true;
    }
  }
  onInit() {
      const is_menu = localStorage.getItem('is_menu');
    }
    logout() {
    // this.userService.logout();
    localStorage.removeItem('is_menu');
    this.router.navigate(['login']);
    }
    menuUser() {
      /*
		$(document).ready(function()
		{
			$("#modalUser").modal('open');
		});
		*/
    }
}
