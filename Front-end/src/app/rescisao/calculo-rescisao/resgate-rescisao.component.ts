import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MaterializeAction} from 'angular2-materialize';
import {TerceirizadoRescisao} from '../terceirizado-rescisao';
import {RescisaoService} from '../rescisao.service';
import {CalculoRescisao} from '../calculo-rescisao';

@Component({
    selector: 'app-resgate-rescisao-component',
    templateUrl: './resgate-rescisao.component.html',
    styleUrls: ['./calculo-rescisao.component.scss']
})
export class ResgateRescisaoComponent implements OnInit {
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
                tipoRescisao: new FormControl(),
                selected: new FormControl(this.isSelected),
                tipoRestituicao: new FormControl(this.tipoRestituicao),
                dataDesligamento: new FormControl(),
                dataInicio: new FormControl(),
                dataFim: new FormControl()
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
                } else {
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
}
