import {ListaTotalMensalData} from './lista-total-mensal-data';

export class TotalMensalPendente {
    private _totaisMensais: ListaTotalMensalData;
    private _status: string;
    private _observacoes: string;

    constructor(totaisMensais: ListaTotalMensalData, status: string) {
        this._totaisMensais = totaisMensais;
        this._status = status;
    }
    get status(): string {
        return this._status;
    }
    get totaisMensais(): ListaTotalMensalData {
        return this._totaisMensais;
    }

    set observacoes(value: string) {
        this._observacoes = value;
    }

    get observacoes(): string {
        return this._observacoes;
    }
}
