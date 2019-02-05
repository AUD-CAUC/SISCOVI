import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ConfigService} from '../_shared/config.service';
import {TotalMensalPendente} from './total-mensal-pendente';

@Injectable()
export class TotalMensalService {
    private http: Http;
    private configService: ConfigService;
    constructor(http: Http, config: ConfigService) {
        this.configService = config;
       this.http = http;
    }
    public getValoresRetidos(codigoContrato: number, codigoUsuario: number) {
        const url = this.configService.myApi + '/total-mensal-a-reter/getValoresRetidos/' + codigoContrato + '/' + codigoUsuario;
        return this.http.get(url).map(res => res.json());
    }
    public calcularTotalMensal(codigoContrato: number, mes: number, ano: number) {
        const url = this.configService.myApi + '/total-mensal-a-reter/calculaTotalMensal=' + this.configService.user.id + '/codigo=' + codigoContrato + '/mes=' + mes + '/ano=' + ano;
        return this.http.get(url).map(res => res.json());
    }
    public getTotaisPendentes(codigoContrato: number) {
        const url = this.configService.myApi + '/total-mensal-a-reter/getValoresPendentes/' + codigoContrato + '/' + this.configService.user.id;
        return this.http.get(url).map(res => res.json());
    }
    public getTotaisPendentesExecucao(codigoContrato: number) {
        const url = this.configService.myApi + '/total-mensal-a-reter/getValoresPendentesExecucao/' + codigoContrato + '/' + this.configService.user.id;
        return this.http.get(url).map(res => res.json());
    }
    public enviarAvaliacaoCalculosTotalMensal(codigoContrato: number, totaisMensaisAvaliados: TotalMensalPendente[]) {
        const val = [];
        totaisMensaisAvaliados.forEach(item => {
            const valor = [];
            let object2 = {};
            item.totaisMensais.totais.forEach(total => {
                object2 = {
                    funcao: total.funcao,
                    ferias: total.ferias,
                    tercoConstitucional: total.tercoConstitucional,
                    decimoTerceiro: total.decimoTerceiro,
                    incidencia: total.incidencia,
                    multaFGTS: total.multaFGTS,
                    total: total.total,
                    status: total.status
                };
                valor.push(object2);
            });
            const totaiMensais = {
                dataReferencia: item.totaisMensais.dataReferencia,
                totais: valor
            };
            const object1 = {
                observacoes: JSON.parse(JSON.stringify(item.observacoes)),
                status: JSON.parse(JSON.stringify(item.status)),
                totaisMensais: JSON.parse(JSON.stringify(totaiMensais))
            };
            val.push(object1);
        });
        const object = {
            totalMensalPendenteModels: val,
            codigoContrato: codigoContrato,
            user: this.configService.user
        };
        const url = this.configService.myApi + '/total-mensal-a-reter/enviarAvaliacaoCalculosTotalMensal';
        return this.http.post(url, object).map(res => res.json());
    }
    public enviarExecucaoCalculosTotalMensal(codigoContrato: number, totaisMensaisAvaliados: TotalMensalPendente[]) {
        const val = [];
        totaisMensaisAvaliados.forEach(item => {
            const valor = [];
            let object2 = {};
            item.totaisMensais.totais.forEach(total => {
                object2 = {
                    funcao: total.funcao,
                    ferias: total.ferias,
                    tercoConstitucional: total.tercoConstitucional,
                    decimoTerceiro: total.decimoTerceiro,
                    incidencia: total.incidencia,
                    multaFGTS: total.multaFGTS,
                    total: total.total,
                    status: total.status
                };
                valor.push(object2);
            });
            const totaiMensais = {
                dataReferencia: item.totaisMensais.dataReferencia,
                totais: valor
            };
            const object1 = {
                observacoes: JSON.parse(JSON.stringify(item.observacoes)),
                status: JSON.parse(JSON.stringify(item.status)),
                totaisMensais: JSON.parse(JSON.stringify(totaiMensais))
            };
            val.push(object1);
        });
        const object = {
            totalMensalPendenteModels: val,
            codigoContrato: codigoContrato,
            user: this.configService.user
        };
        const url = this.configService.myApi + '/total-mensal-a-reter/enviarAvaliacaoCalculosExecutadosTotalMensal';
        return this.http.post(url, object).map(res => res.json());
    }

    getMesesCalculoValidos(ano: number, codigoContrato: number) {
        const url = this.configService.myApi + '/total-mensal-a-reter/getMesesCalculo/' + ano + '/' + codigoContrato;
        return this.http.get(url).map(res => res.json());
    }
}
