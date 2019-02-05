export class TotalMensal {
    private _funcao: string;
    private _ferias: number;
    private _tercoConstitucional: number;
    private _decimoTerceiro: number;
    private _incidencia: number;
    private _multaFGTS: number;
    private _total: number;
    private _status: string;
    private _numeroTerceirizados;
    constructor(funcao: string, ferias: number, tercoConstitucional: number, decimoTerceiro: number, incidencia: number, multaFGTS: number, total: number, status: string, numeroTerceirizados: number) {
        this._funcao = funcao;
        this._ferias = ferias;
        this._tercoConstitucional = tercoConstitucional;
        this._decimoTerceiro = decimoTerceiro;
        this._incidencia = incidencia;
        this._multaFGTS = multaFGTS;
        this._total = total;
        this._status = status;
        this._numeroTerceirizados = numeroTerceirizados;
    }


    get funcao(): string {
        return this._funcao;
    }

    get ferias(): number {
        return this._ferias;
    }

    get tercoConstitucional(): number {
        return this._tercoConstitucional;
    }

    get decimoTerceiro(): number {
        return this._decimoTerceiro;
    }

    get incidencia(): number {
        return this._incidencia;
    }

    get multaFGTS(): number {
        return this._multaFGTS;
    }

    get total(): number {
        return this._total;
    }
    get status(): string {
        return this._status;
    }

    get numeroTerceirizados() {
        return this._numeroTerceirizados;
    }
}
