import {Injectable} from '@angular/core';
import {Router, CanActivate} from '@angular/router';
import {ConfigService} from '../_shared/config.service';
@Injectable()
export class ProfileGuard implements CanActivate {
  constructor(private config: ConfigService, private router: Router) {}
  canActivate() {
    if (this.config.user.perfil.sigla === 'ADMINISTRADOR') {
      return true;
    }else {
      this.router.navigate(['home']);
    }
    return false;
  }
}
