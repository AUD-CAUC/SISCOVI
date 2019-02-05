import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-recalculo-total-mensal-component',
    templateUrl: './recalculo-total-mensal.component.html'
})
export class RecalculoTotalMensalComponent {
    dataReferencia: Date;
    codigoContrato: number;
    constructor(private route: ActivatedRoute, private router: Router) {
        this.route.params.subscribe(params => {
            this.dataReferencia = params['dataReferencia'];
            this.codigoContrato = params['id'];
        });
    }
}
