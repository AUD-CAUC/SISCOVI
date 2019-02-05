import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/observable/throw';
// import 'rxjs/add/operator/do';

import { ConfigService } from '../_shared/config.service';

import { User } from './user';
// import { Area } from '../areas/area';

@Injectable()
export class UserService {
  private loggedIn = false;
  private headers: Headers;
  public user: User = null;
  constructor(
    private config: ConfigService,
    private http: Http
  ) {
    this.loggedIn = !!localStorage.getItem('auth_token');
  }
  getCurrent() {
    let user: User = null;
    if (this.loggedIn) {
      user = JSON.parse(localStorage.getItem('auth_user')) as User;
      this.user = user;
      this.config.user = user;
      // this.config.area = JSON.parse(localStorage.getItem('auth_area')) as Area;
      }
      return user;
  }
  login(username, password) {
    this.logout();
    this.headers = new Headers(
      {
        'Content-Type': 'application/json'
      }
      );
    const url: string = this.config.myApi + '/login/token';
    const data: any = JSON.stringify({username, password});
    return this.http
      .post(
        url, data, {headers: this.headers}
        )
      .map(res => res.json())
      .map((res) => {
      if (res.success) {
        localStorage.setItem('auth_token', res.data.token);
        this.config.user = res.data.user;
        // this.config.area = res.data.user.Area;
        // this.config.user.areas_pai = res.data.areas_pai;
        // this.config.user.areas_child = res.data.areas_child;
        localStorage.setItem('auth_user', JSON.stringify(this.config.user));
        // localStorage.setItem('auth_area', JSON.stringify(this.config.area));
        this.config.setHeaders();
        this.loggedIn = true;
      }
      return res;
    });
  }
  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    localStorage.removeItem('auth_area');
    localStorage.clear();
    this.config.user = null;
    // this.config.area = null;
    this.loggedIn = false;
  }
  private handleError(error: any) {
    let errMsg: string;
    errMsg = error.message ? error.message : error.toString();
    console.error(errMsg);
    return Promise.reject(errMsg);
  }
  getUsuarios() {
    const url = this.config.myApi + '/usuario/getUsuarios';
    if (this.config.user.perfil.sigla === 'ADMINISTRADOR') {
      return this.http.get(url, {headers: this.headers}).map(res => res.json());
    }else {
      return null;
    }
  }
  isLoggedIn() {
    // this.fakeLogin();
    const token = localStorage.getItem('auth_token');
    this.loggedIn = !!token;
    this.getCurrent();
    return this.loggedIn;
  }
  getGestores() {
      const url = this.config.myApi + '/usuario/getGestores';
      return this.http.get(url, {headers: this.headers}).map(res => res.json());
  }
}
