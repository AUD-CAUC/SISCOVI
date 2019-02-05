import {TotalMensal} from './totalMensal';

export class ListaTotalMensalData {
    private _dataReferencia: Date;
    private _totais: TotalMensal[];


    constructor(dataReferencia: Date, totais: TotalMensal[]) {
        this._dataReferencia = dataReferencia;
        this._totais = totais;
    }

    get dataReferencia(): Date {
        return this._dataReferencia;
    }

    get totais(): TotalMensal[] {
        return this._totais;
    }
}
