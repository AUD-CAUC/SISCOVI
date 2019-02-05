import {Component, EventEmitter} from '@angular/core';
import {ConfigService} from '../../_shared/config.service';
import {FuncionariosService} from '../../funcionarios/funcionarios.service';
import {CargoService} from '../cargo.service';
import {Contrato} from '../../contratos/contrato';
import {ContratosService} from '../../contratos/contratos.service';
import {ListaCargosFuncionarios} from './lista.cargos.funcionarios';
import {ActivatedRoute, Router} from '@angular/router';
import {MaterializeAction} from 'angular2-materialize';

@Component({
  selector: 'app-cargos-funcionarios',
  templateUrl: './cargos.dos.funcionarios.html',
  styleUrls: ['./cargos.dos.funcionarios.scss']
})
export class CargosDosFuncionariosComponent {
    contratos: Contrato[] = [];
    cargServ: CargoService;
    listaCargosFuncionarios: ListaCargosFuncionarios[] = [];
    valid = false;
    modalActions = new EventEmitter<string | MaterializeAction>();
    constructor(config: ConfigService, funcServ: FuncionariosService, cargServ: CargoService, contServ: ContratosService, private router: Router, private route: ActivatedRoute) {
        this.cargServ = cargServ;
        contServ.getContratosDoUsuario().subscribe(res => {
          this.contratos = res;
        });
    }
    onChange (value: number) {
        this.valid = true;
        this.cargServ.getCargosFuncionarios(value).subscribe(res => {
            this.listaCargosFuncionarios = res;
        }, error => {
            this.listaCargosFuncionarios = [];
        });
    }
    goToGerenciarCargos() {
        this.router.navigate(['./gerenciar-funcoes-terceirizados'], {relativeTo: this.route});
    }
    openModal() {
        this.modalActions.emit({action: 'modal', params: ['open']});
    }
    closeModal() {
        this.modalActions.emit({action: 'close', params: ['close']});
    }
}
