import {ChangeDetectorRef, Component, EventEmitter} from '@angular/core';
import {Contrato} from '../../contrato';
import {ContratosService} from '../../contratos.service';
import {UserService} from '../../../users/user.service';
import {ConfigService} from '../../../_shared/config.service';
import {Usuario} from '../../../usuarios/usuario';
import {Cargo} from '../../../cargos/cargo';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {PercentualService} from '../../../percentuais/percentual.service';
import {Percentual} from '../../../percentuais/percentual';
import {Convencao} from '../../../convencoes-coletivas/convencao';
import {ConvencaoService} from '../../../convencoes-coletivas/convencao.service';
import {CargoService} from '../../../cargos/cargo.service';
import {EventoContratual} from '../evento-contratual';
import {TipoEventoContratual} from '../tipo-evento-contratual';
import {HistoricoGestor} from '../../../historico/historico-gestor';

@Component({
    selector: 'app-cadastrar-ajustes',
    templateUrl: './cadastrar-ajustes.component.html',
    styleUrls: ['./cadastrar-ajustes.component.scss']
})
export class CadastrarAjustesComponent {
    contratos: Contrato[];
    field = false;
    usuarios: Usuario[];
    cargosCadastrados: Cargo[];
    myForm: FormGroup;
    nomeGestorContrato: string;
    percentualDecimoTerceiro: number;
    percentualFerias: number;
    percentualIncidencia: number;
    percentuaisFerias: Percentual[] = [];
    percentuaisDecimoTerceiro: Percentual[] = [];
    convencoesColetivas: Convencao[] = [];
    contrato: Contrato;
    primeiroSubstituto: string;
    segundoSubstituto: string;
    cadastroAjuste = new EventEmitter();
    tiposEventosContratuais: TipoEventoContratual[] = [];

    constructor(private contratoService: ContratosService, private userService: UserService, private config: ConfigService, private  fb: FormBuilder, private percentService: PercentualService,
                private convService: ConvencaoService, private ref: ChangeDetectorRef, private cargoService: CargoService) {
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
        });
        this.percentService.getPercentuaisFerias().subscribe(res => {
            if (!res.error) {
                this.percentuaisFerias = res;
            }
        });
        this.percentService.getPercentuaisDecimoTerceiro().subscribe(res => {
            if (!res.error) {
                this.percentuaisDecimoTerceiro = res;
            }
        });
        this.convService.getAll().subscribe(res => {
            if (!res.error) {
                this.convencoesColetivas = res;
            }
        });
        if (userService.user.perfil.sigla === 'ADMINISTRADOR') {
            userService.getUsuarios().subscribe(res2 => {
                this.usuarios = res2;
            });
        } else {
            userService.getGestores().subscribe(res3 => {
                this.usuarios = res3;
            });
        }

        this.cargoService.getAllCargos().subscribe(res => {
            this.cargosCadastrados = res;
        }, error2 => {
            console.log(error2);
        });
        this.contratoService.getTiposEventosContratuais().subscribe(res => {
            this.tiposEventosContratuais = res;
        });
    }

    enableField(codigo: number) {
        this.field = false;
        this.contratoService.getNomeDoGestor(codigo).subscribe(res => {
            if (res === 'Este contrato não existe !') {
            } else {
                this.nomeGestorContrato = res;
                this.field = true;
            }
        });
        this.contratoService.getContratoCompletoUsuario(codigo).subscribe(res => {
                this.contrato = res;
            },
            error => {
                console.log(error);
            },
            () => {
                this.startView();
            }
        );
        this.initForm();
    }

    getFormArrayItems() {
        const control = <FormArray>this.myForm.controls.cargos;
        return control.controls;
    }

    formArrayLength() {
        const control = <FormArray>this.myForm.controls.cargos;
        return control.length;
    }

    initForm() {
        this.myForm = this.fb.group({
            cargos: this.fb.array([]),
            tipoAjuste: new FormControl('', [Validators.required]),
            prorrogacao: new FormControl('N', [Validators.required]),
            gestor: new FormControl('', [Validators.required]),
            primeiroSubstituto: new FormControl(''),
            segundoSubstituto: new FormControl(''),
            assinatura: new FormControl('', [Validators.required]),
            inicioVigencia: new FormControl('', [Validators.required]),
            fimVigencia: new FormControl('', [Validators.required]),
            assunto: new FormControl(''),
            percentualFerias: new FormControl('', [Validators.required]),
            percentualDecimoTerceiro: new FormControl('', [Validators.required]),
            percentualIncidencia: new FormControl(''),
            numeroContrato: new FormControl('', [Validators.required]),
            nomeEmpresa: new FormControl('', [Validators.required]),
            cnpj: new FormControl('', [Validators.required]),
            ativo: new FormControl('', [Validators.required]),
            objeto: new FormControl('')
        });
        this.initCargos();
    }

    adicionaCargo(): void {
        const control = <FormArray>this.myForm.controls.cargos;
        const addCtrl = this.initCargos();
        control.push(addCtrl);
    }

    /*removeCargo(i: number) {
      const control = <FormArray>this.myForm.controls.cargos;
      control.removeAt(i);
    }*/
    initCargos() {
        return this.fb.group({
            nome: new FormControl('', [Validators.required]),
            remuneracao: new FormControl('', [Validators.required]),
            descricao: new FormControl(''),
            trienios: new FormControl('0'),
            adicionais: new FormControl('', [Validators.required]),
            convencao: new FormControl(''),
            dataBase: new FormControl('')
        });
    }

    startView() {
        if (this.contrato) {
            this.myForm.controls.numeroContrato.setValue(this.contrato.numeroDoContrato);
            this.myForm.controls.nomeEmpresa.setValue(this.contrato.nomeDaEmpresa);
            this.myForm.controls.cnpj.setValue(this.formaCNPJ(this.contrato.cnpj));
            this.myForm.controls.objeto.setValue(this.contrato.objeto);
            if (this.contrato.seAtivo === 'S' || this.contrato.seAtivo === 'SIM') {
                this.myForm.controls.ativo.setValue('Sim');
            } else {
                this.myForm.controls.ativo.setValue('Não');
            }
            this.myForm.controls.objeto.setValue(this.contrato.objeto);
            this.myForm.controls.gestor.setValue(this.nomeGestorContrato);
            if (this.contrato.percentuais) {
                for (let i = 0; i < this.contrato.percentuais.length; i++) {
                    const percentual: Percentual = this.contrato.percentuais[i];
                    if (percentual.nome.includes('Férias')) {
                        this.percentualFerias = percentual.percentual;
                        this.myForm.controls.percentualFerias.setValue(percentual.percentual);
                    }
                    if (percentual.nome.includes('Décimo terceiro')) {
                        this.percentualDecimoTerceiro = percentual.percentual;
                        this.myForm.controls.percentualDecimoTerceiro.setValue(percentual.percentual);
                    }
                    if (percentual.nome.includes('Incidência')) {
                        this.percentualIncidencia = percentual.percentual;
                        this.myForm.controls.percentualIncidencia.setValue(percentual.percentual);
                    }
                }
            }
            if (this.contrato.historicoGestao.length > 1) {
                if (this.contrato.historicoGestao[1]) {
                    this.primeiroSubstituto = this.contrato.historicoGestao[1].gestor;
                    this.myForm.controls.primeiroSubstituto.setValue(this.primeiroSubstituto);
                }
                if (this.contrato.historicoGestao[2]) {
                    this.segundoSubstituto = this.contrato.historicoGestao[2].gestor;
                    this.myForm.controls.segundoSubstituto.setValue(this.segundoSubstituto);
                }
            }
            this.contrato.funcoes.forEach(funcao => {
                const control = <FormArray>this.myForm.controls.cargos;
                const addCtrl = this.initCargos();
                addCtrl.controls.nome.setValue(funcao.nome);
                addCtrl.controls.nome.disable();
                addCtrl.controls.remuneracao.setValue(funcao.remuneracao);
                addCtrl.controls.descricao.setValue(funcao.descricao);
                addCtrl.controls.trienios.setValue(funcao.trienios);
                addCtrl.controls.adicionais.setValue(funcao.adicionais);
                if (funcao.convencao) {
                    addCtrl.controls.convencao.setValue(funcao.convencao.codigo);
                    addCtrl.controls.dataBase.setValue(this.dateToString(funcao.convencao.dataBase));
                }
                control.push(addCtrl);
            });
            this.myForm.updateValueAndValidity();
            this.ref.markForCheck();
            this.ref.detectChanges();
        }
    }

    checkValidity() {
        if (this.myForm.valid) {
            this.cadastroAjuste.emit('Chamando Evento');
        }
    }

    protected selectConvencao(codConvencao: number, indexForm: number): void {
        const i: number = this.convencoesColetivas.findIndex(item => item.codigo === Number(codConvencao));
        if (i !== -1) {
            this.getFormArrayItems()[indexForm].get('dataBase').setValue(this.dateToString(this.convencoesColetivas[i].dataBase));
        }
    }

    private dateToString(value: any): string {
        const date: string[] = value.split('-');
        return date[2] + '/' + date[1] + '/' + date['0'];
    }

    private stringToDate(value: string): Date {
        const date: string[] = value.split('/');
        return new Date(Number(date[2]), Number(date[1]) - 1, Number(date[0]));
    }

    private formaCNPJ(value: string): string {
        const firstString: string = value.substring(0, 2);
        const secondString: string = value.substring(2, 5);
        const thirdString: string = value.substring(5, 8);
        const fourthString: string = value.substring(8, 12);
        const fifthString: string = value.substring(12);
        return firstString + '.' + secondString + '.' + thirdString + '/' + fourthString + '-' + fifthString;
    }

    verificaAjusteASerCadastrado() {
        const contrato: Contrato = Object.assign({}, this.contrato);
        const funcoesContrato: Cargo[] = this.contrato.funcoes;
        contrato.percentuais = [];
        contrato.historicoGestao = [];
        contrato.funcoes = [];
        const eventoContratual: EventoContratual = new EventoContratual();
        let index: number = this.tiposEventosContratuais.findIndex(item => item.cod === Number(this.myForm.get('tipoAjuste').value));
        if (index !== -1) {
            eventoContratual.tipo = this.tiposEventosContratuais[index];
        }
        eventoContratual.assunto = this.myForm.get('assunto').value;
        eventoContratual.prorrogacao = this.myForm.get('prorrogacao').value;
        eventoContratual.dataInicioVigencia = this.myForm.get('inicioVigencia').value;
        eventoContratual.dataFimVigencia = this.myForm.get('fimVigencia').value;
        eventoContratual.dataAssinatura = this.myForm.get('assinatura').value;
        this.getFormArrayItems().forEach(control => {
            let funcao: Cargo = this.contrato.funcoes.find(element => element.nome === control.get('nome').value);
            index = this.contrato.funcoes.findIndex(item => item.nome === control.get('nome').value);
            funcao = this.contrato.funcoes[index];
            if ((Number(control.get('remuneracao').value) !== funcao.remuneracao) ||
                (Number(control.get('trienios').value) !== funcao.trienios) ||
                (Number(control.get('adicionais').value) !== funcao.adicionais) || (control.get('convencao').value.length !== 0)) {

                const func: Cargo = funcao;
                func.remuneracao = Number(control.get('remuneracao').value);
                func.trienios = Number(control.get('trienios').value);
                func.adicionais = Number(control.get('adicionais').value);

                if (funcao.convencao) {
                    if (funcao.convencao.codigo !== Number(control.get('convencao').value)) {
                        funcao.convencao = this.convencoesColetivas.find(item => item.codigo === Number(control.get('convencao').value));
                    }
                } else if (control.get('convencao').value.length !== 0) {
                    funcao.convencao = this.convencoesColetivas.find(item => item.codigo === Number(control.get('convencao').value));
                }
                contrato.funcoes.push(func);
            }

        });
        this.contrato.percentuais.forEach(percentual => {
            if (percentual.nome.includes('Décimo terceiro')) {
                if (Number(this.myForm.get('percentualDecimoTerceiro').value) !== percentual.percentual) {
                    const pct: Percentual = new Percentual();
                    pct.nome = percentual.nome;
                    pct.codigo = percentual.codigo;
                    pct.dataInicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
                    pct.dataFim = this.stringToDate(this.myForm.get('fimVigencia').value);
                    pct.dataAditamento = this.stringToDate(this.myForm.get('assinatura').value);
                    pct.percentual = Number(this.myForm.get('percentualDecimoTerceiro').value);
                    contrato.percentuais.push(pct);
                }
            }
            if (percentual.nome.includes('Férias')) {
                if (Number(this.myForm.get('percentualFerias').value) !== percentual.percentual) {
                    let pct: Percentual = new Percentual();
                    pct.nome = percentual.nome;
                    pct.codigo = percentual.codigo;
                    pct.dataInicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
                    pct.dataFim = this.stringToDate(this.myForm.get('fimVigencia').value);
                    pct.dataAditamento = this.stringToDate(this.myForm.get('assinatura').value);
                    pct.percentual = Number(this.myForm.get('percentualFerias').value);
                    contrato.percentuais.push(pct);
                    pct = this.contrato.percentuais.find(item => item.nome.includes('Terço'));
                    pct.dataInicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
                    pct.dataFim = this.stringToDate(this.myForm.get('fimVigencia').value);
                    pct.dataAditamento = this.stringToDate(this.myForm.get('assinatura').value);
                    pct.percentual = Number(this.myForm.get('percentualFerias').value) / 3;
                    contrato.percentuais.push(pct);
                }
            }
            if (percentual.nome.includes('Incidência')) {
                if (Number(this.myForm.get('percentualIncidencia').value) !== percentual.percentual) {
                    const pct: Percentual = new Percentual();
                    pct.nome = percentual.nome;
                    pct.codigo = percentual.codigo;
                    pct.dataInicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
                    pct.dataFim = this.stringToDate(this.myForm.get('fimVigencia').value);
                    pct.dataAditamento = this.stringToDate(this.myForm.get('assinatura').value);
                    pct.percentual = Number(this.myForm.get('percentualDecimoTerceiro').value);
                    contrato.percentuais.push(pct);

                }
            }
        });
        if (this.myForm.get('gestor').value !== this.nomeGestorContrato) {
            const historico: HistoricoGestor = new HistoricoGestor();
            historico.inicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
            historico.gestor = this.myForm.get('gestor').value;
            historico.fim = this.stringToDate(this.myForm.get('fimVigencia').value);
            historico.codigoPerfilGestao = 1;
            contrato.historicoGestao.push(historico);
        }
        if (this.myForm.get('primeiroSubstituto').value.length > 0) {
            const historico: HistoricoGestor = this.contrato.historicoGestao.find(item => item.codigoPerfilGestao === 2);
            let create = false;
            if (historico) {
                if (this.myForm.get('primeiroSubstituto').value !== historico.gestor) {
                    create = true;
                }
            } else {
                create = true;
            }
            if (create) {
                const hist: HistoricoGestor = new HistoricoGestor();
                hist.inicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
                hist.gestor = this.myForm.get('primeiroSubstituto').value;
                hist.fim = this.stringToDate(this.myForm.get('fimVigencia').value);
                hist.codigoPerfilGestao = 2;
                contrato.historicoGestao.push(hist);
            }
        }
        if (this.myForm.get('segundoSubstituto').value.length > 0) {
            const historico: HistoricoGestor = this.contrato.historicoGestao.find(item => item.codigoPerfilGestao === 3);
            let create = false;
            if (historico) {
                if (this.myForm.get('segundoSubstituto').value !== historico.gestor) {
                    create = true;
                }
            } else {
                create = true;
            }
            if (create) {
                const hist: HistoricoGestor = new HistoricoGestor();
                hist.inicio = this.stringToDate(this.myForm.get('inicioVigencia').value);
                hist.gestor = this.myForm.get('segundoSubstituto').value;
                hist.fim = this.stringToDate(this.myForm.get('fimVigencia').value);
                hist.codigoPerfilGestao = 3;
                contrato.historicoGestao.push(hist);
            }

        }
        contrato.eventoContratual = eventoContratual;
        this.contratoService.cadastrarAjuste(contrato).subscribe(res => {

        });
    }

    public findInvalidControls() {
        const invalid = [];
        const controls = this.myForm.controls;
        for (const name in controls) {
            if (controls[name].invalid) {
                invalid.push({nome: name, error: [controls[name].errors]});
            }
        }
        return invalid;
    }
}
