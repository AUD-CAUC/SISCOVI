import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {ComumComponent} from '../layout/comum/comum.component';
import { ConfigService } from '../_shared/config.service';
// import { ChartConfig } from '../charts/chart-config';

@Component(
{
  selector: 'app-indicadores',
  templateUrl: './indicadores.component.html',
})
export class IndicadoresComponent {
  dados: any = {};

  constructor(private config: ConfigService,
              private router: Router) {}
}
