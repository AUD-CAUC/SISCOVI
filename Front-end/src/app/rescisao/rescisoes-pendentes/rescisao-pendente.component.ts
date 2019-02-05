import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Contrato} from '../../contratos/contrato';
import {ConfigService} from '../../_shared/config.service';
import {FormArray, FormBuilder, FormGroup, FormControl} from '@angular/forms';
import {MaterializeAction} from 'angular2-materialize';
import {ContratosService} from '../../contratos/contratos.service';
import {RescisaoService} from '../rescisao.service';
import {RescisaoPendente} from '../../decimo_terceiro/decimo-terceiro-pendente/rescisao-pendente';


@Component({
    selector: 'app-rescisao-pendente-component',
    templateUrl: './rescisao-pendente.component.html',
    styleUrls: ['../rescisao.component.scss']
})
export class RescisaoPendenteComponent implements OnInit {
    @Input() codigoContrato: number;
    contratos: Contrato[];
    isSelected = false;
    calculosPendentes: RescisaoPendente[];
    calculosAvaliados: RescisaoPendente[] = [];
    calculosNegados: RescisaoPendente[] = [];
    config: ConfigService;
    decimoTerceiroForm: FormGroup;
    decimoTerceiroFormAfter: FormGroup;
    modalActions = new EventEmitter<string | MaterializeAction>();
    modalActions2 = new EventEmitter<string | MaterializeAction>();
    modalActions3 = new EventEmitter<string | MaterializeAction>();
    modalActions4 = new EventEmitter<string | MaterializeAction>();
    modalActions5 = new EventEmitter<string | MaterializeAction>();
    notifications: number;
    @Output() nav = new EventEmitter();

    constructor(config: ConfigService, private  fb: FormBuilder, private  ref: ChangeDetectorRef, private rescisaoService: RescisaoService, private contratoService: ContratosService) {
        this.config = config;
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
            if (this.codigoContrato) {
                this.rescisaoService.getCalculosPendentes(this.codigoContrato).subscribe(res2 => {
                    this.calculosPendentes = res2;
                    if (this.calculosPendentes.length === 0) {
                        this.calculosPendentes = null;
                    } else {
                        this.formInit();
                    }
                });
            }
        });
        if (this.codigoContrato) {
            this.rescisaoService.getCalculosPendentesNegados(this.codigoContrato).subscribe(res3 => {
                const historico: RescisaoPendente[] = res3;
                this.calculosNegados = historico;
                this.notifications = this.calculosNegados.length;
                this.ref.markForCheck();
            });
        }
    }

    ngOnInit() {
        this.formInit();
    }

    formInit() {
        if (this.calculosPendentes) {
            this.decimoTerceiroForm = this.fb.group({
                avaliacaoCalculoFerias: this.fb.array([])
            });
            if (this.calculosPendentes) {
                const control = <FormArray>this.decimoTerceiroForm.controls.avaliacaoCalculoFerias;
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
        this.decimoTerceiroFormAfter = this.fb.group({
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
        this.decimoTerceiroFormAfter = this.fb.group({
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
            this.rescisaoService.getCalculosPendentes(this.codigoContrato).subscribe(res2 => {
                this.calculosPendentes = res2;
                if (this.calculosPendentes.length === 0) {
                    this.calculosPendentes = null;
                } else {
                    this.formInit();
                }
            });
            this.rescisaoService.getCalculosPendentesNegados(this.codigoContrato).subscribe(res3 => {
                const historico: RescisaoPendente[] = res3;
                this.calculosNegados = historico;
                this.notifications = this.calculosNegados.length;
                this.ref.markForCheck();
            });
        }
    }

    verificaFormulario() {
        let aux = 0;
        for (let i = 0; i < this.calculosPendentes.length; i++) {
            if (this.decimoTerceiroForm.get('avaliacaoCalculoFerias').get('' + i).get('selected').value) {
                aux++;
                const temp: RescisaoPendente = this.calculosPendentes[i];
                temp.status = this.decimoTerceiroForm.get('avaliacaoCalculoFerias').get('' + i).get('avaliacao').value;
                this.calculosAvaliados.push(temp);
            }
        }
        if (aux === 0) {
            this.openModal();
        } else {
            const control = <FormArray>this.decimoTerceiroFormAfter.controls.calculosAvaliados;
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
            this.calculosAvaliados[i].observacoes = this.decimoTerceiroFormAfter.get('calculosAvaliados').get('' + i).get('observacoes').value;
        }
        this.rescisaoService.salvarDecimoTerceiroAvaliados(this.codigoContrato, this.calculosAvaliados).subscribe(res => {
            if (res.success) {
                this.openModal3();
                this.closeModal2();
            } else {
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
