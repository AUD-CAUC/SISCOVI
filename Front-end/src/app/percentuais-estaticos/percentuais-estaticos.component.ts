import {Component} from '@angular/core';
import {RubricasService} from '../rubricas/rubricas.service';
import {PercentualEstatico} from '../rubricas/percentual-estatico';

@Component({
  selector: 'app-percent-static',
  templateUrl: './percentuais-estaticos.component.html',
  styleUrls: ['./percentuais-estaticos.component.scss']
})
export class PercentuaisEstaticosComponent {
  staticPercent: PercentualEstatico[] = [];
  constructor(rubricaSer: RubricasService) {
    rubricaSer.getPercentuaisEstaticos().subscribe(res => {
      this.staticPercent = res;
      this.staticPercent.forEach( (percentual) => {
        if (percentual.dataFim === null) {
          percentual.dataFim = '-';
        }
        if (percentual.dataInicio === null) {
          percentual.dataInicio = '-';
        }
        if (percentual.dataAditamento === null) {
          percentual.dataAditamento = '-';
        }
      });
    });
  }
}
