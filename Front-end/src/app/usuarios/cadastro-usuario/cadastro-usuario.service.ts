import {Injectable} from '@angular/core';
import {Http, Headers, RequestOptions} from '@angular/http';
import {ConfigService} from '../../_shared/config.service';
import {CadastroUsuario} from './cadastro-usuario';
import {Usuario} from '../usuario';


@Injectable()
export class CadastroUsuarioService {
    validity = true;
    nome: string;
    login: string;
    sigla: string;
    password: string;
    http: Http;
    config: ConfigService;
    cadastroUsuario: CadastroUsuario;
    constructor(http: Http, config: ConfigService) {
        this.http = http;
        this.config = config;
        this.cadastroUsuario = new CadastroUsuario();
    }
    changeValidity() {
        this.validity = ((!this.validity && false) || (this.validity && true));
    }
    getValidity() {
        return this.validity;
    }
    setValidity(value: boolean) {
        this.validity = value;
    }
    cadastrarUsuario() {
        this.cadastroUsuario.usuario = new Usuario();
        this.cadastroUsuario.usuario.login = this.login;
        this.cadastroUsuario.usuario.nome = this.nome;
        this.cadastroUsuario.usuario.perfil = this.sigla;
        this.cadastroUsuario.password = this.password;
        this.cadastroUsuario.currentUser = this.config.user.username;
        const headers = new Headers({'Content-type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        const url = this.config.myApi + '/usuario/cadastrarUsuario';
        return this.http.post(url, this.cadastroUsuario, options).map(res => res.json());
    }
}
