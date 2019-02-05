import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ComumComponent} from './layout/comum/comum.component';
import {LoginComponent} from './users/login.component';
import {LoggedInGuard} from './users/logged-in.guard';
import {IndicadoresComponent} from './indicadores/indicadores.component';
import {InicioComponent} from './inicio/inicio.component';
import {RubricasComponent} from './rubricas/rubricas.component';
import {ProfileGuard} from './users/profile.guard';
import {PercentuaisEstaticosComponent} from './percentuais-estaticos/percentuais-estaticos.component';
import {UsuariosComponent} from './usuarios/usuarios.component';
import {CargoComponent} from './cargos/cargo.component';
import {ContratosComponent} from './contratos/contratos.component';
import {PercentuaisComponent} from './percentuais/percentuais.component';
import {CargosDoContratoComponent} from './cargos/cargos.do.contrato.component';
import {ConvencoesColetivasComponent} from './convencoes-coletivas/convencoes.coletivas.component';
import {VigenciaDosContratosComponent} from './vigencia-dos-contratos/vigencia.dos.contratos.component';
import {FuncionariosComponent} from './funcionarios/funcionarios.component';
import {CargosDosFuncionariosComponent} from './cargos/cargos-dos-funcionarios/cargos.dos.funcionarios.component';
import {CadastrarRubricaComponent} from './rubricas/cadastrar-rubrica/cadastrar-rubrica.component';
import {AjusteContratoComponent} from './contratos/ajustes-contratuais/ajuste-contrato.component';
import {HistoricoGestoresComponent} from './historico/historico-gestores.component';
import {TotalMensalComponent} from './totalMensal/total-mensal.component';
import {FeriasComponent} from './ferias/ferias.component';
import {DecimoTerceiroComponent} from './decimo_terceiro/decimo-terceiro.component';
import {RescisaoComponent} from './rescisao/rescisao.component';
import {RecalculoTotalMensalComponent} from './totalMensal/recalculo-total-mensal/recalculo-total-mensal.component';
import {CadastrarTerceirizadoComponent} from './funcionarios/cadastrar-terceirizado/cadastrar-terceirizado.component';
import {CadastrarGestorContratoComponent} from './historico/cadastrar-gestor/cadastrar-gestor-contrato.component';
import {GerenciarCargosTerceirizadosComponent} from './cargos/gerenciar-cargos-terceirizados/gerenciar-cargos-terceirizados.component';
import {CadastroContratoComponent} from './contratos/cadastro-contrato/cadastro.contrato.component';
import {SaldoTotalComponent} from './saldo/total/saldo-total.component';
import {SaldoIndividualComponent} from './saldo/individual/saldo-individual-component';
import {CadastrarAjustesComponent} from './contratos/ajustes-contratuais/cadastrar-ajustes/cadastrar-ajustes.component';

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'home'},
  {path: 're' , component: IndicadoresComponent, canActivate: [LoggedInGuard],
    children: []
  },
  {path: '', component: ComumComponent,
    children: [
      {path: 'login', component: LoginComponent},
      {path: 'indicadores', component: IndicadoresComponent, canActivate: [LoggedInGuard]},
      {path: 'rubricas', component: RubricasComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'rubricas/:id', component: CadastrarRubricaComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'usuarios', component: UsuariosComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'cargos', component: CargoComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'gerenciar', component: IndicadoresComponent, canActivate: [LoggedInGuard]},
      {path: 'contratos', component: ContratosComponent, canActivate: [LoggedInGuard]},
      {path: 'percentuais', component: PercentuaisComponent, canActivate: [LoggedInGuard]},
      {path: 'cargosContrato', component: CargosDoContratoComponent, canActivate: [LoggedInGuard]},
      {path: 'convencoes', component: ConvencoesColetivasComponent, canActivate: [LoggedInGuard]},
      {path: 'terceirizados', component: FuncionariosComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'terceirizados/:id', component: CadastrarTerceirizadoComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'terceirizados/cadastro-terceirizado', component: CadastrarTerceirizadoComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'funcoes-dos-terceirizados', component: CargosDosFuncionariosComponent, canActivate: [LoggedInGuard]},
      {path: 'vigencias', component: VigenciaDosContratosComponent, canActivate: [LoggedInGuard]},
      {path: 'home', component: InicioComponent, canActivate: [LoggedInGuard]},
      {path: 'ferias', component: FeriasComponent, canActivate: [LoggedInGuard]},
      {path: 'decTer', component: DecimoTerceiroComponent, canActivate: [LoggedInGuard]},
      {path: 'rescisao', component: RescisaoComponent, canActivate: [LoggedInGuard]},
      {path: 'totalMensal', component: TotalMensalComponent, canActivate: [LoggedInGuard]},
      {path: 'totalMensal/:id/:dataReferencia', component: RecalculoTotalMensalComponent, canActivate: [LoggedInGuard]},
      {path: 'saldoConta', component: InicioComponent, canActivate: [LoggedInGuard]},
      {path: 'percentEst', component: PercentuaisEstaticosComponent, canActivate: [LoggedInGuard, ProfileGuard]},
      {path: 'ajustes-contratuais', component: AjusteContratoComponent, canActivate: [LoggedInGuard]},
      {path: 'ajustes-contratuais/cadastrar-ajuste', component: CadastrarAjustesComponent, canActivate: [LoggedInGuard]},
      {path: 'historico-gestores', component: HistoricoGestoresComponent, canActivate: [LoggedInGuard]},
      {path: 'historico-gestores/:id', component: CadastrarGestorContratoComponent, canActivate: [LoggedInGuard]},
      {path: 'historico-gestores/cadastro-gestor-contrato', component: CadastrarGestorContratoComponent, canActivate: [LoggedInGuard]},
      {path: 'funcoes-dos-terceirizados/gerenciar-funcoes-terceirizados', component: GerenciarCargosTerceirizadosComponent, canActivate: [LoggedInGuard]},
      {path: 'contratos/cadastro-contrato', component: CadastroContratoComponent, canActivate: [LoggedInGuard]},
      {path: 'saldo/individual', component: SaldoIndividualComponent, canActivate: [LoggedInGuard]},
      {path: 'saldo/total', component: SaldoTotalComponent, canActivate: [LoggedInGuard]},
    ]}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
