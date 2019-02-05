import { Injectable } from '@angular/core';
import { ConfigService } from '../_shared/config.service';
import {Http, Headers, RequestOptions} from '@angular/http';
import {Rubrica} from './rubrica';
import {CadastroRubrica} from './cadastrar-rubrica/cadastro-rubrica';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class RubricasService {
  private headers: Headers;
  disabled = true;
  validity = true;
  nome: string;
  sigla: string;
  descricao: string;
  constructor(private config: ConfigService, private http: Http) {
    this.headers = new Headers(
      {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    );
  }
  getValidity() {
      return this.validity;
  }
  setValdity(value: boolean) {
      this.validity = value;
  }
  getAllrubricas() {
    const url = this.config.myApi + '/rubricas/getAll';
    return this.http.get(url).map(res => res.json());
  }
  getPercentuaisEstaticos() {
    const url = this.config.myApi + '/rubricas/getStaticPercent';
    return this.http.get(url).map(res => res.json());
  }
  cadastrarRubrica() {
      const cadastroRubrica = new CadastroRubrica();
      cadastroRubrica.rubrica = new Rubrica();
      cadastroRubrica.rubrica.nome = this.nome;
      cadastroRubrica.rubrica.sigla = this.sigla;
      cadastroRubrica.rubrica.descricao = this.descricao;
      cadastroRubrica.currentUser = this.config.user.username;
      const url = this.config.myApi + '/rubricas/criarRubrica';
      const data = cadastroRubrica;
      const headers = new Headers({'Content-type': 'application/json'});
      const options = new RequestOptions({headers: headers});
      return this.http.post(url, data, options).map(res => res.json());
  }
  buscarRubrica(codigo: number): Observable<Rubrica> {
      const url = this.config.myApi + '/rubricas/getRubrica/' + codigo;
      return this.http.get(url).map(res => res.json());
  }
  apagarRubrica(codigo: number) {
      const url = this.config.myApi + '/rubricas/deleteRubrica/' + codigo;
      return this.http.delete(url).map(res => res.json());
  }
  salvarAlteracao(rubrica: Rubrica) {
      const url = this.config.myApi + '/rubricas/alterarRubrica';
      const cadastroRubrica = new CadastroRubrica();
      cadastroRubrica.rubrica = new Rubrica();
      cadastroRubrica.rubrica = rubrica;
      cadastroRubrica.currentUser = this.config.user.username;
      const data = cadastroRubrica;
      return this.http.put(url, data).map(res => res.json());
  }
}
