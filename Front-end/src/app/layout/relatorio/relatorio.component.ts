import { Component } from '@angular/core';

import { ConfigService } from '../../_shared/config.service';

@Component({
  selector: 'app-relatorio',
  templateUrl: './relatorio.component.html',
  styleUrls: [
    './relatorio.component.scss',
    './relatorio-child.scss'
  ]
})
export class RelatorioComponent {
  constructor(public config: ConfigService) { }
}
