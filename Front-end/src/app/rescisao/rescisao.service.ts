import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {TerceirizadoRescisao} from './terceirizado-rescisao';
import {RescisaoPendente} from '../decimo_terceiro/decimo-terceiro-pendente/rescisao-pendente';

@Injectable()
export class RescisaoService {
    constructor(private http: Http, private config: ConfigService) {}
    getFuncionariosRescisao(codigoContrato: number, tipoRestituicao: string) {
        const url = this.config.myApi + '/rescisao/getTerceirizadosRescisao=' + codigoContrato + '/' + tipoRestituicao;
        return this.http.get(url).map(res => res.json());
    }
    calculaRescisaoTerceirizados(terceirizadosRescisao: TerceirizadoRescisao[]) {
        const url = this.config.myApi + '/rescisao/calculaTerceirizados';
        const data =  [];
        terceirizadosRescisao.forEach(item => {
            let tipoRestituicao = '';
            if (item.tipoRestituicao === 'MOVIMENTACAO') {
                tipoRestituicao = 'MOVIMENTAÇÃO';
            } else if (item.tipoRestituicao === 'RESGATE') {
              tipoRestituicao = item.tipoRestituicao;
            }
            const val = {
                'codTerceirizadoContrato': item.codTerceirizadoContrato,
                'nomeTerceirizado': item.nomeTerceirizado,
                'dataDesligamento': item.dataDesligamento,
                'tipoRescisao': item.tipoRescisao,
                'tipoRestituicao': tipoRestituicao
            };
            data.push(val);
        });
        const headers = new Headers({'Content-type': 'application/json'});
        return this.http.post(url, data, headers).map(res => res.json());
    }
    registrarCalculoRescisao(calculos: TerceirizadoRescisao[]) {
        const url = '';
        return this.http.get(url).map(res => res.json());
    }

    getCalculosPendentes(codigoContrato: number) {
        const url = '';
        return this.http.get(url).map(res => res.json());
    }

    getCalculosPendentesNegados(codigoContrato: number) {
        const url = '';
        return this.http.get(url).map(res => res.json());
    }

    salvarDecimoTerceiroAvaliados(codigoContrato: number, calculosAvaliados: RescisaoPendente[]) {
        const url = '';
        return this.http.get(url).map(res => res.json());
    }
}
