import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {Contrato} from '../../contratos/contrato';
import {ConfigService} from '../../_shared/config.service';
import {ContratosService} from '../../contratos/contratos.service';
import {SaldoTotal} from './saldo-total';
import {SaldoService} from '../saldo.service';

@Component({
  selector: 'app-saldo-component',
  templateUrl: './saldo-total.component.html',
  styleUrls: ['../saldo.component.scss']
})

export class SaldoTotalComponent {
  @Input() codigoContrato: number;
  contratos: Contrato[];
  isSelected = false;
  saldos: SaldoTotal[];
  config: ConfigService;

  constructor(config: ConfigService, private saldoService: SaldoService, private contratoService: ContratosService, private ref: ChangeDetectorRef) {
    this.config = config;
    this.contratoService.getContratosDoUsuario().subscribe(res => {
      this.contratos = res;
      if (this.codigoContrato) {
        this.saldoService.getSaldoTotal(this.codigoContrato).subscribe(res2 => {
          this.saldos = res2;
          if (this.saldos.length === 0) {
            this.saldos = null;
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
      this.saldoService.getSaldoTotal(this.codigoContrato).subscribe(res2 => {
        this.saldos = res2;
        if (this.saldos.length === 0) {
          this.saldoService = null;
          this.ref.markForCheck();
        } else {

        }
      });
    }
  }
}
