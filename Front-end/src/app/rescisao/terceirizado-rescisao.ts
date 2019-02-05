export class TerceirizadoRescisao {
    private _codTerceirizadoContrato: number;
    private _nomeTerceirizado: string;
    private _dataDesligamento: Date;
    private _tipoRescisao: string;
    private _tipoRestituicao: string;
    constructor(codTerceirizadoContrato: number, nomeTerceirizado: string, dataDesligamento: Date, tipoRescisao: string, tipoRestituicao: string) {
        this._codTerceirizadoContrato = codTerceirizadoContrato;
        this._nomeTerceirizado = nomeTerceirizado;
        this._dataDesligamento = dataDesligamento;
        this._tipoRescisao = tipoRescisao;
        this._tipoRestituicao = tipoRestituicao;
    }
    public get nomeTerceirizado() {
        return this._nomeTerceirizado;
    }

    public get codTerceirizadoContrato() {
        return this._codTerceirizadoContrato;
    }

    get dataDesligamento(): Date {
        return this._dataDesligamento;
    }

    get tipoRescisao(): string {
        return this._tipoRescisao;
    }

    get tipoRestituicao(): string {
        return this._tipoRestituicao;
    }
}
