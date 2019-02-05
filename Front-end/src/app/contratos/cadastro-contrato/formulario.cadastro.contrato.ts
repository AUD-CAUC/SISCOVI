import {Cargo} from '../../cargos/cargo';

export class FormularioCadastroContrato {
    nomeGestor: string;
    nomeEmpresa: string;
    cnpj: string;
    numeroContrato: number;
    ativo: string;
    inicioVigencia: string;
    nomePrimeiroSubstituto: string;
    nomeSegundoSubstituto: string;
    fimVigencia: string;
    objeto: string;
    assinatura: string;
    // diaConvencao: number;
    // mes: number;
    percentualFerias: number;
    percentualDecimoTerceiro: number;
    percentualIncidencia: number;
    cargos: Cargo[];
}
