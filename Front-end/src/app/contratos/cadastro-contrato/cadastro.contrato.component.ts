import {Component, EventEmitter, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {CargoService} from '../../cargos/cargo.service';
import {Cargo} from '../../cargos/cargo';
import {ContratosService} from '../contratos.service';
import {UserService} from '../../users/user.service';
import {ConfigService} from '../../_shared/config.service';
import {Usuario} from '../../usuarios/usuario';
import {MaterializeAction} from 'angular2-materialize';
import {Contrato} from '../contrato';
import {HistoricoGestor} from '../../historico/historico-gestor';
import {Percentual} from '../../percentuais/percentual';
import {Rubrica} from '../../rubricas/rubrica';
import {Convencao} from '../../convencoes-coletivas/convencao';
import {ConvencaoService} from '../../convencoes-coletivas/convencao.service';

@Component({
  selector: 'app-cadastro-contrato',
  templateUrl: './cadastro.contrato.component.html',
  styleUrls: ['./cadastro.contrato.component.scss']
})
export class CadastroContratoComponent implements OnInit {

    router: Router;
    carSer: CargoService;
    cargosCadastrados: Cargo[];
    myForm: FormGroup;
    myForm2: FormGroup;
    fb: FormBuilder;
    fb1: FormBuilder;
    contratoService: ContratosService;
    dataInicio = '';
    usuarios: Usuario[];
    meses =  [
        {valor: 1, mes: 'Janeiro'},
        {valor: 2, mes: 'Fevereiro'},
        {valor: 3, mes: 'Março'},
        {valor: 4, mes: 'Abril'},
        {valor: 5, mes: 'Maio'},
        {valor: 6, mes: 'Junho'},
        {valor: 7, mes: 'Julho'},
        {valor: 8, mes: 'Agosto'},
        {valor: 9, mes: 'Setembro'},
        {valor: 10, mes: 'Outubro'},
        {valor: 11, mes: 'Novembro'},
        {valor: 12, mes: 'Dezembro'}
    ];
    modalActions = new EventEmitter<string | MaterializeAction>();
    convencoesColetivas: Convencao[];
    constructor(router: Router, carSer: CargoService, fb: FormBuilder, fb1: FormBuilder, contratoService: ContratosService, userService: UserService, config: ConfigService,
                private convServ: ConvencaoService) {
        this.router = router;
        this.fb = fb;
        this.fb1 = fb1;
        this.contratoService = contratoService;
        this.carSer = carSer;
        this.carSer.getAllCargos().subscribe(res => {
            this.cargosCadastrados = res;
            this.initCargos();
        });
        this.convServ.getAll().subscribe(res => {
            this.convencoesColetivas = res;
        });
        if (userService.user.perfil.sigla === 'ADMINISTRADOR') {
            userService.getUsuarios().subscribe(res => {
                this.usuarios = res;
            });
        }else {
            userService.getGestores().subscribe(res => {
                this.usuarios = res;
            });
        }
    }
    ngOnInit() {
        this.myForm2 = this.fb1.group({
            inicioVigencia: new FormControl('', [Validators.required, this.myDateValidator]),
            fimVigencia: new FormControl('', [Validators.required, this.myDateValidator]),
            assinatura: new FormControl('', [Validators.required, this.myDateValidator]),
            nomeGestor: new FormControl('', [Validators.required, this.nameValidator]),
            nomeEmpresa: new FormControl('', [Validators.required, this.nameValidator]),
            cnpj: new FormControl('', [Validators.required, this.cnpjValidator]),
            ativo: new FormControl('S', [Validators.required]),
            objeto: new FormControl(''),
            percentualFerias: new FormControl('', [Validators.required]),
            percentualDecimoTerceiro: new FormControl('', [Validators.required]),
            percentualIncidencia: new FormControl('', [Validators.required, this.percentualValidator]),
            numeroContrato: new FormControl('', [Validators.required]),
            primeiroSubstituto: new FormControl(''),
            segundoSubstituto: new FormControl(''),
            numeroProcessoSTJ: new FormControl('')
        });
        this.myForm = this.fb.group({
            cargos: this.fb.array([])
        });
        this.adicionaCargo();
        this.carSer.getAllCargos().subscribe(res => {
            this.cargosCadastrados = res;
            this.initCargos();
        });
        this.contratoService.formValido = this.myForm.valid && this.myForm2.valid;
    }
    initCargos() {
        return this.fb.group({
            nome: new FormControl('', [Validators.required]),
            remuneracao: new FormControl('', [Validators.required]),
            descricao: new FormControl(''),
            adicionais: new FormControl(''),
            trienios: new FormControl(''),
            convencao: new FormControl(''),
            dataBase: new FormControl('')
            // dia: new FormControl('', [Validators.required]),
            // mes: new FormControl('', [Validators.required])
        });
    }
    openModal() {
        this.modalActions.emit({action: 'modal', params: ['open']});
    }
    closeModal() {
        this.modalActions.emit({action: 'modal', params: ['close']});
    }
    adicionaCargo(): void {
        const control = <FormArray>this.myForm.controls.cargos;
        const addCtrl = this.initCargos();
        control.push(addCtrl);
    }
    removeCargo(i: number) {
        const control = <FormArray>this.myForm.controls.cargos;
        control.removeAt(i);
    }
    public myDateValidator(control: AbstractControl): {[key: string]: any} {
        const val = control.value;
        const mensagem = [];
        const otherRegex = new RegExp(/^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/);
        if (val.length > 0 ) {
            const dia = Number(val.split('/')[0]);
            const mes = Number(val.split('/')[1]);
            const ano = Number(val.split('/')[2]);
            if (dia <= 0 || dia > 31 ) {
                mensagem.push('O dia da data é inválido.');
            }
            if (mes <= 0 || mes > 12) {
                mensagem.push('O Mês digitado é inválido');
            }
            if (ano < 2000 || ano > (new Date().getFullYear() + 5)) {
                mensagem.push('O Ano digitado é inválido');
            }
            if (val.length === 10 ) {
                if (!otherRegex.test(val)) {
                    mensagem.push('A data digitada é inválida');
                }
            }
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    public percentualValidator(control: AbstractControl): {[key: string]: any} {
        const val = control.value;
        const mensagem = [];
        if (control.value) {
            if (val === 0) {
                mensagem.push('Digite um valor para o percentual de incidência. Valores comuns para este percentual são  36,8 e 35,6');
            }
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    isValid() {

    }
    formArrayLength() {
        const control = <FormArray>this.myForm.controls.cargos;
        return control.length;
    }
    getFormArrayItems() {
        const control = <FormArray>this.myForm.controls.cargos;
        return control.controls;
    }
    public nameValidator(control: AbstractControl): {[key: string]: any} {
        const exp = new RegExp(/[a-zA-Z-\u00C0-\u00FF]+$/);
        const mensagem = [];
        if (!exp.test(control.value)) {
            mensagem.push('O nome deve conter apenas letras');
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    public cnpjValidator(control: AbstractControl): {[key: string]: any} {
        let cnpj = control.value;
        cnpj = cnpj.replace(/[^\d]+/g, '');
        const mensagem = [];
        const valida = new Array(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2);
        let dig1 = 0;
        let dig2 = 0;
        const digito = Number(cnpj.charAt(12) + cnpj.charAt(13));

        for (let i = 0; i < valida.length; i++) {
            dig1 += (i > 0 ? (cnpj.charAt(i - 1) * valida[i]) : 0);
            dig2 += cnpj.charAt(i) * valida[i];
        }
        dig1 = (((dig1 % 11) < 2) ? 0 : (11 - (dig1 % 11)));
        dig2 = (((dig2 % 11) < 2) ? 0 : (11 - (dig2 % 11)));

        if (((dig1 * 10) + dig2) !== digito) {
            mensagem.push('CNPJ inválido');
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    enviarCadastroContrato() {
        const historicoGestao: HistoricoGestor[] = this.criaHistorico();
        const funcoes: Cargo[] = this.criaFuncoes();
        const percentuais: Percentual[] = this.criaPercentuais();
        const contrato: Contrato = new Contrato(this.myForm2.get('nomeEmpresa').value, this.myForm2.get('cnpj').value, 0, this.myForm2.get('numeroContrato').value, 0,
            this.converteDateFormat(this.myForm2.get('inicioVigencia').value),
            this.converteDateFormat(this.myForm2.get('fimVigencia').value), this.myForm2.get('objeto').value,
            this.myForm2.get('ativo').value,
            historicoGestao,
            funcoes);
        contrato.dataAssinatura = this.converteDateFormat(this.myForm2.get('assinatura').value);
        contrato.numeroProcessoSTJ = this.myForm2.get('numeroProcessoSTJ').value;
        contrato.percentuais = percentuais;
        this.contratoService.cadastrarContrato(contrato).subscribe(res => {
            if (res.success) {
            }else {
            }
        });
    }

    private criaHistorico(): HistoricoGestor[] {
        const lista: HistoricoGestor[] = [];
        let historico = new HistoricoGestor();
        historico.inicio = this.converteDateFormat(this.myForm2.get('inicioVigencia').value);
        historico.gestor = this.myForm2.get('nomeGestor').value;
        historico.fim = this.converteDateFormat(this.myForm2.get('fimVigencia').value);
        historico.codigoPerfilGestao = 1;
        lista.push(historico);
        historico = new HistoricoGestor();
        if (this.myForm2.get('primeiroSubstituto').value.length > 0) {
            historico.inicio = this.converteDateFormat(this.myForm2.get('inicioVigencia').value);
            historico.gestor = this.myForm2.get('primeiroSubstituto').value;
            historico.fim = this.converteDateFormat(this.myForm2.get('fimVigencia').value);
            historico.codigoPerfilGestao = 2;
            lista.push(historico);
            historico = new HistoricoGestor();
        }
        if (this.myForm2.get('segundoSubstituto').value.length > 0) {
            historico.inicio = this.converteDateFormat(this.myForm2.get('inicioVigencia').value);
            historico.gestor = this.myForm2.get('segundoSubstituto').value;
            historico.fim = this.converteDateFormat(this.myForm2.get('fimVigencia').value);
            historico.codigoPerfilGestao = 3;
            lista.push(historico);
        }
        return lista;
    }

    private converteDateFormat(value: string): Date {
        const date: string[] = value.split('/');
        return new Date(Number(date[2]), Number(date[1]) - 1, Number(date[0]));
    }

    private criaFuncoes(): Cargo[] {
        const funcoes: Cargo[] = [];
        const cargosForm = this.getFormArrayItems();
        for (let i = 0; i < this.formArrayLength(); i++) {
            const funcao = new Cargo();
            funcao.nome = this.myForm.get('cargos').get('' + i).get('nome').value;
            funcao.remuneracao = this.myForm.get('cargos').get('' + i).get('remuneracao').value;
            funcao.descricao = this.myForm.get('cargos').get('' + i).get('descricao').value;
            funcao.adicionais = this.myForm.get('cargos').get('' + i).get('adicionais').value;
            funcao.trienios = this.myForm.get('cargos').get('' + i).get('trienios').value;
            const convencao: Convencao = this.convencoesColetivas.find( item => {
                if (item.codigo === Number(this.myForm.get('cargos').get('' + i).get('convencao').value)) {
                    return true;
                }
            });
            convencao.dataBase = this.converteDateFormat(this.myForm.get('cargos').get('' + i).get('dataBase').value);
            const currentName: string =  this.myForm.get('cargos').get('' + i).get('nome').value;
            const index = this.cargosCadastrados.findIndex(item => {
                if (item.nome === currentName) {
                    return true;
                }
            });
            if (index >= 0) {
                funcao.codigo = this.cargosCadastrados[index].codigo;
            }
            funcao.convencao = convencao;
            funcoes.push(funcao);
        }
        return funcoes;
    }

    private criaPercentuais(): Percentual[] {
        const percentuais: Percentual[] = [];
        let percentual = new Percentual();
        const date = this.converteDateFormat(this.myForm2.get('inicioVigencia').value);
        percentual.dataInicio = date;
        percentual.dataAditamento = date;
        percentual.percentual = this.myForm2.get('percentualFerias').value;
        let rubrica = new Rubrica();
        rubrica.codigo = 1;
        rubrica.nome = 'Férias';
        rubrica.sigla = 'Férias';
        percentual.rubrica = rubrica;
        percentuais.push(percentual);
        percentual = new Percentual();
        percentual.dataInicio = date;
        percentual.dataAditamento = date;
        percentual.percentual = this.myForm2.get('percentualDecimoTerceiro').value;
        rubrica = new Rubrica();
        rubrica.codigo = 3;
        rubrica.nome = 'Décimo Terceiro';
        rubrica.sigla = '13º';
        percentual.rubrica = rubrica;
        percentuais.push(percentual);
        percentual = new Percentual();
        percentual.dataInicio = date;
        percentual.dataAditamento = date;
        percentual.percentual = this.myForm2.get('percentualIncidencia').value;
        rubrica = new Rubrica();
        rubrica.codigo = 7;
        rubrica.nome = 'Incidência do submódulo 4.1';
        rubrica.sigla = 'Submódulo 4.1';
        percentual.rubrica = rubrica;
        percentuais.push(percentual);
        return percentuais;
    }
}
