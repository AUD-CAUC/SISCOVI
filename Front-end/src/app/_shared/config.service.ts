import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {User} from '../users/user';

@Injectable()
export class ConfigService {
  title = 'SISCOVI';
  subtitle = 'Sistema Conta Vinculada';
  user: User;
  headers: Headers = new Headers();
  public myApi = 'http://10.20.0.114:8080/SISCOVI/rest';
  public mask = {
    processo_adm: [/\d/, /\d/, /\d/, /\d/, /\d/, /\d/, '/', /[2]/, /[0]/, /\d/, /\d/],
    os: [/\d/, /\d/, /\d/, '/', /[2]/, /[0]/, /\d/, /\d/]
  };
  constructor() {
    this.setHeaders();
    if (window.location.hostname === 'localhost') {
      this.myApi = 'http://localhost:8080/SISCOVI/rest';
    }
    const user  = localStorage.getItem('auth_user');
    if (!user) {
      localStorage.clear();
    }
  }
  public setHeaders () {
    const authToken = localStorage.getItem('auth_token');
    if (authToken) {
      this.headers = new Headers();
      this.headers.append('Auth', `${authToken}`);
      this.headers.append('Accept', 'application/json');
      this.headers.append('Origin', 'http://localhost:8080' );
    }
  }
}
