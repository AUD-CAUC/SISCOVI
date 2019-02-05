import {Component} from '@angular/core';

@Component({
    selector: 'app-rescisao-component',
    templateUrl: './rescisao.component.html',
    styleUrls: ['./rescisao.component.scss']
})
export class RescisaoComponent {
    contentAvailable: Content = Content.Calculos;
    tabSelectionParams = ['select_tab', 'test2'];
    codigoContrato: number;

    constructor() {    }

    calculosPendentes(codigoContrato: number) {
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test3'];
        this.setPendentesActive();
    }
    testeCalculo(): boolean {
        if (this.contentAvailable === Content.Calculos) {
            return true;
        }
        return false;
    }
    testePendentes(): boolean {
        if (this.contentAvailable === Content.Pendentes) {
            return true;
        }
        return false;
    }
    testeRetencoes() {
        if (this.contentAvailable === Content.Retencoes) {
            return true;
        }
        return false;
    }
    testeExecutados() {
        if (this.contentAvailable === Content.Executados) {
            return true;
        }
        return false;
    }
    setRetencoesActive(): void {
        this.contentAvailable = Content.Retencoes;
        this.tabSelectionParams = ['select_tab', 'test1'];
    }
    setCalcularActive(): void {
        this.contentAvailable = Content.Calculos;
        this.tabSelectionParams = ['select_tab', 'test2'];
    }
    setPendentesActive(): void {
        this.contentAvailable = Content.Pendentes;
        this.tabSelectionParams = ['select_tab', 'test3'];
    }
    setExecutadosActive(): void {
        this.contentAvailable = Content.Executados;
        this.tabSelectionParams = ['select_tab', 'test4'];
    }
}
enum Content {Calculos, Retencoes, Pendentes, Executados}
