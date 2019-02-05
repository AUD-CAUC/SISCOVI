import { Component } from '@angular/core';
import {VigenciaService} from './vigencia.service';
import {ContratosService} from '../contratos/contratos.service';
import {Contrato} from '../contratos/contrato';
import {ListaVigencias} from './lista.vigencias';

@Component({
  selector: 'app-vigencia',
  templateUrl: './vigencia.dos.contratos.component.html',
  styleUrls: ['./vigencia.dos.contratos.component.scss']
})
export class VigenciaDosContratosComponent {
  contratoService: ContratosService;
  vigServ: VigenciaService;
  contratos: Contrato[] = [];
  vigencias: ListaVigencias[] = [];
  constructor(vigServ: VigenciaService, contrService: ContratosService) {
    this.contratoService = contrService;
    this.vigServ = vigServ;
    this.contratoService.getContratosDoUsuario().subscribe(res => {
      this.contratos = res;
      this.vigServ.getVigenciasDosContratos(this.contratos).subscribe(res2 => {
        this.vigencias = res2;
        console.log(this.vigencias);
      });
    });
  }
}
