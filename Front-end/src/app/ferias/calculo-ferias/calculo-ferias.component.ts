import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ContratosService} from '../../contratos/contratos.service';
import {Contrato} from '../../contratos/contrato';
import {TerceirizadoFeriasMovimentacao} from '../terceirizado-ferias-movimentacao';
import {FeriasService} from '../ferias.service';

@Component({
    selector: 'app-calculo-ferias-component',
    templateUrl: './calculo-ferias.component.html',
    styleUrls: ['./calculo-ferias.component.scss']
})
export class CalculoFeriasComponent {
    protected contratos: Contrato[];
    protected terceirizados: TerceirizadoFeriasMovimentacao[];
    codigo: number;
    tipoRestituicao: string;
    @Output() navegaParaViewDeCalculos = new EventEmitter();

    constructor(private contratoService: ContratosService, private feriasService: FeriasService) {
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
        });
    }

    defineCodigoContrato(codigoContrato: number): void {
        this.terceirizados = null;
        this.codigo = codigoContrato;
        if (this.codigo && this.tipoRestituicao) {
            this.feriasService.getFuncionariosFerias(this.codigo, this.tipoRestituicao).subscribe(res => {
                this.terceirizados = res;
            });
        }
    }

    defineTipoMovimentacao(tipoMovimentacao: string): void {
        this.tipoRestituicao = tipoMovimentacao;
        if (this.codigo && this.tipoRestituicao) {
            this.feriasService.getFuncionariosFerias(this.codigo, this.tipoRestituicao).subscribe(res => {
                this.terceirizados = res;
            });
        }
    }

    eventNav(codigo: number): void {
        console.log(codigo);
        this.navegaParaViewDeCalculos.emit(codigo);
    }
}
