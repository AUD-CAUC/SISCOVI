import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {HistoricoGestor} from './historico-gestor';

@Injectable()
export class HistoricoService {
    constructor(private http: Http, private config: ConfigService) {}

    getHistoricoGestores(codigo: number) {
        const url = this.config.myApi + '/historico/getHistoricoGestores=' + codigo;
        return this.http.get(url).map(res => res.json());
    }

    getPerfisGestao() {
        const url = this.config.myApi + '/historico/getPerfisGestao';
        return this.http.get(url).map(res => res.json());
    }

    cadastrarGestorContrato(historico: HistoricoGestor) {
        historico.loginAtualizacao = this.config.user.username;
        const url = this.config.myApi + '/historico/cadastrarGestorContrato';
        return this.http.post(url, historico).map(res => res.json());
    }

    getHistoricoGestor(value: number) {
        const url = this.config.myApi + '/historico/getHistoricoGestor/' + value;
        return this.http.get(url).map(res => res.json());
    }
}
