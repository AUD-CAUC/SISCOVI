import {EventEmitter, Injectable, Output} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {Observable} from 'rxjs/Observable';
import {TerceirizadoDecimoTerceiro} from './terceirizado-decimo-terceiro';
import {DecimoTerceiroPendente} from './decimo-terceiro-pendente/decimo-terceiro-pendente';

@Injectable()
export class DecimoTerceiroService {
    constructor(private http: Http, private config: ConfigService) {}
    getFuncionariosDecimoTerceiro(codigoContrato: number, tipoRestituicao: string) {
        const url = this.config.myApi + '/decimo-terceiro/getTerceirizadosDecimoTerceiro=' + codigoContrato + '/' + tipoRestituicao;
        return this.http.get(url).map(res => res.json());
    }
    /*calculaFeriasTerceirizados(decimoTerceiro: DecimoTerceiroCalcular[]) {
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
                'pTotalIncidenciaTerco': ferias.pTotalIncidenciaTerco
            };
            data.push(val);
        });
        const headers = new Headers({'Content-type': 'application/json'});
        return this.http.post(url, data, headers).map(res => res.json());
    }*/
    calculaDecimoTerceiroTerceirizados(tercerizadosDecimoTerceiro: TerceirizadoDecimoTerceiro[]) {
        const url = this.config.myApi + '/decimo-terceiro/calcularDecimoTerceiroTerceirizados';
        const data = tercerizadosDecimoTerceiro;
        const headers = new Headers({'Content-type': 'application/json'});
        return this.http.post(url, data, headers).map(res => res.json());
    }
    protected encapsulaDatas(value: any): Date {
        const a = value.split('/');
        const dia = Number(a[0]);
        const mes = Number(a[1]) - 1;
        const ano = Number(a[2]);
        return new Date(ano, mes, dia);
    }
    registrarCalculoDecimoTerceiro(calculosDecimoTerceiro: TerceirizadoDecimoTerceiro[]) {
        const url = this.config.myApi + '/decimo-terceiro/registrarCalculoDecimoTerceiro';
        const data: any[] = [];
        calculosDecimoTerceiro.forEach(item => {
            const info = {
                codigoTerceirizadoContrato: item.codigoTerceirizadoContrato,
                nomeTerceirizado: item.nomeTerceirizado,
                inicioContagem: item.inicioContagem,
                valorDisponivel: item.valorDisponivel,
                tipoRestituicao: item.tipoRestituicao,
                valorMovimentado: item.valorMovimentado,
                parcelas: item.parcelas,
                valoresDecimoTerceiro: item.valoresDecimoTerceiro,
                nomeCargo: item.nomeCargo,
                fimContagem: item.fimContagem,
                id: this.config.user.username
            };
            data.push(info);
        });
        const headers = new Headers({'Content-type': 'application/json'});
        return this.http.post(url, data, headers).map(res => res.json());
    }

    getCalculosPendentes(codigoContrato: number) {
        const url = this.config.myApi + '/decimo-terceiro/getCalculosPendentes/' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }

    getCalculosPendentesNegados(codigoContrato: number) {
        const url = this.config.myApi + '/decimo-terceiro/getCalculosPendentesNegados/' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }

    salvarDecimoTerceiroAvaliados(codigoContrato: number, calculosAvaliados: DecimoTerceiroPendente[]) {
        const url = this.config.myApi + '/decimo-terceiro/avaliarCalculosPendentes';
        const object = {
           decimosTerceirosPendentes: calculosAvaliados,
           user: this.config.user,
           codigoContrato: codigoContrato

        };
        return this.http.put(url, object).map(res => res.json());
    }

    executarDecimoTerceiroAvaliados(codigoContrato: number, calculosAvaliados: DecimoTerceiroPendente[]) {
        const url = this.config.myApi + '/decimo-terceiro/executarCalculos';
        const object = {
            decimosTerceirosPendentes: calculosAvaliados,
            user: this.config.user,
            codigoContrato: codigoContrato

        };
        return this.http.put(url, object).map(res => res.json());
    }

    getCalculosPendentesExecucao(codigoContrato: number) {
        const url = this.config.myApi + '/decimo-terceiro/getCalculosPendentesExecucao/' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }

    getCalculosNaoPendentesNegados(codigoContrato: number) {
        const url = this.config.myApi + '/decimo-terceiro/getCalculosNaoPendentesNegados/' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }

    getRestituicoesDecimoTerceiro(codigoContrato: number) {
        const url = this.config.myApi + '/decimo-terceiro/getRestituicoes/' + codigoContrato + '/' + this.config.user.id;
        return this.http.get(url).map(res => res.json());
    }
}
