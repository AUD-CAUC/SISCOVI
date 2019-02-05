import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {Contrato} from '../contratos/contrato';
import {Funcionario} from './funcionario';

@Injectable()
export class FuncionariosService {
    http: Http;
    config: ConfigService;
    constructor(http: Http, config: ConfigService) {
    this.http = http;
    this.config = config;
    }
    getFuncionarios(contratos: Contrato[]) {
    const url = this.config.myApi + '/funcionarios/getFuncionariosContratos';
    const data = contratos;
    return this.http.post(url, data).map(res => res.json());
    }

    getFuncionariosDeUmContrato(codigoContrato: number) {
    const url = this.config.myApi + '/funcionarios/getFuncionariosContrato=' + codigoContrato;
    return this.http.get(url).map(res => res.json());
    }

    cadastraTerceirizado(funcionario: Funcionario) {
        const url = this.config.myApi + '/funcionarios/cadastrarTerceirizado';
        funcionario.loginAtualizacao = this.config.user.username;
        return this.http.post(url, funcionario).map(res => res.json());
    }

    cadastraTerceirizados(listaTerceirizados: Funcionario[]) {
        const url = this.config.myApi + '/funcionarios/cadastrarTerceirizados';
        for (const terceirizado of listaTerceirizados){
            terceirizado.loginAtualizacao = this.config.user.username;
        }
        return this.http.post(url, listaTerceirizados).map(res => res.json());
    }
    getAllTerceirizados() {
        const url = this.config.myApi + '/funcionarios/getAllTerceirizados';
        return this.http.get(url).map(res => res.json());
    }

    getTerceirizado(id: number) {
        const url = this.config.myApi + '/funcionarios/getTerceirizado/' + id;
        return this.http.get(url).map(res => res.json());
    }

    updateTerceirizado(terceirizado: Funcionario) {
        terceirizado.loginAtualizacao = this.config.user.username;
        const url = this.config.myApi + '/funcionarios/updateTerceirizado';
        return this.http.put(url, terceirizado).map(res => res.json());
    }

    getTerceirizadosNaoAlocados() {
        const url = this.config.myApi + '/funcionarios/getTerceirizadosNaoAlocados';
        return this.http.get(url).map(res => res.json());
    }

  verificaTerceirizadoContrato(cpf: string, cod: number) {
    const url = this.config.myApi + '/funcionarios/verificaExistenciaTerceirizado/' + cpf + '/' + cod;
    return this.http.get(url).map(res => res.json());
  }
}
