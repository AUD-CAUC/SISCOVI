import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {Contrato} from '../contratos/contrato';

@Injectable()
export class VigenciaService {
  http: Http;
  config: ConfigService;
  constructor(http: Http, config: ConfigService) {
    this.http = http;
    this.config = config;
  }
  getVigenciasDosContratos(contratos: Contrato[]) {
    const url = this.config.myApi + '/vigencia/getVigenciaContratos';
    const data = contratos;
    return this.http.post(url, data).map(res => res.json());
  }
}
