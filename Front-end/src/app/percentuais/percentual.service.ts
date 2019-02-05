import {Injectable} from '@angular/core';
import {Http, Headers} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {Contrato} from '../contratos/contrato';

@Injectable()
export class PercentualService {
  headers: Headers;
  http: Http;
  config: ConfigService;
  disabled = true;
  constructor(http: Http, config: ConfigService) {
    this.headers = config.headers;
    this.http = http;
    this.config = config;
  }
  getPercentuaisDosContratos(contratos: Contrato[]) {
    const url = this.config.myApi + '/percentual/percentualDosContratos';
    const data = contratos;
    return this.http.post(url, data).map(res => res.json());
  }

    getPercentuaisFerias() {
      const url = this.config.myApi + '/percentual/getPercentuaisFerias';
      return this.http.get(url).map(res => res.json());
    }

    getPercentuaisDecimoTerceiro() {
        const url = this.config.myApi + '/percentual/getPercentuaisDecimoTerceiro';
        return this.http.get(url).map(res => res.json());
    }
}
