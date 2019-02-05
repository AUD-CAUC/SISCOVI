import {Component} from '@angular/core';
import {ContratosService} from '../../contratos/contratos.service';
import {Contrato} from '../../contratos/contrato';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
    selector: 'app-cadastro-percentuais',
    templateUrl: './cadastro-percentuais.component.html',
    styleUrls: ['./cadastro-percentuais.component.scss']
})
export class CadastroPercentuaisComponent {
    contratos: Contrato[] = [];
    percentuaisForm: FormGroup;

    constructor(contraServ: ContratosService, fb: FormBuilder, ) {
        contraServ.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
        });
       this.initPercentuaisForm(fb);
    }
    initPercentuaisForm(fb: FormBuilder) {
        this.percentuaisForm = fb.group({
            contrato: new FormControl('', Validators.required),
            dataInicio: new FormControl('', [Validators.required, Validators.maxLength(10),
                Validators.minLength(10), this.myDateValidator]),
            dataFim: new FormControl('', [Validators.required, Validators.minLength(10), Validators.maxLength(10), this.myDateValidator]),
            percentualFerias: new FormControl('', Validators.required),
            percentualDecTer: new FormControl('', Validators.required),
            percentualInc: new FormControl('', Validators.required)
        });
    }
    public myDateValidator(control: AbstractControl): {[key: string]: any} {
        const val = control.value;
        const mensagem = [];
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
            if (val.length === 10) {
                const regex = new RegExp(/^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/);
                if (!regex.test(val)) {
                    mensagem.push('A data digitada é inválida');
                }
            }
        }
        return (mensagem.length > 0) ? {'mensagem': [mensagem]} : null;
    }
}
