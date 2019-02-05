import {EventEmitter, Injectable, Output} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {Observable} from 'rxjs/Observable';
import {SaldoTotal} from './total/saldo-total';


@Injectable()
export class SaldoService {
  constructor(private http: Http, private config: ConfigService) {}
  protected encapsulaDatas(value: any): Date {
    const a = value.split('/');
    const dia = Number(a[0]);
    const mes = Number(a[1]) - 1;
    const ano = Number(a[2]);
    return new Date(ano, mes, dia);
  }
  getSaldoTotal(codigoContrato: number) {
    const url = this.config.myApi + '/saldo/getSaldoTotal/' + codigoContrato + '/' + this.config.user.id;
    return this.http.get(url).map(res => res.json());
  }
  getSaldoIndividual(codigoContrato: number) {
    const url = this.config.myApi + '/saldo/getSaldoIndividual/' + codigoContrato;
    return this.http.get(url).map(res => res.json());
  }
}
