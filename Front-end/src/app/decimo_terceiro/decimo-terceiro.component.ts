import {Component} from '@angular/core';

@Component({
    selector: 'app-decimo-terceiro-component',
    templateUrl: './decimo-terceiro.component.html',
    styleUrls: ['./decimo-terceiro.component.scss']
})
export class DecimoTerceiroComponent {
    contentAvailable: Content = Content.Calculos;
    tabSelectionParams = ['select_tab', 'test2'];
    codigoContrato: number;

    constructor() {    }
    calculosPendentes(codigoContrato: number) {
        this.contentAvailable = Content.Pendentes;
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test3'];
        this.setPendentesActive();
    }
    navegaExecucao(codigoContrato: number) {
        this.contentAvailable = Content.Executados;
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test4'];
        this.setExecutadosActive();
    }
    navegaRetencoes(codigoContrato: number) {
        this.contentAvailable = Content.Retencoes;
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test1'];
        this.setRetencoesActive();
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

