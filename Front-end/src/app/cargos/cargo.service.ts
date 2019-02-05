import {Injectable} from '@angular/core';
import {Http, Headers, RequestOptions} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {Contrato} from '../contratos/contrato';
import {Cargo} from './cargo';
import {CargosFuncionarios} from './cargos-dos-funcionarios/cargos.funcionarios';

@Injectable()
export class CargoService {
  http: Http;
  private _enabled = false;
  config: ConfigService;
  headers: Headers;
  loading = false;
  displayCargos = false;
  cargos: Cargo[];
  constructor(http: Http, config: ConfigService) {
    this.http = http;
    this.config = config;
    this.headers = new Headers(
      {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    );
  }
  getAllCargos() {
    const url = this.config.myApi + '/cargo/getAllCargos';
    return this.http.get(url, {headers: this.headers}).map(res => res.json());
  }
  getCargosDosContratos(contrato: Contrato[]) {
    const url = this.config.myApi + '/cargo/getCargosDosContratos';
    const data = contrato;
    return this.http.post(url, data).map(res => res.json());
  }
  getCargosFuncionarios (codigo: number) {
    const url = this.config.myApi + '/funcionarios/getFuncioECargos=' + codigo;
    return this.http.get(url).map(res => res.json());
  }
  get enabled (): boolean {
      return this._enabled;
  }
  set enabled (enabled: boolean) {
      this._enabled = enabled;
  }
  cadastrarCargos() {
      const headers = new Headers({'Content-type': 'application/json'});
      const options = new RequestOptions({headers: headers});
      const data = new ListaCargos();
      data.cargos = this.cargos;
      data.currentUser = this.config.user.username;
      const url = this.config.myApi + '/cargo/cadastrarCargos';
      return this.http.post(url, data, options).map(res => res.json());
  }

    getFuncoesContrato(codigo: number) {
      const url = this.config.myApi + '/cargo/getFuncoesContrato/' + codigo;
      const data = this.config.user;
        return this.http.post(url, data).map(res => res.json());
    }

    getTerceirizadosFuncao(codigoContrato: number) {
      const url = this.config.myApi + '/cargo/getTerceirizadosFuncao/' + codigoContrato;
      const data = this.config.user;
      return this.http.post(url, data).map(res => res.json());
    }

    alocarFuncao(alocarTerceririzados: CargosFuncionarios[], codigo: number) {
      const url = this.config.myApi + '/cargo/alocarTerceirizadosContrato/' + codigo + '/' + this.config.user.username;
      const data = [];
        alocarTerceririzados.forEach(item => {
            const a = item.dataDisponibilizacao.toISOString().split('T');
            const alocarTerceirizado = {
                funcionario: item.funcionario,
                funcao: item.funcao,
                dataDisponibilizacao: a[0]
            };
            data.push(alocarTerceirizado);
        });
      return this.http.post(url, data).map(res => res.json());
    }

    alterarFuncaoTerceirizado(confirmarAlteracao: CargosFuncionarios[], codigoContrato: number) {
      const url = this.config.myApi + '/cargo/alterarFuncaoTerceirizado/' + codigoContrato + '/' + this.config.user.username;
      const data = [];
      for (let i = 0; i < confirmarAlteracao.length; i++) {
          const date = confirmarAlteracao[i].dataDisponibilizacao.toISOString().split('T');
          const info = {
              funcionario: confirmarAlteracao[i].funcionario,
              dataDisponibilizacao: date[0],
              funcao: confirmarAlteracao[i].funcao
          };
          data.push(info);
      }
      return this.http.post(url, data).map(res => res.json());

    }
}
export class ListaCargos {
    currentUser: string;
    cargos: Cargo[];
}
