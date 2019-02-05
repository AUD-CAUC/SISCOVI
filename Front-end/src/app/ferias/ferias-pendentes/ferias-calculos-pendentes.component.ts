import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Contrato} from '../../contratos/contrato';
import {FeriasService} from '../ferias.service';
import {ContratosService} from '../../contratos/contratos.service';
import {FeriasCalculosPendentes} from './ferias-calculos-pendentes';
import {ConfigService} from '../../_shared/config.service';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {MaterializeAction} from 'angular2-materialize';

@Component({
    selector: 'app-ferias-calculos-pendentes',
    templateUrl: './ferias-calculos-pendentes.component.html',
    styleUrls: ['./ferias-calculos-pendentes.component.scss']
})
export class FeriasCalculosPendentesComponent implements OnInit {
    contratos: Contrato[];
    @Input() codigoContrato = 0;
    isSelected = false;
    calculosPendentes: FeriasCalculosPendentes[];
    calculosAvaliados: FeriasCalculosPendentes[] = [];
    calculosNegados: FeriasCalculosPendentes[] = [];
    config: ConfigService;
    feriasForm: FormGroup;
    feriasFormAfter: FormGroup;
    modalActions = new EventEmitter<string | MaterializeAction>();
    modalActions2 = new EventEmitter<string | MaterializeAction>();
    modalActions3 = new EventEmitter<string | MaterializeAction>();
    modalActions4 = new EventEmitter<string | MaterializeAction>();
    modalActions5 = new EventEmitter<string | MaterializeAction>();
    notifications: number;
    @Output() nav = new EventEmitter();
    constructor(private feriasService: FeriasService, private contratoService: ContratosService, config: ConfigService, private fb: FormBuilder, private ref: ChangeDetectorRef) {
        this.config = config;
        this.contratoService.getContratosDoUsuario().subscribe(res => {
           this.contratos = res;
           if (this.codigoContrato) {
              this.feriasService.getCalculosPendentes(this.codigoContrato).subscribe(res2 => {
                  this.calculosPendentes = res2;
                  if (this.calculosPendentes.length === 0) {
                      this.calculosPendentes = null;
                  }else {
                      this.formInit();
                  }
              });
               this.feriasService.getCalculosPendentesNegados(this.codigoContrato).subscribe(res3 => {
                   const historico: FeriasCalculosPendentes[] = res3;
                   this.calculosNegados = historico;
                   this.notifications = this.calculosNegados.length;
                   this.ref.markForCheck();
               });
           }
        });
    }
    ngOnInit() {
        this.formInit();
    }
    formInit() {
        if (this.calculosPendentes ) {
            this.feriasForm = this.fb.group({
                avaliacaoCalculoFerias: this.fb.array([])
            });
            if (this.calculosPendentes) {
                const control = <FormArray>this.feriasForm.controls.avaliacaoCalculoFerias;
                this.calculosPendentes.forEach(() => {
                    const addControl = this.fb.group({
                        selected: new FormControl(),
                        avaliacao: new FormControl('S')
                    });
                    control.push(addControl);
                });
            }
            this.ref.markForCheck();
        }
        this.feriasFormAfter = this.fb.group({
           calculosAvaliados: this.fb.array([])
        });
    }
    openModal() {
        this.modalActions.emit({action: 'modal', params: ['open']});
    }
    closeModal() {
        this.modalActions.emit({action: 'modal', params: ['close']});
    }
    openModal2() {
        this.modalActions2.emit({action: 'modal', params: ['open']});
    }
    closeModal2() {
        this.modalActions2.emit({action: 'modal', params: ['close']});
        this.calculosAvaliados = [];
        this.feriasFormAfter = this.fb.group({
            calculosAvaliados: this.fb.array([])
        });
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
    }
    openModal5() {
        this.modalActions5.emit({action: 'modal', params: ['open']});
    }
    closeModal5() {
        this.modalActions5.emit({action: 'modal', params: ['close']});
    }
    defineCodigoContrato(codigoContrato: number): void {
        this.codigoContrato = codigoContrato;
        if (this.codigoContrato) {
            this.feriasService.getCalculosPendentes(this.codigoContrato).subscribe(res2 => {
                this.calculosPendentes = res2;
                if (this.calculosPendentes.length === 0) {
                    this.calculosPendentes = null;
                }else {
                    this.formInit();
                }
            });
            this.feriasService.getCalculosPendentesNegados(this.codigoContrato).subscribe(res3 => {
                const historico: FeriasCalculosPendentes[] = res3;
                this.calculosNegados = historico;
                this.notifications = this.calculosNegados.length;
                this.ref.markForCheck();
            });
        }
    }
    verificaFormulario() {
        let aux = 0;
        for (let i = 0; i < this.calculosPendentes.length; i ++) {
            if (this.feriasForm.get('avaliacaoCalculoFerias').get('' + i).get('selected').value) {
                aux++;
                const temp: FeriasCalculosPendentes = this.calculosPendentes[i];
                temp.status = this.feriasForm.get('avaliacaoCalculoFerias').get('' + i).get('avaliacao').value;
                this.calculosAvaliados.push(temp);
            }
        }
        if (aux === 0) {
            this.openModal();
        }else {
            const control = <FormArray>this.feriasFormAfter.controls.calculosAvaliados;
            this.calculosAvaliados.forEach(() => {
                const addControl = this.fb.group({
                    observacoes: new FormControl(),
                });
                control.push(addControl);
            });
            this.openModal2();
        }
    }
    salvarAlteracoes() {
        for (let i = 0; i < this.calculosAvaliados.length; i++) {
           this.calculosAvaliados[i].observacoes = this.feriasFormAfter.get('calculosAvaliados').get('' + i).get('observacoes').value;
        }
        this.feriasService.salvarFeriasAvaliadas(this.codigoContrato, this.calculosAvaliados).subscribe(res => {
            if (res.success) {
               this.openModal3();
               this.closeModal2();
            }else {
                this.openModal5();
                this.closeModal2();
            }
        });
    }
    navegaViewExec() {
        this.closeModal3();
        this.nav.emit(this.codigoContrato);
    }
}
