import { Component } from '@angular/core';
import {ConfigService} from '../_shared/config.service';

@Component({
  selector: 'app-inicio',
  templateUrl: 'inicio.component.html'
})
export class InicioComponent {
  constructor(config: ConfigService) {}
}
