import {Component, EventEmitter} from '@angular/core';
import {CargoService} from './cargo.service';
import {Cargo} from './cargo';
import {MaterializeAction} from 'angular2-materialize';
import {PagerService} from '../_shared/pager.service';

@Component({
  selector: 'app-cargos',
  templateUrl: './cargo.component.html',
  styleUrls: ['./cargo.component.scss']
})
export class CargoComponent {
  cargos: Cargo[] = [];
  modalActions = new EventEmitter<string|MaterializeAction>();
  render = false;
  cargoService: CargoService;
  indice = 1;
  pager: any;
  pagedItems: Cargo[];
  constructor(cargoService: CargoService, private pagerService: PagerService) {
      this.cargoService = cargoService;
    cargoService.getAllCargos().subscribe(res => {
      this.cargos = res;
        this.setPage(1);
    });
  }
  openModal() {
      this.render = true;
      this.modalActions.emit({action: 'modal', params: ['open']});
  }
  closeModal() {
      this.render = false;
      this.cargoService.enabled = false;
      this.cargoService.displayCargos = false;
      this.modalActions.emit({action: 'modal', params: ['close']});
  }
  cadastrarCargos() {
      this.cargoService.cadastrarCargos().subscribe(res => {
          console.log(res);
          if (res === 'Cadastro realizado com sucesso !') {
              this.cargoService.getAllCargos().subscribe(res2 => {
                  this.cargos.slice();
                  this.cargos = res2;
                  this.closeModal();
              });
          }else {
              console.log(res);
          }
      });
  }
    setPage(page: number) {
        // get pager object from service
        this.pager = this.pagerService.getPager(this.cargos.length, page, 30);
        // get current page of items
        this.pagedItems = this.cargos.slice(this.pager.startIndex, this.pager.endIndex + 1);
    }
}
