import {ChangeDetectorRef, Component, EventEmitter, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {FuncionariosService} from '../funcionarios.service';
import {MaterializeAction} from 'angular2-materialize';
import {Funcionario} from '../funcionario';
import * as XLSX from 'xlsx';

@Component({
    selector: 'app-cadastrar-terceirizado-component',
    templateUrl: './cadastrar-terceirizado.component.html',
    styleUrls: ['./cadastrar-terceirizado.component.scss']
})
export class CadastrarTerceirizadoComponent implements OnInit {
    id: number;
    terceirizadoForm: FormGroup;
    editaTerceirizadoForm: FormGroup;
    modalActions = new EventEmitter<string | MaterializeAction>();
    modalActions2 = new EventEmitter<string | MaterializeAction>();
    modalActions3 = new EventEmitter<string | MaterializeAction>();
    modalActions4 = new EventEmitter<string | MaterializeAction>();
    modalActions5 = new EventEmitter<string | MaterializeAction>();
    opcao: number;
    buttonDisabled = true;
    file: File;
    listaTerceirizados: Funcionario[];
    funcionario: Funcionario;
    cpf: string;
    salvarButtonDisabled = true;
    terceirizadosPlanilhaForm: FormGroup;
    constructor(private fb: FormBuilder, private  terceirizadoService: FuncionariosService, private  route: ActivatedRoute, private router: Router, private ref: ChangeDetectorRef) {
        this.route.params.subscribe(params => {
            console.log(params);
          if (!isNaN(params['id'])) {
            this.id = params['id'];
          }
          if (this.id) {
              this.terceirizadoService.getTerceirizado(this.id).subscribe(res2 => {
                  this.funcionario = res2;
                  this.cpf = this.funcionario.cpf;
                  this.mascararCPF();
                  this.editaTerceirizadoForm.get('nomeTerceirizado').setValue(this.funcionario.nome);
                  //  this.editaTerceirizadoForm.get('cpf').setValue(this.funcionario.cpf);
                  this.editaTerceirizadoForm.get('ativo').setValue(this.funcionario.ativo);
              });
           }
        });

    }
    ngOnInit() {
        this.terceirizadoForm = this.fb.group({
            terceirizados: this.fb.array([this.createTerceirizado()])
        });
        if (this.id) {
            this.editaTerceirizadoForm = this.fb.group({
                nomeTerceirizado: new FormControl('', [Validators.required]),
                cpf: new FormControl('', [Validators.required]),
                ativo: new FormControl('', [Validators.required])
            });
            if (this.funcionario) {
                this.editaTerceirizadoForm.get('nomeTerceirizado').setValue(this.funcionario.nome);
                this.mascararCPF();
                this.editaTerceirizadoForm.get('ativo').setValue(this.funcionario.ativo);
            }
            this.ref.markForCheck();
            this.ref.detectChanges();
        }
    }
    createTerceirizado(): FormGroup {
        return this.fb.group({
            nomeTerceirizado: new FormControl('', [Validators.required]),
            cpf: new FormControl('', [Validators.required]),
            ativo: new FormControl('S', [Validators.required])
        });
    }
    get terceirizados(): FormArray {
        return this.terceirizadoForm.get('terceirizados') as FormArray;
    }
    get terceirizadosPlanilha(): FormArray {
        return this.terceirizadosPlanilhaForm.get('terceirizados') as FormArray;
    }
    adicionaTerceirizadoForm(): void {
        this.terceirizados.push(this.fb.group({
            nomeTerceirizado: new FormControl('', [Validators.required]),
            cpf: new FormControl('', [Validators.required]),
            ativo: new FormControl('S', [Validators.required])
        }));
    }
    removeTerceirizado(i: number) {
        const control = <FormArray>this.terceirizadoForm.get('terceirizados');
        control.removeAt(i);
    }
    onChange(value: number) {
        this.opcao = value;
    }
    openModal() {
        this.modalActions.emit({action: 'modal', params: ['open']});
    }
    closeModal() {
        this.modalActions.emit({action: 'modal', params: ['close']});
        this.terceirizadoForm.get('nomeTerceirizado').setValue('');
        this.terceirizadoForm.get('nomeTerceirizado').reset();
        this.terceirizadoForm.get('cpf').setValue('');
        this.terceirizadoForm.get('cpf').reset();
        this.terceirizadoForm.get('ativo').reset();
        this.terceirizadoForm.reset();
    }
    openModal2() {
        this.modalActions2.emit({action: 'modal', params: ['open']});
    }
    closeModal2() {
        this.modalActions2.emit({action: 'modal', params: ['close']});
        window.location.reload();
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
    sobeArquivo(event: any) {
        if (event.srcElement.files[0]) {
            if (event.srcElement.files[0].name === 'modelo-cadastro-terceirizados.xlsx') {
                this.file = event.srcElement.files[0];
                this.buttonDisabled = false;
            }else {
                this.file = null;
                this.buttonDisabled = true;
            }
        }
    }
    uploadData() {
        this.buttonDisabled = true;
        this.listaTerceirizados = [];
        if (this.file) {
            const fileReader = new FileReader();
            fileReader.onload = (e: any) => {
                const bstr: string = e.target.result;
                const wb: XLSX.WorkBook = XLSX.read(bstr, {type: 'binary'});

                /* grab first sheet */
                const wsname: string = wb.SheetNames[0];
                const ws: XLSX.WorkSheet = wb.Sheets[wsname];

                /* save data */
                const data = (XLSX.utils.sheet_to_json(ws, {header: 1}));
                data.forEach((result: any) => {
                    if (result[0]) {
                        const funcionario = new Funcionario();
                        funcionario.nome = result[0];
                        funcionario.cpf = result[1];
                        funcionario.ativo = result[2];
                        if (funcionario.ativo.toUpperCase() === 'SIM') {
                           funcionario.ativo = 'S';
                        }else {
                            funcionario.ativo = 'N';
                        }
                        this.listaTerceirizados.push(funcionario);
                    }
                });
                this.listaTerceirizados.splice(0, 1);
                if (this.listaTerceirizados.length > 0) {
                   this.terceirizadosPlanilhaForm = this.fb.group({
                       terceirizados: this.fb.array([])
                   });
                   const formArray = this.terceirizadosPlanilhaForm.get('terceirizados') as FormArray;
                   this.listaTerceirizados.forEach(terceirizado => {
                       formArray.push(this.fb.group({
                           nomeTerceirizado: new FormControl(terceirizado.nome, [Validators.required]),
                           cpf: new FormControl(terceirizado.cpf, [Validators.required]),
                           ativo: new FormControl(terceirizado.ativo, [Validators.required])
                       }));
                   });
                   this.terceirizadosPlanilhaForm.updateValueAndValidity();
                   this.ref.markForCheck();
                }
            };
            fileReader.readAsBinaryString(this.file);
        }
    }
    cadastroTerceirizado() {
        if (this.terceirizadoForm.valid) {
                    this.listaTerceirizados = [];
                    for (const control of this.terceirizados.controls) {
                        const funcionario = new Funcionario();
                        funcionario.nome = control.get('nomeTerceirizado').value;
                        funcionario.cpf = control.get('cpf').value;
                        funcionario.ativo = control.get('ativo').value;
                        this.listaTerceirizados.push(funcionario);
                    }
                    if (this.listaTerceirizados.length === 1) {
                        this.terceirizadoService.cadastraTerceirizado(this.listaTerceirizados[0]).subscribe(res => {
                            if (res.success) {
                                this.openModal();
                            }else {
                                this.openModal2();
                            }
                        });
                    }else {
                        this.terceirizadoService.cadastraTerceirizados(this.listaTerceirizados).subscribe(res => {
                            if (res.success) {
                                this.openModal();
                            }else {
                                this.openModal2();
                            }
                        });
            }
        }
    }
    cadastroTerceirizadosPlanilha() {
        if (this.terceirizadosPlanilhaForm.valid) {
            this.listaTerceirizados = [];
            for (const control of this.terceirizadosPlanilha.controls) {
                const funcionario = new Funcionario();
                funcionario.nome = control.get('nomeTerceirizado').value;
                funcionario.cpf = this.unmask(control.get('cpf').value);
                funcionario.ativo = control.get('ativo').value;
                this.listaTerceirizados.push(funcionario);
            }
            if (this.listaTerceirizados.length === 1) {
                this.terceirizadoService.cadastraTerceirizado(this.listaTerceirizados[0]).subscribe(res => {
                    if (res.success) {
                        this.openModal();
                    }else {
                        this.openModal2();
                    }
                });
            }else {
                this.terceirizadoService.cadastraTerceirizados(this.listaTerceirizados).subscribe(res => {
                    if (res.success) {
                        this.openModal();
                    }else {
                        this.openModal2();
                    }
                });
            }
        }
    }
    private mascararCPF() {
        let value = '';
        for (let i = 0; i < this.funcionario.cpf.length; i++) {
            if (i === 9) {
               value = value + '-';
            }
            if ((i === 6) || (i === 3)) {
                value = value + '.';
            }
            value = value + this.funcionario.cpf[i];
        }
        this.editaTerceirizadoForm.get('cpf').setValue(value);
    }
    activateButton() {
        const cpfUnmasked = this.unmask(this.editaTerceirizadoForm.get('cpf').value);
        this.editaTerceirizadoForm.get('cpf').updateValueAndValidity();
        this.editaTerceirizadoForm.get('nomeTerceirizado').updateValueAndValidity();
        this.editaTerceirizadoForm.get('ativo').updateValueAndValidity();
        this.editaTerceirizadoForm.get('cpf').updateValueAndValidity();
        if (this.id) {
           if ((this.editaTerceirizadoForm.get('nomeTerceirizado').value !== this.funcionario.nome ||
                cpfUnmasked !== this.cpf ||
                this.editaTerceirizadoForm.get('ativo').value !== this.funcionario.ativo)) {
               this.salvarButtonDisabled = false;
           }else {
               this.salvarButtonDisabled = true;
           }
        }
    }
    voltar() {
        if (this.editaTerceirizadoForm.get('nomeTerceirizado').value !== this.funcionario.nome ||
            this.unmask(this.editaTerceirizadoForm.get('cpf').value) !== this.funcionario.cpf ||
            this.editaTerceirizadoForm.get('ativo').value !== this.funcionario.ativo) {
            this.openModal3();
        }else {
            this.navTer();
        }
    }
    navTer() {
        this.closeModal3();
        this.closeModal4();
        this.closeModal5();
        this.modalActions.emit({action: 'modal', params: ['close']});
        this.router.navigate(['terceirizados']);
    }
    salvarAlteracoes() {
        if (this.editaTerceirizadoForm.valid) {
            const terceirizado: Funcionario = new Funcionario();
            terceirizado.nome = this.editaTerceirizadoForm.get('nomeTerceirizado').value;
            terceirizado.cpf = this.unmask(this.editaTerceirizadoForm.get('cpf').value);
            terceirizado.ativo = this.editaTerceirizadoForm.get('ativo').value;
            terceirizado.codigo = this.funcionario.codigo;
            this.terceirizadoService.updateTerceirizado(terceirizado).subscribe(res => {
                if (res.success) {
                   this.openModal4();
                }else {
                    this.openModal5();
                }
            });
        }
    }

    private unmask(cpf: string): string {
        let partes: string[] = cpf.split('.');
        let value = '';
        for (const parte  of partes) {
            value = value + parte;
        }
        partes = value.split('-');
        value = '';
        for (const parte of partes) {
            value = value +  parte;
        }
        return value;
    }
}
