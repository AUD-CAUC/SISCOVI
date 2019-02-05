import {Component, EventEmitter, Output, ViewChild} from '@angular/core';
import {MaterializeAction} from 'angular2-materialize';
import {TotalMensalRetComponent} from './total-mensal-ret/total-mensal-ret.component';

@Component({
    selector: 'app-total-mensal',
    templateUrl: './total-mensal.component.html',
    styleUrls: ['./total-mensal.component.scss']
})
export class TotalMensalComponent {
    contentAvailable: Content = Content.Calculos;
    tabSelectionParams = ['select_tab', 'test2'];
    codigoContrato: number;
    constructor() {}

    calculosPendentes(codigoContrato: number) {
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test3'];
        this.contentAvailable = Content.Pendentes;
    }
    calculosPendentesExecucao(codigoContrato: number) {
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test4'];
        this.contentAvailable = Content.Executados;
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
        if (this.codigoContrato) {
            this.codigoContrato = null;
        }
        this.contentAvailable = Content.Retencoes;
        this.tabSelectionParams = ['select_tab', 'test1'];
    }
    setCalcularActive(): void {
        if (this.codigoContrato) {
            this.codigoContrato = null;
        }
        this.contentAvailable = Content.Calculos;
        this.tabSelectionParams = ['select_tab', 'test2'];
    }
    setPendentesActive(): void {
        if (this.codigoContrato) {
            this.codigoContrato = null;
        }
        this.contentAvailable = Content.Pendentes;
        this.tabSelectionParams = ['select_tab', 'test3'];
    }
    setExecutadosActive(): void {
        if (this.codigoContrato) {
            this.codigoContrato = null;
        }
        this.contentAvailable = Content.Executados;
        this.tabSelectionParams = ['select_tab', 'test4'];
    }
    calculosRetidos(codigoContrato: number) {
        this.codigoContrato = codigoContrato;
        this.tabSelectionParams = ['select_tab', 'test1'];
        this.contentAvailable = Content.Retencoes;
    }

}
enum Content {Calculos, Retencoes, Pendentes, Executados}