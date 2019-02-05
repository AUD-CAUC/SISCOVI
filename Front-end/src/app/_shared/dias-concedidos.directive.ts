import {Directive, EventEmitter, OnInit, Output} from '@angular/core';

@Directive({
    selector: '[appDiasConcedidos]'
})
export class DiasConcedidosDirective implements OnInit {
    @Output() diasConcedidos: EventEmitter<any> = new EventEmitter<any>();
    constructor() {}
    ngOnInit() {
        this.diasConcedidos.emit();
    }
}
