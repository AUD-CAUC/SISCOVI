import {Component, EventEmitter, Output} from '@angular/core';
import {Contrato} from '../../contratos/contrato';
import {ContratosService} from '../../contratos/contratos.service';
import {DecimoTerceiroService} from '../../decimo_terceiro/decimo-terceiro.service';
import {RescisaoService} from "../rescisao.service";

@Component({
    selector: 'app-calculo-rescisao-component',
    templateUrl: './calculo-rescisao.component.html',
    styleUrls: ['./calculo-rescisao.component.scss']
})
export class CalculoRescisaoComponent {
    protected contratos: Contrato[];
    protected terceirizados: Object[];
    codigo: number;
    tipoRestituicao: string;
    @Output() navegaParaViewDeCalculos = new EventEmitter();
    constructor(private contratoService: ContratosService, private rescisaoService: RescisaoService) {
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
        });
    }
    defineCodigoContrato(codigoContrato: number): void {
        this.codigo = codigoContrato;
        if (this.codigo && this.tipoRestituicao) {
            this.rescisaoService.getFuncionariosRescisao(this.codigo, this.tipoRestituicao).subscribe(res => {
                this.terceirizados = res;
            });
        }
    }

    defineTipoMovimentacao(tipoMovimentacao: string): void {
        this.tipoRestituicao = tipoMovimentacao;
        if (this.codigo && this.tipoRestituicao) {
            this.rescisaoService.getFuncionariosRescisao(this.codigo, this.tipoRestituicao).subscribe(res => {
                this.terceirizados = res;
            });
        }
    }
    eventNav(codigo: number): void {
        console.log(codigo);
        this.navegaParaViewDeCalculos.emit(codigo);
    }
}