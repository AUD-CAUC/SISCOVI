import {BrowserModule} from '@angular/platform-browser';
import {LOCALE_ID, NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpModule} from '@angular/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MaterializeModule} from 'angular2-materialize';
import {MaskDateDirective} from './_shared/mask-date.directive';
import {ConfigService} from './_shared/config.service';
import {UserService} from './users/user.service';
import {LoginComponent} from './users/login.component';
import {ComumComponent} from './layout/comum/comum.component';
import {FooterComponent} from './layout/footer.component';
import {LoadingComponent} from './layout/loading.component';
import {MenuComponent} from './layout/menu.component';
import {LoggedInGuard} from './users/logged-in.guard';
import {AppRoutingModule} from './app-routing.module';
import {IndicadoresComponent} from './indicadores/indicadores.component';
import {PageMenuComponent} from './_shared/page-menu/page-menu.component';
import {RelatorioComponent} from './layout/relatorio/relatorio.component';
import {InicioComponent} from './inicio/inicio.component';
import {RubricasComponent} from './rubricas/rubricas.component';
import {RubricasService} from './rubricas/rubricas.service';
import {ContratosService} from './contratos/contratos.service';
import {ProfileGuard} from './users/profile.guard';
import {PercentuaisEstaticosComponent} from './percentuais-estaticos/percentuais-estaticos.component';
import {UsuariosComponent} from './usuarios/usuarios.component';
import {CargoComponent} from './cargos/cargo.component';
import {CargoService} from './cargos/cargo.service';
import {ContratosComponent} from './contratos/contratos.component';
import {CpfPipe} from './_shared/cpf.pipe';
import {CnpjPipe} from './_shared/cnpj.pipe';
import {PercentualService} from './percentuais/percentual.service';
import {PercentuaisComponent} from './percentuais/percentuais.component';
import {DatePipe} from './_shared/date.pipe';
import {CargosDoContratoComponent} from './cargos/cargos.do.contrato.component';
import {ConvencoesColetivasComponent} from './convencoes-coletivas/convencoes.coletivas.component';
import {ConvencaoService} from './convencoes-coletivas/convencao.service';
import {VigenciaDosContratosComponent} from './vigencia-dos-contratos/vigencia.dos.contratos.component';
import {VigenciaService} from './vigencia-dos-contratos/vigencia.service';
import {FuncionariosComponent} from './funcionarios/funcionarios.component';
import {FuncionariosService} from './funcionarios/funcionarios.service';
import {AfirmativePipe} from './_shared/afirmative.pipe';
import {CargosDosFuncionariosComponent} from './cargos/cargos-dos-funcionarios/cargos.dos.funcionarios.component';
import {CadastroContratoComponent} from './contratos/cadastro-contrato/cadastro.contrato.component';
import {CadastroUsuarioComponent} from './usuarios/cadastro-usuario/cadastro-usuario.component';
import {CadastroUsuarioService} from './usuarios/cadastro-usuario/cadastro-usuario.service';
import {CurrencyMaskModule} from 'ng2-currency-mask';
import {CadastroPercentuaisComponent} from './percentuais/cadastro-percentuais/cadastro-percentuais.component';
import {MaskPercentageDirective} from './_shared/mask-percentage.directive';
import {CadastrarRubricaComponent} from './rubricas/cadastrar-rubrica/cadastrar-rubrica.component';
import {CadastroCargosComponent} from './cargos/cadastro-cargos/cadastro-cargos.component';
import {VisualizaCargoCadastroComponent} from './cargos/cadastro-cargos/visualiza-cargo-cadastro.component';
import {AjusteContratoComponent} from './contratos/ajustes-contratuais/ajuste-contrato.component';
import {KzMaskDirective} from './_shared/kz-mask.directive';
import {TitlecaseMaskDirective} from './_shared/titlecase-mask.directive';
import {PagerService} from './_shared/pager.service';
import {CadastrarAjustesComponent} from './contratos/ajustes-contratuais/cadastrar-ajustes/cadastrar-ajustes.component';
import {HistoricoGestoresComponent} from './historico/historico-gestores.component';
import {HistoricoService} from './historico/historico.service';
import {TotalMensalComponent} from './totalMensal/total-mensal.component';
import {TotalMensalCalculoComponent} from './totalMensal/total-mensal-calculo/total-mensal-calculo.component';
import {TotalMensalRetComponent} from './totalMensal/total-mensal-ret/total-mensal-ret.component';
import {TotalMensalService} from './totalMensal/total-mensal.service';
import {FeriasComponent} from './ferias/ferias.component';
import {DecimoTerceiroComponent} from './decimo_terceiro/decimo-terceiro.component';
import {RescisaoComponent} from './rescisao/rescisao.component';
import {CalculoFeriasComponent} from './ferias/calculo-ferias/calculo-ferias.component';
import {FeriasService} from './ferias/ferias.service';
import {FeriasCalculosPendentesComponent} from './ferias/ferias-pendentes/ferias-calculos-pendentes.component';
import {RelatorioRetencoesFeriasComponent} from './ferias/relatorio-retencoes-ferias/relatorio-retencoes-ferias.component';
import {ResgateFeriasComponent} from './ferias/calculo-ferias/resgate-ferias.component';
import {MovimentacaoFeriasComponent} from './ferias/calculo-ferias/movimentacao-ferias.component';
import {DiasConcedidosDirective} from './_shared/dias-concedidos.directive';
import {ParcelaPipe} from './_shared/parcela.pipe';
import {CalculoDecimoTerceiroComponent} from './decimo_terceiro/calculo-decimo-terceiro/calculo-decimo-terceiro.component';
import {DecimoTerceiroService} from './decimo_terceiro/decimo-terceiro.service';
import {MovimentacaoDecimoTerceiroComponent} from './decimo_terceiro/calculo-decimo-terceiro/movimentacao-decimo-terceiro.component';
import {ResgateDecimoTerceiroComponent} from './decimo_terceiro/calculo-decimo-terceiro/resgate-decimo-terceiro.component';
import {TotalMensalPendenteComponent} from './totalMensal/calculos-pendentes/total-mensal-pendente.component';
import {CalculoRescisaoComponent} from './rescisao/calculo-rescisao/calculo-rescisao.component';
import {ResgateRescisaoComponent} from './rescisao/calculo-rescisao/resgate-rescisao.component';
import {MovimentacaoRescisaoComponent} from './rescisao/calculo-rescisao/movimentacao-rescisao.component';
import {RescisaoService} from './rescisao/rescisao.service';
import {TotalMensalExecucaoComponent} from './totalMensal/calculos-pendentes/total-mensal-execucao.component';
import {PendentePipe} from './_shared/pendente.pipe';
import {RecalculoTotalMensalComponent} from './totalMensal/recalculo-total-mensal/recalculo-total-mensal.component';
import {HistoricoTotalMensalComponent} from './totalMensal/calculos-pendentes/historico-total-mensal/historico-total-mensal.component';
import {FeriasCalculosPendentesExecucaoComponent} from './ferias/ferias-pendentes/ferias-calculos-pendentes-execucao.component';
import {DecimoTerceiroPendenteComponent} from './decimo_terceiro/decimo-terceiro-pendente/decimo-terceiro-pendente.component';
import {DecimoTerceiroPendenteExecucaoComponent} from './decimo_terceiro/decimo-terceiro-pendente/decimo-terceiro-pendente-execucao.component';
import {RetencoesDecimoTerceiroComponent} from './decimo_terceiro/relatorio-retencoes-decimo-terceiro/retencoes-decimo-terceiro.component';
import {RescisaoPendenteComponent} from './rescisao/rescisoes-pendentes/rescisao-pendente.component';
import {CadastrarTerceirizadoComponent} from './funcionarios/cadastrar-terceirizado/cadastrar-terceirizado.component';
import {CadastrarGestorContratoComponent} from './historico/cadastrar-gestor/cadastrar-gestor-contrato.component';
import {GerenciarCargosTerceirizadosComponent} from './cargos/gerenciar-cargos-terceirizados/gerenciar-cargos-terceirizados.component';
import {SaldoTotalComponent} from './saldo/total/saldo-total.component';
import {SaldoService} from './saldo/saldo.service';
import {SaldoIndividualComponent} from './saldo/individual/saldo-individual-component';

@NgModule({
  declarations: [
    AppComponent,
    MaskDateDirective,
    MaskPercentageDirective,
    KzMaskDirective,
    DiasConcedidosDirective,
    LoginComponent,
    ComumComponent,
    FooterComponent,
    LoadingComponent,
    MenuComponent,
    IndicadoresComponent,
    PageMenuComponent,
    RelatorioComponent,
    InicioComponent,
    RubricasComponent,
    PercentuaisEstaticosComponent,
    UsuariosComponent,
    CargoComponent,
    ContratosComponent,
    CpfPipe,
    CnpjPipe,
    DatePipe,
    ParcelaPipe,
    PendentePipe,
    TitlecaseMaskDirective,
    AfirmativePipe,
    PercentuaisComponent,
    CargosDoContratoComponent,
    ConvencoesColetivasComponent,
    VigenciaDosContratosComponent,
    FuncionariosComponent,
    CargosDosFuncionariosComponent,
    CadastroContratoComponent,
    CadastroUsuarioComponent,
    CadastroPercentuaisComponent,
    CadastrarRubricaComponent,
    CadastroCargosComponent,
    VisualizaCargoCadastroComponent,
    AjusteContratoComponent,
    CadastrarAjustesComponent,
    HistoricoGestoresComponent,
    TotalMensalComponent,
    TotalMensalCalculoComponent,
    TotalMensalRetComponent,
    FeriasComponent,
    DecimoTerceiroComponent,
    RescisaoComponent,
    CalculoFeriasComponent,
    FeriasCalculosPendentesComponent,
    RelatorioRetencoesFeriasComponent,
    ResgateFeriasComponent,
    MovimentacaoFeriasComponent,
    CalculoDecimoTerceiroComponent,
    MovimentacaoDecimoTerceiroComponent,
    ResgateDecimoTerceiroComponent,
    TotalMensalPendenteComponent,
    CalculoRescisaoComponent,
    ResgateRescisaoComponent,
    MovimentacaoRescisaoComponent,
    TotalMensalExecucaoComponent,
    RecalculoTotalMensalComponent,
    HistoricoTotalMensalComponent,
    FeriasCalculosPendentesExecucaoComponent,
    DecimoTerceiroPendenteComponent,
    DecimoTerceiroPendenteExecucaoComponent,
    RetencoesDecimoTerceiroComponent,
    RescisaoPendenteComponent,
    CadastrarTerceirizadoComponent,
    CadastrarGestorContratoComponent,
    GerenciarCargosTerceirizadosComponent,
    SaldoTotalComponent,
    SaldoIndividualComponent,
  ],
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    MaterializeModule,
    AppRoutingModule,
    ReactiveFormsModule,
    CurrencyMaskModule,
  ],
  providers: [
    ConfigService,
    UserService,
    LoggedInGuard,
    ProfileGuard,
    RubricasService,
    ContratosService,
    CargoService,
    PercentualService,
    ConvencaoService,
    VigenciaService,
    FuncionariosService,
    CadastroUsuarioService,
    PagerService,
    HistoricoService,
    TotalMensalService,
    FeriasService,
    DecimoTerceiroService,
    RescisaoService,
    SaldoService,
    {provide: LOCALE_ID, useValue: 'pt'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
