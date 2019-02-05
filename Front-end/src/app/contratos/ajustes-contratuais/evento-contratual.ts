import {TipoEventoContratual} from './tipo-evento-contratual';

export class EventoContratual {

    codigo: number;
    tipo: TipoEventoContratual;
    prorrogacao: string;
    assunto: string;
    dataInicioVigencia: Date;
    dataFimVigencia: Date;
    dataAssinatura: Date;
    loginAtualizacao: string;
    dataAtualizacao: Date;
}
