import {Component, EventEmitter, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Cargo} from '../cargo';
import {CargoService} from '../cargo.service';
import * as XLSX from 'xlsx';

@Component({
    selector: 'app-cadastro-cargos',
    templateUrl: './cadastro-cargos.component.html',
    styleUrls: ['./cadastro-cargos.component.scss']
})
export class CadastroCargosComponent implements OnInit {
    cargosForm: FormGroup;
    opcao = 0;
    file: File;
    listaCargos: Cargo[] = [];
    buttonDisabled = true;
    cargoService: CargoService;
    constructor(private formBuilder: FormBuilder, cargoService: CargoService) {
        this.cargoService = cargoService;
        cargoService.cargos = this.listaCargos;
    }
    ngOnInit() {
        this.cargosForm = this.formBuilder.group({
            cargos: this.formBuilder.array([this.createCargo()])
        });
    }
    createCargo(): FormGroup {
        return this.formBuilder.group({
            nome: new FormControl('', [Validators.required]),
            descricao: new FormControl('', [Validators.required])
        });
    }
    get cargos(): FormArray {
        return this.cargosForm.get('cargos') as FormArray;
    }
    adicionaCargo(): void {
        this.cargos.push(this.formBuilder.group({
            nome: new FormControl('', [Validators.required]),
            descricao: new FormControl('', [Validators.required]),
        }));
    }
    removeCargo(i: number) {
        const control = <FormArray>this.cargosForm.get('cargos');
        control.removeAt(i);
    }
    uploadData() {
        this.cargoService.loading = true;
        this.buttonDisabled = true;
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
                       const cargo = new Cargo();
                       cargo.nome = result[0];
                       if (result.length > 1) {
                           cargo.descricao = result[1];
                       }else {
                           cargo.descricao = '-';
                       }
                       this.listaCargos.push(cargo);
                   }
                });
                this.listaCargos.splice(0, 1);
                this.cargoService.cargos = this.listaCargos;
            };
            fileReader.readAsBinaryString(this.file);
        }
        this.cargoService.displayCargos = true;
        this.cargoService.enabled = true;
    }
    uploadFile(event) {
        this.cargoService.displayCargos = false;
        if (event.srcElement.files[0]) {
            if (event.srcElement.files[0].name === 'modelo-cadastro-cargos.xlsx') {
                this.file = event.srcElement.files[0];
                this.buttonDisabled = false;
            }else {
                this.file = null;
                this.buttonDisabled = true;
                this.cargoService.displayCargos = false;
            }
        }
    }
}
