import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {FeriasCalcular} from './ferias-calcular';
import {Observable} from 'rxjs/Observable';
import {FeriasCalculosPendentes} from './ferias-pendentes/ferias-calculos-pendentes';
import {User} from '../users/user';


@Injectable()
export class FeriasService {

    constructor(private http: Http, private config: ConfigService) {}

    getFuncionariosFerias(codigoContrato: number, tipoRestituicao: string) {
        const url = this.config.myApi + '/ferias/getTerceirizadosFerias=' + codigoContrato + '/' + tipoRestituicao;
        return this.http.get(url).map(res => res.json());
    }

    calculaFeriasTerceirizados(feriasCalcular: FeriasCalcular[]) {
        const url = this.config.myApi + '/ferias/calcularFeriasTerceirizados';
        const data = [];
        feriasCalcular.forEach(ferias => {
            const inicioFerias = this.encapsulaDatas(ferias.getInicioFerias());
            const fimFerias = this.encapsulaDatas(ferias.getFimFerias());
            let tipoRestituicao = '';
            if (ferias.getTipoRestituicao() === 'MOVIMENTACAO') {
                tipoRestituicao = 'MOVIMENTAÇÃO';
            }else if (ferias.getTipoRestituicao() === 'RESGATE') {
                tipoRestituicao = ferias.getTipoRestituicao();
            }
            const val = {
                'codTerceirizadoContrato': ferias.getCodTerceirizadoContrato(),
                'tipoRestituicao': tipoRestituicao,
                'diasVendidos': ferias.getDiasVendidos(),
                'inicioFerias': inicioFerias.toISOString().split('T')[0],
                'fimFerias': fimFerias.toISOString().split('T')[0],
                'inicioPeriodoAquisitivo': ferias.getInicioPeriodoAquisitivo(),
                'fimPeriodoAquisitivo': ferias.getFimPeriodoAquisitivo(),
                'valorMovimentado': ferias.getValorMovimentado(),
                'parcelas': ferias.getParcelas(),
                'pTotalFerias': ferias.pTotalFerias,
                'pTotalTercoConstitucional': ferias.pTotalTercoConstitucional,
                'pTotalIncidenciaFerias': ferias.pTotalIncidenciaFerias,
                'pTotalIncidenciaTerco': ferias.pTotalIncidenciaTerco,
                'username': this.config.user.username
            };
            data.push(val);
        });
        const headers = new Headers({'Content-type': 'application/json'});
        return this.http.post(url, data, headers).map(res => res.json());
    }

    getValoresFeriasTerceirizado(feriasCalcular: FeriasCalcular) {
        const url = this.config.myApi + '/ferias/getValorRestituicaoFerias';
        const inicioFerias = this.encapsulaDatas(feriasCalcular.getInicioFerias());
        const fimFerias = this.encapsulaDatas(feriasCalcular.getFimFerias());
        const data = {
            'codTerceirizadoContrato': feriasCalcular.getCodTerceirizadoContrato(),
            'tipoRestituicao': feriasCalcular.getTipoRestituicao(),
            'diasVendidos': feriasCalcular.getDiasVendidos(),
            'inicioFerias': inicioFerias.toISOString().split('T')[0],
            'fimFerias': fimFerias.toISOString().split('T')[0],
            'inicioPeriodoAquisitivo': feriasCalcular.getInicioPeriodoAquisitivo(),
            'fimPeriodoAquisitivo': feriasCalcular.getFimPeriodoAquisitivo(),
            'valorMovimentado': feriasCalcular.getValorMovimentado(),
            'parcelas': feriasCalcular.getParcelas()
        };
        return this.http.post(url, data).map(res => res.json());
    }

    getCalculosPendentes(codigoContrato: number) {
        const url = this.config.myApi + '/ferias' + '/getCalculosPendentes=' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }

    salvarFeriasAvaliadas(codigoContrato: number, lista: FeriasCalculosPendentes[]) {
        const url = this.config.myApi + '/ferias/salvarFeriasAvaliadas';
        const object = {
            codContrato: codigoContrato,
            user: this.config.user,
            calculosAvaliados: lista
        };
        return this.http.put(url, object).map(res => res.json());
    }

    getCalculosPendentesNegados(codigoContrato: number) {
        const url = this.config.myApi + '/ferias' + '/getCalculosPendentesNegados=' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }

    getCalculosPendentesExecucao(codigoContrato: number) {
        const url = this.config.myApi + '/ferias' + '/getCalculosPendentesExecucao=' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }
    getCalculosNaoPendentesNegados(codigoContrato: number) {
        const url = this.config.myApi + '/ferias' + '/getCalculosNaoPendentesNegados=' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }
    getRetencoesFerias(codigoContrato: number) {
        const url = this.config.myApi + '/ferias/getRetencoesFerias/' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }
    salvarExecucaoFerias(codigoContrato: number, lista: FeriasCalculosPendentes[]) {
        const url = this.config.myApi + '/ferias/salvarExecucaoFerias';
        const object = {
            codContrato: codigoContrato,
            user: this.config.user,
            calculosAvaliados: lista
        };
        return this.http.put(url, object).map(res => res.json());
    }
    protected encapsulaDatas(value: any): Date {
            const a = value.split('/');
            const dia = Number(a[0]);
            const mes = Number(a[1]) - 1;
            const ano = Number(a[2]);
            return new Date(ano, mes, dia);
    }
    private handleError(error: any) {
        let errMsg: string;
        errMsg = error.message ? error.message : error.toString();
        return Observable.throw(error);
    }
}
