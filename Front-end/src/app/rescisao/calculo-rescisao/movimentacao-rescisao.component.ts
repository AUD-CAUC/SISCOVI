import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TerceirizadoRescisao} from '../terceirizado-rescisao';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MaterializeAction} from 'angular2-materialize';
import {RescisaoService} from '../rescisao.service';
import {CalculoRescisao} from '../calculo-rescisao';
import {Observable} from 'rxjs/Observable';
import {map} from 'rxjs/operators';
import 'rxjs/add/observable/of';

@Component({
    selector: 'app-movimentacao-rescisao-component',
    templateUrl: './movimentacao-rescisao.component.html',
    styleUrls: ['./calculo-rescisao.component.scss']
})
export  class MovimentacaoRescisaoComponent implements  OnInit {
    @Input() protected terceirizados: TerceirizadoRescisao[];
    @Input() codigoContrato: number;
    @Input() tipoRestituicao: string;
    rescisaoForm: FormGroup;
    isSelected = false;
    selected = false;
    terceirizadosCalculosRescisao: TerceirizadoRescisao[] = [];
    calculosRescisao: CalculoRescisao[] = [];
    modalActions = new EventEmitter<string | MaterializeAction>();
    modalActions2 = new EventEmitter<string | MaterializeAction>();
    modalActions3 = new EventEmitter<string | MaterializeAction>();
    modalActions4 = new EventEmitter<string | MaterializeAction>();
    vmsm = false;
    protected diasConcedidos: number[] = [];
    @Output() navegaParaViewDeCalculos = new EventEmitter();
    constructor(private fb: FormBuilder, private rescisaoService: RescisaoService) { }
    ngOnInit() {
        this.formInit();
    }
    formInit(): void {
        this.rescisaoForm = this.fb.group({
            calcularTerceirizados: this.fb.array([])
        });
        const control = <FormArray>this.rescisaoForm.controls.calcularTerceirizados;
        this.terceirizados.forEach(item => {
            const addCtrl = this.fb.group({
                codTerceirizadoContrato: new FormControl(item.codTerceirizadoContrato),
                nomeTerceirizado: new FormControl(item.nomeTerceirizado),
                tipoRescisao: new FormControl(),
                selected: new FormControl(this.isSelected),
                tipoRestituicao: new FormControl(this.tipoRestituicao),
                dataDesligamento: new FormControl(),
                dataInicio: new FormControl(),
                dataFim: new FormControl(),
                valorAMovimentar: new FormControl(0)
            });
            control.push(addCtrl);
        });
        for (let i = 0; i < this.terceirizados.length; i++) {
            this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('codTerceirizadoContrato').setValidators(Validators.required);
            this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('tipoRescisao').setValidators(Validators.required);
            this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('tipoRescisao').setValue(0);
            this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('tipoRestituicao').setValidators(Validators.required);
            this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('dataDesligamento');
        }
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
    }
    openModal3() {
        this.vmsm = false;
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
    efetuarCalculo(): void {
        this.rescisaoService.registrarCalculoRescisao(this.terceirizadosCalculosRescisao).subscribe(res => {
            if (res.success) {
                this.closeModal3();
                this.openModal4();
            }
        });
    }
    verificaDadosFormulario() {
        let aux = 0;
        this.vmsm = false;
        for (let i = 0; i < this.terceirizados.length; i++) {
            if (this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('selected').value) {
                aux++;
                if (this.rescisaoForm.get('calcularTerceirizados').get('' + i).status === 'VALID') {
                    const objeto = new TerceirizadoRescisao(this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('codTerceirizadoContrato').value,
                        this.terceirizados[i].nomeTerceirizado,
                        this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('dataDesligamento').value,
                        this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('tipoRescisao').value,
                        this.tipoRestituicao);
                    let index = -1;
                    for (let j = 0; j < this.terceirizadosCalculosRescisao.length; j++) {
                        if (this.terceirizadosCalculosRescisao[j].codTerceirizadoContrato === this.rescisaoForm.get('calcularTerceirizados').get('' + i).get('codTerceirizadoContrato').value) {
                            index = j;
                        }
                    }
                    if (index === -1) {
                        this.terceirizadosCalculosRescisao.push(objeto);
                    } else {
                        this.terceirizadosCalculosRescisao.splice(index, 1);
                        this.terceirizadosCalculosRescisao.push(objeto);
                    }
                }else {
                    aux = undefined;
                    this.openModal2();
                }
            }
        }
        if (aux === 0) {
            this.openModal1();
        }
        if ((this.terceirizadosCalculosRescisao.length > 0) && aux) {
            this.diasConcedidos = [];
            this.rescisaoService.calculaRescisaoTerceirizados(this.terceirizadosCalculosRescisao).subscribe(res => {
                if (!res.error) {
                    this.calculosRescisao = res;
                    this.openModal3();
                    this.vmsm = true;
                }
            });
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
    /*public valorMovimentadoValidator(control: AbstractControl) {
        const mensagem: string[] = [];
        if (control.value <= 0) {
            mensagem.push('O valor a ser movimentado deve ser maior que zero !');
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
            if (fimUsufruto && inicioUsufruto) {
                if (control.parent.get('fimFerias').valid && control.parent.get('inicioFerias').valid) {
                    const feriasTemp = new TerceirizadoRescisao(control.parent.get('codTerceirizadoContrato').value,
                        control.parent.get('nomeTerceirizado').value,
                        control.parent.get('dataDesligamento').value,
                        control.parent.get('tipoRescisao').value,
                        control.parent.get('tipoRestituicao').value)
                    const index = this.terceirizados.findIndex( x => x.codTerceirizadoContrato === Number(control.parent.get('codTerceirizadoContrato').value) );
                    this.rescisaoService.getValores(feriasTemp).subscribe(res => {
                        if (!res.error) {
                            this.terceirizados.forEach(terceirizado => {
                                if (terceirizado.codigoTerceirizadoContrato === control.parent.get('codTerceirizadoContrato').value) {
                                    terceirizado.valorRestituicaoFerias = res;
                                    control.parent.get('valorMaximoASerMovimentado').setValue(terceirizado.valorRestituicaoFerias.valorFerias + terceirizado.valorRestituicaoFerias.valorTercoConstitucional);
                                    this.vmsm = true;
                                }
                            });
                        } else {
                            const error: string = res.error;
                            mensagem.push(error);
                        }
                    });
                }
            }
            if (control.value && this.vmsm && control.parent.get('valorMaximoASerMovimentado').value ) {
                if (control.value > (control.parent.get('valorMaximoASerMovimentado').value)) {
                    mensagem.push('O valor disponível para movimentação é : R$' + String(control.parent.get('valorMaximoASerMovimentado').value).replace('.', ',') + ' !');
                }
            }
        }
        // return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
        return Observable.of((mensagem.length > 0 ) ? mensagem : null).pipe(
            map(result => (mensagem.length > 0) ? {'mensagem': mensagem} : null)
        );
    }
    */
}
