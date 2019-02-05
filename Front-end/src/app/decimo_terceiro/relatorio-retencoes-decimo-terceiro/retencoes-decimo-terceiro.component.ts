import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {Contrato} from '../../contratos/contrato';
import {ConfigService} from '../../_shared/config.service';
import {DecimoTerceiroService} from '../decimo-terceiro.service';
import {ContratosService} from '../../contratos/contratos.service';
import {TerceirizadoDecimoTerceiro} from '../terceirizado-decimo-terceiro';

@Component({
    selector: 'app-relatorio-retencoes-decimo-terceiro-component',
    templateUrl: './retencoes-decimo-terceiro.component.html',
    styleUrls: ['../decimo-terceiro.component.scss']
})
export class RetencoesDecimoTerceiroComponent {
    @Input() codigoContrato: number;
    contratos: Contrato[];
    isSelected = false;
    calculos: TerceirizadoDecimoTerceiro[];
    config: ConfigService;

    constructor(config: ConfigService, private decimoTerceiroService: DecimoTerceiroService, private contratoService: ContratosService, private ref: ChangeDetectorRef) {
        this.config = config;
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
            if (this.codigoContrato) {
                this.decimoTerceiroService.getRestituicoesDecimoTerceiro(this.codigoContrato).subscribe(res2 => {
                    this.calculos = res2;
                    if (this.calculos.length === 0) {
                        this.calculos = null;
                        this.ref.markForCheck();
                    } else {
                    }
                });
            }
        });
    }

    defineCodigoContrato(codigoContrato: number): void {
        this.codigoContrato = codigoContrato;
        if (this.codigoContrato) {
            this.decimoTerceiroService.getRestituicoesDecimoTerceiro(this.codigoContrato).subscribe(res2 => {
                this.calculos = res2;
                if (this.calculos.length === 0) {
                    this.calculos = null;
                    this.ref.markForCheck();
                } else {

                }
            });
        }
    }
}
