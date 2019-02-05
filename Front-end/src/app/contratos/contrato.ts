import {HistoricoGestor} from '../historico/historico-gestor';
import {Cargo} from '../cargos/cargo';
import {Percentual} from '../percentuais/percentual';
import {EventoContratual} from './ajustes-contratuais/evento-contratual';

export class Contrato {
  public cnpj: string;
  public codigo: number;
  public dataInicio: Date;
  public nomeDaEmpresa: string;
  public numeroDoContrato: string;
  public anoDoContrato: number;
  public dataFim: Date;
  public objeto: string;
  public seAtivo: string;
  public historicoGestao: HistoricoGestor[];
  public funcoes: Cargo[];
  public dataAssinatura: Date;
  public percentuais: Percentual[];
  public numeroProcessoSTJ: string;
  public loginAtualizacao: string;
  public dataAtualizacao: Date;
  public eventoContratual: EventoContratual;

  constructor(nomeDaEmpresa: string, cnpj: string, codigo: number, numeroDoContrato: string, anoDoContrato: number, dataInicio: Date, dataFim: Date, objeto: string,
              seAtivo: string, historicoGestao: HistoricoGestor[], funcoes: Cargo[]) {
    this.nomeDaEmpresa = nomeDaEmpresa;
    this.cnpj = cnpj;
    this.codigo = codigo;
    this.numeroDoContrato = numeroDoContrato;
    this.anoDoContrato = anoDoContrato;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.objeto = objeto;
    this.seAtivo = seAtivo;
    this.historicoGestao = historicoGestao;
    this.funcoes = funcoes;
  }
}
