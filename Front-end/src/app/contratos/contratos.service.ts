import {Injectable} from '@angular/core';
import {ConfigService} from '../_shared/config.service';
import {Http, Headers} from '@angular/http';
import {Contrato} from './contrato';
import {FormularioCadastroContrato} from './cadastro-contrato/formulario.cadastro.contrato';
import {Observable} from 'rxjs/Observable';
import {EventoContratual} from './ajustes-contratuais/evento-contratual';
import {TipoEventoContratual} from './ajustes-contratuais/tipo-evento-contratual';

@Injectable()
export class ContratosService {

    private headers: Headers;
    contratos: Contrato[] = [];
    formValido = false;
    formCadContr: FormularioCadastroContrato;

    constructor(private config: ConfigService, private  http: Http) {
    }

    getContratosDoUsuario() {
        const url = this.config.myApi + '/contrato/getContrato/usuario=' + this.config.user.username;
        return this.http.get(url).map(res => res.json());
    }

    cadastrarAjuste(contrato: Contrato) {
        const url = this.config.myApi + '/contrato/cadastrarAjuste/' + this.config.user.username;
        const historico = [];
        const percentuais = [];
        const funcoes = [];
        if (contrato.historicoGestao.length > 0) {
            contrato.historicoGestao.forEach(item => {
                const hist = {
                    gestor: item.gestor,
                    codigoPerfilGestao: item.codigoPerfilGestao,
                    inicio: this.convertDate(item.inicio),
                    fim: this.convertDate(item.fim)
                };
                historico.push(hist);
            });
        }
        if (contrato.percentuais.length > 0) {
            contrato.percentuais.forEach(item => {
                console.log(item.percentual);
                const perc = {
                    percentual: item.percentual,
                    dataInicio: this.convertDate(item.dataInicio),
                    dataAditamento: this.convertDate(item.dataAditamento),
                    rubrica: item.rubrica
                };
                percentuais.push(perc);
            });
        }
        if (contrato.funcoes.length > 0) {
            contrato.funcoes.forEach(funcao => {
                const func = {
                    codigo: funcao.codigo,
                    nome: funcao.nome,
                    descricao: funcao.descricao,
                    remuneracao: funcao.remuneracao,
                    adicionais: funcao.adicionais,
                    trienios: funcao.trienios,
                    convencao: {
                        codigo: funcao.convencao.codigo,
                        nome: funcao.convencao.nome,
                        dataBase: this.convertDate(funcao.convencao.dataBase),
                        descricao: funcao.convencao.descricao,
                        sigla: funcao.convencao.sigla
                    }
                };
                funcoes.push(func);
            });
        }
        const evento = {
            tipo: {
                cod: contrato.eventoContratual.tipo.cod,
                tipo: contrato.eventoContratual.tipo
            },
            prorrogacao: contrato.eventoContratual.prorrogacao,
            assunto: contrato.eventoContratual.assunto,
            dataInicioVigencia: this.convertDate(contrato.eventoContratual.dataInicioVigencia),
            dataFimVigencia: this.convertDate(contrato.eventoContratual.dataFimVigencia),
            dataAssinatura: this.convertDate(contrato.eventoContratual.dataAssinatura)
        };
        const data = {
            cnpj: contrato.cnpj,
            nomeDaEmpresa: contrato.nomeDaEmpresa,
            numeroDoContrato: contrato.numeroDoContrato,
            seAtivo: contrato.seAtivo,
            historicoGestao: historico,
            funcoes: funcoes,
            percentuais: percentuais,
            numeroProcessoSTJ: contrato.numeroProcessoSTJ,
            eventoContratual: evento
        };
       return  this.http.post(url, data).map(res => res.json());
    }

    getNomeDoGestor(codigo: number) {
        const url = this.config.myApi + '/contrato/getGestorContrato=' + codigo;
        return this.http.get(url).map(res => res.json());
    }

    cadastrarContrato(contrato: Contrato) {
        const url = this.config.myApi + '/contrato/cadastrarContrato/' + this.config.user.username;
        const historico = [];
        const percentuais = [];
        const funcoes = [];
        contrato.historicoGestao.forEach(item => {
            const hist = {
                gestor: item.gestor,
                codigoPerfilGestao: item.codigoPerfilGestao,
                inicio: this.convertDate(item.inicio),
                fim: this.convertDate(item.fim)
            };
            historico.push(hist);
        });
        contrato.percentuais.forEach(item => {
            console.log(item.percentual);
            const perc = {
                percentual: item.percentual,
                dataInicio: this.convertDate(item.dataInicio),
                dataAditamento: this.convertDate(item.dataAditamento),
                rubrica: item.rubrica
            };
            percentuais.push(perc);
        });
        contrato.funcoes.forEach(funcao => {
            const func = {
                codigo: funcao.codigo,
                nome: funcao.nome,
                descricao: funcao.descricao,
                remuneracao: funcao.remuneracao,
                adicionais: funcao.adicionais,
                trienios: funcao.trienios,
                convencao: {
                    codigo: funcao.convencao.codigo,
                    nome: funcao.convencao.nome,
                    dataBase: this.convertDate(funcao.convencao.dataBase),
                    descricao: funcao.convencao.descricao,
                    sigla: funcao.convencao.sigla
                }
            };
            funcoes.push(func);
        });
        const data = {
            cnpj: contrato.cnpj,
            dataInicio: this.convertDate(contrato.dataInicio),
            nomeDaEmpresa: contrato.nomeDaEmpresa,
            numeroDoContrato: contrato.numeroDoContrato,
            objeto: contrato.objeto,
            seAtivo: contrato.seAtivo,
            historicoGestao: historico,
            funcoes: funcoes,
            dataAssinatura: this.convertDate(contrato.dataAssinatura),
            percentuais: percentuais,
            numeroProcessoSTJ: contrato.numeroProcessoSTJ,
            dataFim: this.convertDate(contrato.dataFim)
        };
        return this.http.post(url, data).map(res => res.json());
    }

    private convertDate(date: Date) {
        const value = date.toISOString();
        const temp = value.split('T');
        return temp[0];
    }

    getEventosContratuais(value: number) {
        const url = this.config.myApi + '/contrato/getEventosContratuais/' + this.config.user.username + '/' + value;
        return this.http.get(url).map(res => res.json());
    }

    getContratoCompletoUsuario(value: number): Observable<Contrato> {
        const url = this.config.myApi + '/contrato/getContratoCompleto/' + this.config.user.username + '/' + value;
        return this.http.get(url).map(res => res.json() as Contrato);
    }

    getTiposEventosContratuais(): Observable<TipoEventoContratual[]> {
        const url = this.config.myApi + '/contrato/getTiposEventosContratuais';
        return this.http.get(url).map(res => res.json());
    }
}
