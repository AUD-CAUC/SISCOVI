import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {FeriasService} from '../ferias.service';
import {Contrato} from '../../contratos/contrato';
import {TerceirizadoFeriasMovimentacao} from '../terceirizado-ferias-movimentacao';
import {FeriasCalcular} from '../ferias-calcular';
import {MaterializeAction} from 'angular2-materialize';

@Component({
    selector: 'app-resgate-ferias-component',
    templateUrl: './resgate-ferias.component.html',
    styleUrls: ['./calculo-ferias.component.scss']
})
export class ResgateFeriasComponent implements OnInit {
    protected contratos: Contrato[];
    @Input() protected terceirizados: TerceirizadoFeriasMovimentacao[];
    @Input() codigoContrato: number;
    @Input() tipoRestituicao: string;
    feriasResgate: FormGroup;
    isSelected = false;
    selected = false;
    protected diasConcedidos: number[] = [];
    feriasCalcular: FeriasCalcular[] = [];
    modalActions = new EventEmitter<string | MaterializeAction>();
    modalActions2 = new EventEmitter<string | MaterializeAction>();
    modalActions3 = new EventEmitter<string | MaterializeAction>();
    modalActions4 = new EventEmitter<string | MaterializeAction>();
    @Output() navegaParaViewDeCalculos = new EventEmitter();
    constructor(private feriasService: FeriasService, private fb2: FormBuilder) {}
    ngOnInit() {
        this.formInitResgate();
    }
    formInitResgate(): void {
        this.feriasResgate = this.fb2.group({
            calcularTerceirizados: this.fb2.array([])
        });
        const control2 = <FormArray>this.feriasResgate.controls.calcularTerceirizados;
        this.terceirizados.forEach(item => {
            const addCtrl = this.fb2.group({
                codTerceirizadoContrato: new FormControl(item.codigoTerceirizadoContrato, [Validators.required]),
                inicioPeriodoAquisitivo: new FormControl(item.inicioPeriodoAquisitivo, [Validators.required]),
                fimPeriodoAquisitivo: new FormControl(item.fimPeriodoAquisitivo, [Validators.required]),
                parcelas: new FormControl(0, [Validators.required]),
                selected: new FormControl(this.isSelected),
                existeCalculoAnterior: new FormControl(item.existeCalculoAnterior),
                tipoRestituicao: new FormControl(this.tipoRestituicao, [Validators.required]),
                diasVendidos: new FormControl(0, [this.diasVendidosValidator, Validators.required]),
                inicioFerias: new FormControl('', [this.inicioUsufrutoValidator, Validators.required, this.myDateValidator]),
                fimFerias: new FormControl('', [this.fimUsufrutoValidator, Validators.required, this.myDateValidator]),
            });
            control2.push(addCtrl);
        });
        for (let i = 0; i < this.terceirizados.length; i++) {
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('codTerceirizadoContrato').setValidators(Validators.required);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioPeriodoAquisitivo').setValidators(Validators.required);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimPeriodoAquisitivo').setValidators(Validators.required);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('parcelas').setValidators(Validators.required);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('parcelas').setValue(0);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('tipoRestituicao').setValidators(Validators.required);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('diasVendidos').setValidators([this.diasVendidosValidator, Validators.required]);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioFerias').setValidators([Validators.required,
                this.myDateValidator,
                this.inicioUsufrutoValidator,
                Validators.minLength(10),
                Validators.maxLength(10)]);
            this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimFerias').setValidators([Validators.required,
                this.myDateValidator,
                this.fimUsufrutoValidator,
                Validators.minLength(10),
                Validators.maxLength(10)]);
        }
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
    public diasVendidosValidator(control: AbstractControl): {[key: string]: any} | null {
        const mensagem = [];
        if (control.value) {
            if (control.value < 0) {
                mensagem.push('O valor de dias vendidos não pode ser menor que zero !');
            }
        }
        if (control.parent) {
            let dia = 0;
            let mes = 0;
            let ano = 0;
            dia = Number(control.parent.get('fimFerias').value.split('/')[0]);
            mes = Number(control.parent.get('fimFerias').value.split('/')[1]) - 1;
            ano = Number(control.parent.get('fimFerias').value.split('/')[2]);
            const fimUsufruto: Date = new Date(ano, mes, dia);
            dia = Number(control.parent.get('inicioFerias').value.split('/')[0]);
            mes = Number(control.parent.get('inicioFerias').value.split('/')[1]) - 1;
            ano = Number(control.parent.get('inicioFerias').value.split('/')[2]);
            const inicioUsufruto: Date = new Date(ano, mes, dia);
            const diff = Math.abs(fimUsufruto.getTime() - inicioUsufruto.getTime());
            const diffDay: number = Math.round(diff / (1000 * 3600 * 24)) + 1;
            if ((diffDay + Number(control.value)) !== 30) {
                mensagem.push('A quantidade de dias vendidos com o período de usufruto de férias não pode ser maior que trinta dias !');
            }
            if (control.touched || control.dirty) {
                control.parent.updateValueAndValidity();
            }
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    public fimUsufrutoValidator(control: AbstractControl): {[key: string]: any} | null {
        const mensagem = [];
        if (control.parent) {
            if (control.parent.get('inicioFerias').valid && (control.value.length === 10)) {
                let dia = 0;
                let mes = 0;
                let ano = 0;
                dia = Number(control.value.split('/')[0]);
                mes = Number(control.value.split('/')[1]) - 1;
                ano = Number(control.value.split('/')[2]);
                const fimUsufruto: Date = new Date(ano, mes, dia);
                dia = Number(control.parent.get('inicioFerias').value.split('/')[0]);
                mes = Number(control.parent.get('inicioFerias').value.split('/')[1]) - 1;
                ano = Number(control.parent.get('inicioFerias').value.split('/')[2]);
                const inicioUsufruto: Date = new Date(ano, mes, dia);
                if (fimUsufruto <= inicioUsufruto) {
                    mensagem.push('A Data Fim do Usufruto deve ser maior que a Data de Início do Usufruto !');
                }
                const diff = Math.abs(fimUsufruto.getTime() - inicioUsufruto.getTime());
                const diffDay = Math.round(diff / (1000 * 3600 * 24)) + 1;
                if (diffDay > 30) {
                    mensagem.push('O período de férias não pode ser maior que 30 dias !');
                }
            }
            if ((control.touched || control.dirty) && (control.value.length === 10)) {
                if ((control.parent.get('inicioFerias').touched || control.parent.get('inicioFerias').dirty) && control.parent.get('inicioFerias').valid) {
                    control.parent.get('diasVendidos').updateValueAndValidity();
                }
                if (control.valid && control.parent.get('inicioFerias')) {
                    control.parent.get('diasVendidos').updateValueAndValidity();
                }
            }
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    public inicioUsufrutoValidator(control: AbstractControl): {[key: string]: any} | null {
        const mensagem = [];
        if (control.parent) {
            let dia = 0;
            let mes = 0;
            let ano = 0;
            dia = Number(control.value.split('/')[0]);
            mes = Number(control.value.split('/')[1]) - 1;
            ano = Number(control.value.split('/')[2]);
            const inicioUsufruto: Date = new Date(ano, mes, dia);
            let val: Number[] = control.parent.get('fimPeriodoAquisitivo').value.split('-');
            const fimPeriodoAquisitivo: Date = new Date(Number(val[0]), Number(val[1]) - 1, Number(val[2]));
            val = control.parent.get('inicioPeriodoAquisitivo').value.split('-');
            const inicioPeriodoAquisitivo: Date = new Date(Number(val[0]), Number(val[1]) - 1, Number(val[2]));
            if (control.parent.get('existeCalculoAnterior').value === true) {
                if (inicioUsufruto <= fimPeriodoAquisitivo) {
                    mensagem.push('A Data de início do usufruto deve ser maior que a data fim do período aquisitivo !');
                }
            } else {
                if (inicioUsufruto <= inicioPeriodoAquisitivo) {
                    mensagem.push('A Data de início do usufruto deve ser maior que a data de  início do período aquisitivo !');
                }
            }
            if (control.touched || control.dirty) {
                if (control.parent.get('fimFerias').touched || control.parent.get('fimFerias').dirty) {
                    control.parent.get('diasVendidos').updateValueAndValidity();
                }
                if (control.valid && control.parent.get('fimFerias')) {
                    control.parent.get('diasVendidos').updateValueAndValidity();
                }
            }
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
    closeModal1() {
        this.modalActions.emit({action: 'modal', params: ['close']});
    }
    openModal1() {
        this.modalActions.emit({action: 'modal', params: ['open']});
    }
    openModal2() {
        this.modalActions2.emit({action: 'modal', params: ['open']});
    }
    closeModal2() {
        this.modalActions2.emit({action: 'modal', params: ['close']});
        console.log(this.feriasResgate.get('calcularTerceirizados').get('0' ));
    }
    openModal3() {
        this.modalActions3.emit({action: 'modal', params: ['open']});
    }
    closeModal3() {
        this.modalActions3.emit({action: 'modal', params: ['close']});
    }
    openModal4() {
        this.modalActions4.emit({action: 'modal', params: ['open']});
    }
    closeModal4() {
        this.modalActions4.emit({action: 'modal', params: ['close']});
        this.navegaParaViewDeCalculos.emit(this.codigoContrato);
    }
    protected encapsulaDatas(value: any, operacao: boolean): Date {
        if (operacao) {
            const a = value.split['/'];
            const dia = Number(a[0]);
            const mes = Number(a[1]) - 1;
            const ano = Number(a[2]);
            return new Date(ano, mes, dia);
        }else {
            return value as Date;
        }
    }
    efetuarCalculo(): void {
        this.feriasService.calculaFeriasTerceirizados(this.feriasCalcular).subscribe(res => {
            if (res.success) {
                this.closeModal3();
                this.openModal4();
            }
        });
    }
    verificaDadosFormularioResgate() {
        let aux = 0;
        for (let i = 0; i < this.terceirizados.length; i++) {
            if (this.feriasResgate.get('calcularTerceirizados').get('' + i).get('selected').value) {
                aux++;
                if (this.feriasResgate.get('calcularTerceirizados').get('' + i).status === 'VALID') {
                    const objeto = new FeriasCalcular(this.feriasResgate.get('calcularTerceirizados').get('' + i).get('codTerceirizadoContrato').value,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('tipoRestituicao').value,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('diasVendidos').value,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioFerias').value,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimFerias').value,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioPeriodoAquisitivo').value,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimPeriodoAquisitivo').value,
                        0,
                        this.feriasResgate.get('calcularTerceirizados').get('' + i).get('parcelas').value, 0, 0, 0, 0, 0);
                    if (this.terceirizados[i].valorRestituicaoFerias) {
                        objeto.setInicioPeriodoAquisitivo(this.terceirizados[i].valorRestituicaoFerias.inicioPeriodoAquisitivo);
                        objeto.setFimPeriodoAquisitivo(this.terceirizados[i].valorRestituicaoFerias.fimPeriodoAquisitivo);
                    }
                    let index = -1;
                    for (let j = 0; j < this.feriasCalcular.length; j++) {
                        if (this.feriasCalcular[j].codTerceirizadoContrato === this.feriasResgate.get('calcularTerceirizados').get('' + i).get('codTerceirizadoContrato').value) {
                            index = j;
                        }
                    }
                    objeto.setNomeTerceirizado(this.terceirizados[i].nomeTerceirizado);
                    if (index === -1) {
                        this.feriasCalcular.push(objeto);
                    } else {
                        this.feriasCalcular.splice(index, 1);
                        this.feriasCalcular.push(objeto);
                    }
                }else {
                    this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioFerias').markAsTouched();
                    this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioFerias').markAsDirty();
                    this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimFerias').markAsTouched();
                    this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimFerias').markAsDirty();
                    this.feriasResgate.get('calcularTerceirizados').get('' + i).get('diasVendidos').markAsTouched();
                    this.feriasResgate.get('calcularTerceirizados').get('' + i).get('diasVendidos').markAsDirty();
                    aux = null;
                    this.openModal2();
                }
            }
        }
        if (aux === 0) {
            this.openModal1();
            /* for (let i = 0; i < this.terceirizados.length; i++) {
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioFerias').markAsTouched();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('inicioFerias').markAsDirty();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimFerias').markAsTouched();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('fimFerias').markAsDirty();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('diasVendidos').markAsTouched();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('diasVendidos').markAsDirty();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('valorMovimentado').markAsTouched();
                this.feriasResgate.get('calcularTerceirizados').get('' + i).get('valorMovimentado').markAsDirty();
            } */
        }
        if ((this.feriasCalcular.length > 0) && aux) {
            this.diasConcedidos = [];
            for (let i = 0; i < this.feriasCalcular.length; i++) {
                this.getDiasConcedidos(this.feriasCalcular[i].inicioFerias, this.feriasCalcular[i].fimFerias, this.feriasCalcular[i].diasVendidos, i);
                this.feriasService.getValoresFeriasTerceirizado(this.feriasCalcular[i]).subscribe(res => {
                    if (!res.error) {
                        this.terceirizados.forEach(terceirizado => {
                            if (terceirizado.codigoTerceirizadoContrato === this.feriasCalcular[i].codTerceirizadoContrato) {
                                terceirizado.valorRestituicaoFerias = res;
                                this.feriasCalcular[i].pTotalFerias = terceirizado.valorRestituicaoFerias.valorFerias;
                                this.feriasCalcular[i].pTotalTercoConstitucional = terceirizado.valorRestituicaoFerias.valorTercoConstitucional;
                                this.feriasCalcular[i].pTotalIncidenciaFerias = terceirizado.valorRestituicaoFerias.valorIncidenciaFerias;
                                this.feriasCalcular[i].pTotalIncidenciaTerco = terceirizado.valorRestituicaoFerias.valorIncidenciaTercoConstitucional;
                                this.feriasCalcular[i].inicioPeriodoAquisitivo = terceirizado.valorRestituicaoFerias.inicioPeriodoAquisitivo;
                                this.feriasCalcular[i].fimPeriodoAquisitivo = terceirizado.valorRestituicaoFerias.fimPeriodoAquisitivo;
                                if (i === (this.feriasCalcular.length - 1)) {
                                    this.openModal3();
                                }
                            }
                        });
                    }
                });
            }
        }
    }
    getDiasConcedidos(inicioFerias, fimFerias, diasVendidos, indice) {
        let dia = inicioFerias.split('/')[0];
        let mes = inicioFerias.split('/')[1] - 1;
        let ano = inicioFerias.split('/')[2];
        const initDate = new Date(ano, mes , dia);
        dia = fimFerias.split('/')[0];
        mes = fimFerias.split('/')[1] - 1;
        ano = fimFerias.split('/')[2];
        const finalDate = new Date(ano, mes, dia);
        const diffTime  = Math.abs(finalDate.getTime() - initDate.getTime());
        const diffDay = Math.round(diffTime / (1000 * 3600 * 24)) + 1;
        this.diasConcedidos[indice] = diffDay + diasVendidos;
        console.log(this.diasConcedidos);
    }
}
