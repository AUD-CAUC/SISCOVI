import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {FeriasService} from '../ferias.service';
import {ContratosService} from '../../contratos/contratos.service';
import {ConfigService} from '../../_shared/config.service';
import {Contrato} from '../../contratos/contrato';
import {FeriasCalculosPendentes} from '../ferias-pendentes/ferias-calculos-pendentes';

@Component({
    selector: 'app-relatorio-retencoes-ferias-component',
    templateUrl: './relatorio-retencoes-ferias.component.html',
    styleUrls: ['../ferias.component.scss']
})
export class RelatorioRetencoesFeriasComponent {
    contratos: Contrato[];
    @Input() codigoContrato = 0;
    isSelected = false;
    calculosFerias: FeriasCalculosPendentes[];
    config: ConfigService;
    constructor(private feriasService: FeriasService, private contratoService: ContratosService, config: ConfigService, private ref: ChangeDetectorRef) {
        this.config = config;
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
            if (this.codigoContrato) {
                this.feriasService.getRetencoesFerias(this.codigoContrato).subscribe(res2 => {
                    this.calculosFerias = res2;
                    if (this.calculosFerias.length === 0) {
                        this.calculosFerias = null;
                        this.ref.markForCheck();
                    }
                });
            }
        });
    }
    defineCodigoContrato(codigoContrato: number): void {
        this.codigoContrato = codigoContrato;
        if (this.codigoContrato) {
            this.feriasService.getRetencoesFerias(this.codigoContrato).subscribe(res2 => {
                this.calculosFerias = res2;
                if (this.calculosFerias.length === 0) {
                   this.calculosFerias = null;
                   this.ref.markForCheck();
                }
            });
        }
    }
}
