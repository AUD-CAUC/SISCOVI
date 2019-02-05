import {Injectable} from '@angular/core';
import {ConfigService} from '../_shared/config.service';
import {Http} from '@angular/http';

@Injectable()
export class ConvencaoService {
  private config: ConfigService;
  private http: Http;
  constructor (config: ConfigService, http: Http) {
    this.config = config;
    this.http = http;
  }
  getConvencoes(codigo: number) {
    const url = this.config.myApi + '/convencao/getConvencoesDoContrato=' + codigo;
    return this.http.get(url).map( res => res.json());
  }

    getAll() {
        const url = this.config.myApi + '/convencao/getAll';
        return this.http.get(url).map(res => res.json());
    }
}
