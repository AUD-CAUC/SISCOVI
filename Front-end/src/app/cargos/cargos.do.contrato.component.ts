import {Component} from '@angular/core';
import {ContratosService} from '../contratos/contratos.service';
import {CargoService} from './cargo.service';
import {Contrato} from '../contratos/contrato';
import {Cargos} from './cargos';
import {ConfigService} from '../_shared/config.service';

@Component({
  selector: 'app-cargos-component',
  templateUrl: 'cargos.do.contrato.component.html',
  styleUrls: ['./cargo.component.scss']
})
export class CargosDoContratoComponent {
  contratos: Contrato[] = [];
  listaCargos: Cargos[] = [];
  config: ConfigService;
  constructor(constServ: ContratosService, cargoService: CargoService, config: ConfigService) {
    this.config = config;
    constServ.getContratosDoUsuario().subscribe(res => {
      this.contratos = res;
      cargoService.getCargosDosContratos(this.contratos).subscribe(res2 => {
        this.listaCargos = res2;
      });
    });
  }
}
