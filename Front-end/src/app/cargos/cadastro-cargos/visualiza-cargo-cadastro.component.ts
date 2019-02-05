import {ChangeDetectorRef, Component, Input, OnChanges, Output} from '@angular/core';
import {Cargo} from '../cargo';
import {CargoService} from '../cargo.service';

@Component({
    selector: 'app-visualiza-cargo-cadastro',
    templateUrl: './visualiza-cargo-cadastro.component.html'
})
export class VisualizaCargoCadastroComponent implements OnChanges {
    @Input()
    cargos: Cargo[];
    constructor(private cargoService: CargoService, private cdr: ChangeDetectorRef) {
        this.cargoService.cargos = this.cargos;
    }
    ngOnChanges() {
        setTimeout(() => {
            this.cargoService.loading = false;
        });
    }
    editarCampo(event) {
        event.target.contentEditable = true;
        event.target.focus();
    }
    editarNomeCargo(event: any, i: number): void {
        event.target.contentEditable = false;
        this.cargos[i].nome = event.target.textContent;
        this.cargoService.cargos = this.cargos;
    }
    editarDescricao(event: any, i: number): void {
        event.target.contentEditable = false;
        this.cargos[i].descricao = event.target.textContent;
        this.cargoService.cargos = this.cargos;
    }
}
