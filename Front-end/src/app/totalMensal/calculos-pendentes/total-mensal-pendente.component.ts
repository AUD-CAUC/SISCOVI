import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Contrato} from '../../contratos/contrato';
import {TotalMensalService} from '../total-mensal.service';
import {TotalMensalPendente} from '../total-mensal-pendente';
import {ContratosService} from '../../contratos/contratos.service';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {ConfigService} from '../../_shared/config.service';
import {ListaTotalMensalData} from '../lista-total-mensal-data';
import {MaterializeAction} from 'angular2-materialize';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

@Component({
    selector: 'app-total-mensal-pendente-component',
    templateUrl: './total-mensal-pendente.component.html',
    styleUrls: ['./total-mensal-pendente.component.scss']
})
export class TotalMensalPendenteComponent implements OnInit {
    @Input() codigoContrato: number;
    contratos: Contrato[];
    totais: TotalMensalPendente[] = [];
    totalMensalForm: FormGroup;
    totalMennsalFormAfter: FormGroup;
    config: ConfigService;
    isSelected = false;
    totaisAvaliados: TotalMensalPendente[] = [];
    modalActions = new EventEmitter<string | MaterializeAction>();
    modalActions2 = new EventEmitter<string| MaterializeAction>();
    modalActions3 = new EventEmitter<string | MaterializeAction>();
    modalActions4 = new EventEmitter<string | MaterializeAction>();
    @Output() nav = new EventEmitter();
    historicoPendente: TotalMensalPendente[] = [];
    notifications: number;
    constructor (private contratoService: ContratosService, private totalMensalService: TotalMensalService, private fb: FormBuilder, config: ConfigService, private router: Router, private ref: ChangeDetectorRef) {
        this.config = config;
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
            if (this.totais) {
                this.formInit();
            }
            if (this.codigoContrato) {
                this.totalMensalService.getTotaisPendentes(this.codigoContrato).subscribe(res2 => {
                    const historico: TotalMensalPendente[] = [];
                    if (!res.error) {
                        this.totais = res2;
                    }
                    if (this.totais) {
                        for (let i = 0; i < res.length; i++) {
                            if (res[i].status === 'NEGADO') {
                                const a: any =  res.slice(i, i + 1);
                                const val: TotalMensalPendente = a[0];
                                historico.push(val as TotalMensalPendente);
                                res.splice(i, 1);
                                i = -1;
                            }
                        }
                        this.historicoPendente = historico;
                        this.notifications = this.historicoPendente.length;
                        this.ref.markForCheck();
                        this.formInit();
                    }
                });
            }
        });
    }
    ngOnInit() {
        if (this.totais.length > 0) {
            if (this.totais.length > 0) {
                const historico: TotalMensalPendente[] = [];
                for (let i = 0; i < this.totais.length; i++) {
                    if (this.totais[i].status === 'NEGADO') {
                        const a: any =  this.totais.slice(i, i + 1);
                        const val: TotalMensalPendente = a[0];
                        historico.push(val as TotalMensalPendente);
                        this.totais.splice(i, 1);
                    }
                }
                this.historicoPendente = historico;
                this.notifications = this.historicoPendente.length;
                this.ref.markForCheck();
            }
        }
        this.formInit();
    }
    formInit() {
            this.totalMensalForm = this.fb.group({
                avaliacaoDeCalculo: this.fb.array([])
            });
            if (this.totais) {
                if (this.totais.length > 0) {
                    const control = <FormArray>this.totalMensalForm.controls.avaliacaoDeCalculo;
                    this.totais.forEach(item => {
                        const addCtrl = this.fb.group({
                            avaliacao: new FormControl('S'),
                            selected: new FormControl(false),
                            dataReferencia: new FormControl(item.totaisMensais.dataReferencia)
                        });
                        control.push(addCtrl);
                    });
                }
            }
            this.totalMennsalFormAfter = this.fb.group({
                calculosAvaliados: this.fb.array([])
            });
    }
    defineCodigoContrato(codigo: number) {
        this.historicoPendente = [];
        this.codigoContrato = codigo;
        if (this.totais.length > 0) {
            this.totais = [];
        }
        this.totalMensalService.getTotaisPendentes(codigo).subscribe(res => {
            const historico: TotalMensalPendente[] = [];
            if (!res.error) {
                for (let i = 0; i < res.length; i++) {
                    if (res[i].status === 'NEGADO') {
                        const a: any =  res.slice(i, i + 1);
                        const val: TotalMensalPendente = a[0];
                        historico.push(val as TotalMensalPendente);
                        res.splice(i, 1);
                        i = -1;
                    }
                }
                this.historicoPendente = historico;
                this.totais = res;
            }
            this.notifications = this.historicoPendente.length;
            if (this.formInit()) {
                this.formInit();
            }
        });
    }
    openModal() {
        this.modalActions.emit({action: 'modal', params: ['open']});
    }
    closeModal() {
        this.modalActions.emit({action: 'modal', params: ['close']});
    }
    openModal3() {
        this.modalActions3.emit({action: 'modal', params: ['open']});
    }
    closeModal3() {
        this.modalActions3.emit({action: 'modal', params: ['close']});
    } openModal4() {
        this.modalActions4.emit({action: 'modal', params: ['open']});
    }
    closeModal4() {
        this.modalActions4.emit({action: 'modal', params: ['close']});
    }
    openModal2() {
        this.modalActions2.emit({action: 'modal', params: ['open']});
        if (this.totaisAvaliados) {
            const control = <FormArray>this.totalMennsalFormAfter.controls.calculosAvaliados;
            this.totaisAvaliados.forEach(item => {
                const addCtrl = this.fb.group({
                    status: new FormControl(item.status),
                    dataReferencia: new FormControl(item.totaisMensais.dataReferencia),
                    observacoes: new FormControl()
                });
                control.push(addCtrl);
            });
        }
    }
    closeModal2() {
        this.modalActions2.emit({action: 'modal', params: ['close']});
    }
    verificaFormulario(): void {
        let aux = 0;
        if (this.totaisAvaliados) {
            if (this.totaisAvaliados.length > 0) {
               this.totaisAvaliados = [];
            }
        }
        for (let i = 0; i < this.totais.length; i++) {
            if (this.totalMensalForm.get('avaliacaoDeCalculo').get('' + i).get('selected').value) {
                aux++;
                const listaTotalMensalData = new ListaTotalMensalData(this.totalMensalForm.get('avaliacaoDeCalculo').get('' + i).get('dataReferencia').value, this.totais[i].totaisMensais.totais);
                const objeto = new TotalMensalPendente(listaTotalMensalData, this.totalMensalForm.get('avaliacaoDeCalculo').get('' + i).get('avaliacao').value);
                this.totaisAvaliados.push(objeto);
            }
        }
        if (aux !== 0) {
            this.openModal2();
        }else {
            this.openModal();
        }
    }
    enviarAvaliacao() {
        for (let i = 0; i < this.totaisAvaliados.length; i++) {
           this.totaisAvaliados[i].observacoes =  this.totalMennsalFormAfter.get('calculosAvaliados').get('' + i).get('observacoes').value;
        }
        this.totalMensalService.enviarAvaliacaoCalculosTotalMensal(this.codigoContrato, this.totaisAvaliados).subscribe(res => {
            if (!res.error) {
                if (res.success) {
                    this.openModal3();
                    this.closeModal2();
                }
            }else {
                this.closeModal2();
            }
        });
    }
    navegaViewExec() {
        this.closeModal3();
        this.nav.emit(this.codigoContrato);
    }
    corrigeCalculo(dataReferencia: Date) {
        this.router.navigate(['/totalMensal', this.codigoContrato, dataReferencia], { queryParams: [this.codigoContrato], skipLocationChange: true});
    }
}
