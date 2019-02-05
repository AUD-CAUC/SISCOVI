import {Component, OnInit} from '@angular/core';
import {HistoricoService} from '../historico.service';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ContratosService} from '../../contratos/contratos.service';
import {Contrato} from '../../contratos/contrato';
import {UserService} from '../../users/user.service';
import {Usuario} from '../../usuarios/usuario';
import {Profile} from '../../users/profile';
import {HistoricoGestor} from '../historico-gestor';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-cadastrar-gestor-contrato-component',
    templateUrl: './cadastrar-gestor-contrato.component.html',
    styleUrls: ['./cadastrar-gestor-contrato.component.scss']
})
export class CadastrarGestorContratoComponent implements OnInit {
    gestorContratoForm: FormGroup;
    contratos: Contrato[];
    usuarios: Usuario[];
    perfisGestao: Profile[];
    id: number;
    constructor(private histService: HistoricoService, private fb: FormBuilder, private contratoService: ContratosService, private usuarioService: UserService, private route: ActivatedRoute, private router: Router) {
        this.route.params.subscribe(params => {
            if (!isNaN(params['id'])) {
                this.id = params['id'];
                if (this.id) {
                    this.histService.getHistoricoGestor(this.id).subscribe(res => {
                        console.log(res);
                    });
                }
            }
        });
        this.contratoService.getContratosDoUsuario().subscribe(res => {
            this.contratos = res;
        });
        this.usuarioService.getUsuarios().subscribe(res => {
            this.usuarios = res;
        });
        this.histService.getPerfisGestao().subscribe(res => {
           this.perfisGestao = res;
        });
    }
    ngOnInit() {
        this.gestorContratoForm = this.fb.group({
            contrato: new FormControl(),
            servidor: new FormControl(),
            perfil: new FormControl(),
            dataInicio: new FormControl('')
        });
        this.gestorContratoForm.get('dataInicio').setValidators([Validators.required, this.myDateValidator]);
        this.gestorContratoForm.get('contrato').setValidators([Validators.required]);
        this.gestorContratoForm.get('servidor').setValidators([Validators.required]);
        this.gestorContratoForm.get('perfil').setValidators([Validators.required]);
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

    cadastrarGestorNoContrato() {
        if (this.gestorContratoForm.valid) {
           const historico =  new HistoricoGestor();
           historico.codigoContrato = this.gestorContratoForm.get('contrato').value;
           historico.gestor = this.gestorContratoForm.get('servidor').value;
           historico.codigoPerfilGestao = this.gestorContratoForm.get('perfil').value;
           historico.inicio = this.convertDateFormat(this.gestorContratoForm.get('dataInicio').value);
           this.histService.cadastrarGestorContrato(historico).subscribe(res => {

           });
        }
    }

    private convertDateFormat(value: string): Date {
        const temp = value.split('/');
        const dia = Number(temp[0]);
        const mes = Number(temp[1]);
        const ano = Number(temp[2]);
        return new Date(ano, mes, dia);
    }
}
