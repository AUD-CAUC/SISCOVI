export class FeriasCalcular {
    codTerceirizadoContrato: number;
    tipoRestituicao: string;
    diasVendidos: number;
    inicioFerias: Date;
    fimFerias: Date;
    inicioPeriodoAquisitivo: Date;
    fimPeriodoAquisitivo: Date;
    valorMovimentado: number;
    parcelas: number;
    nomeTerceirizado: string;
    pValorMovimentado: number;
    pTotalFerias: number;
    pTotalTercoConstitucional: number;
    pTotalIncidenciaFerias: number;
    pTotalIncidenciaTerco: number;

    constructor(codTerceirizadoContrato: number,
                tipoRestituicao: string,
                diasVendidos: number,
                inicioFerias: Date,
                fimFerias: Date,
                inicioPeriodoAquisitivo: Date,
                fimPeriodoAquisitivo: Date,
                valorMovimentado: number,
                parcelas: number,
                pValorMovimentado: number,
                pTotalFerias: number,
                pTotalTercoConstitucional: number,
                pTotalIncidenciaFerias: number,
                pTotalIncidenciaTerco: number) {
        this.codTerceirizadoContrato = codTerceirizadoContrato;
        this.tipoRestituicao = tipoRestituicao;
        this.diasVendidos = diasVendidos;
        this.inicioFerias = inicioFerias;
        this.fimFerias = fimFerias;
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;
        this.valorMovimentado = valorMovimentado;
        this.parcelas = parcelas;
        this.pValorMovimentado = pValorMovimentado;
        this.pTotalFerias = pTotalFerias;
        this.pTotalTercoConstitucional = pTotalTercoConstitucional;
        this.pTotalIncidenciaFerias = pTotalIncidenciaFerias;
        this.pTotalIncidenciaTerco = pTotalIncidenciaTerco;
    }

   public  getCodTerceirizadoContrato(): number {
        return this.codTerceirizadoContrato;
    }

    public getTipoRestituicao(): string {
        return this.tipoRestituicao;
    }
    public getDiasVendidos(): number {
        return this.diasVendidos;
    }

    public getInicioFerias(): Date {
        return this.inicioFerias;
    }

    public getFimFerias(): Date {
        return this.fimFerias;
    }

    public getInicioPeriodoAquisitivo(): Date {
        return this.inicioPeriodoAquisitivo;
    }

    public getFimPeriodoAquisitivo(): Date {
        return this.fimPeriodoAquisitivo;
    }

    public getValorMovimentado(): number {
        return this.valorMovimentado;
    }

    public getParcelas(): number {
        return this.parcelas;
    }
    public setInicioFerias(inicioFerias: Date): void {
        this.inicioFerias = inicioFerias;
    }
    public setFimFerias(fimFerias: Date) {
        this.fimFerias = fimFerias;
    }
    public setNomeTerceirizado(nomeTerceirizado: string): void {
        this.nomeTerceirizado = nomeTerceirizado;
    }
    public getNomeTercerizado(): string {
        return this.nomeTerceirizado;
    }
    public setInicioPeriodoAquisitivo(inicioPeriodoAquisitivo: Date): void {
       this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
    }
    public setFimPeriodoAquisitivo(fimPeriodoAquisitivo: Date): void {
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;
    }

    public getPTotalTercoConstitucional(): number {
        return this.pTotalTercoConstitucional;
    }

    public getPTotalIncidenciaFerias(): number {
        return this.pTotalIncidenciaFerias;
    }

    public getPTotalFerias(): number {
        return this.pTotalFerias;
    }

    public getPValorMovimentado(): number {
        return this.pValorMovimentado;
    }

    public getPTotalIncidenciaTerco(): number {
        return this.pTotalIncidenciaTerco;
    }
}

