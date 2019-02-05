import {Component} from '@angular/core';
import {Contrato} from '../contratos/contrato';
import {ConvencaoService} from './convencao.service';
import {Convencao} from './convencao';

@Component({
  selector: 'app-convencao-coletiva',
  templateUrl: 'convencoes-coletivas.component.html',
  styleUrls: ['convencoes.coletivas.component.scss']
})
export class ConvencoesColetivasComponent {
  contratos: Contrato[] = [];
  convServ: ConvencaoService;
  convencoes: Convencao[] = [];
  valid = false;
  index: number;
  constructor(convServ: ConvencaoService) {
    this.convServ = convServ;
    convServ.getAll().subscribe(res => {
      this.convencoes = res;
    });
  }
  /*onChange(value: number): void {
    this.convServ.getConvencoes(value).subscribe(res => {
      this.convencoes = res;
      this.valid = true;
      this.index = value - 1;
    });
  }*/
}
