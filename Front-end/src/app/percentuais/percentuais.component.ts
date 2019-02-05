import {ContratosService} from '../contratos/contratos.service';
import {Contrato} from '../contratos/contrato';
import {PercentualService} from './percentual.service';
import {Component, EventEmitter} from '@angular/core';
import {ListaDePercentuais} from './lista.de.percentuais';
import { MaterializeAction } from 'angular2-materialize';

@Component({
  selector: 'app-percentuais-do-contrato',
  templateUrl: './percentuais.component.html',
  styleUrls: ['./percentuais.component.scss']
})
export class PercentuaisComponent {
  contratos: Contrato[] = [];
  modalActions = new EventEmitter<string|MaterializeAction>();
  listaPercentuais: ListaDePercentuais[];
  render = false;
  percentServ: PercentualService;
   constructor (constServ: ContratosService, percentServ: PercentualService) {
     this.percentServ = percentServ;
     if (constServ.contratos.length === 0) {
       constServ.getContratosDoUsuario().subscribe(res => {
         this.contratos = res;
         percentServ.getPercentuaisDosContratos(this.contratos).subscribe(res2 => {
           this.listaPercentuais = res2;
         });
       });
     }else {
       this.contratos = constServ.contratos;
       percentServ.getPercentuaisDosContratos(this.contratos).subscribe(res => {
         this.listaPercentuais = res;
       });
     }
   }
   openModal() {
     this.render = true;
     this.modalActions.emit({action: 'modal', params: ['open']});
   }
   closeModal() {
     this.render = false;
     this.modalActions.emit({action: 'modal', params: ['close']});
   }
}
