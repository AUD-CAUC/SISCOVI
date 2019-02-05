import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {UserService} from '../users/user.service';
import {MaterializeAction} from 'angular2-materialize';
import {Usuario} from './usuario';
import {EventEmitter} from '@angular/core';
import {CadastroUsuarioService} from './cadastro-usuario/cadastro-usuario.service';

@Component({
  selector: 'app-usuario',
  templateUrl: 'usuarios.component.html',
  styleUrls: ['usuarios.component.scss']
})
export class UsuariosComponent {
  usuarios: Usuario[] = [];
  modalActions = new EventEmitter<string|MaterializeAction>();
  render = false;
  cadUs: CadastroUsuarioService;
  userService: UserService;
  alert: boolean;
  constructor(userService: UserService, cadUs: CadastroUsuarioService) {
    this.cadUs = cadUs;
    this.userService = userService;
    userService.getUsuarios().subscribe(res => {
      this.usuarios = res;
    });
  }
    openModal() {
      this.render = true;
      this.modalActions.emit({action: 'modal', params: ['open']});
    }
    closeModal() {
      this.render = false;
      this.cadUs.setValidity(true);
      this.modalActions.emit({action: 'modal', params: ['close']});
    }
    sendForm(event: Event) {
      event.preventDefault();
      this.cadUs.cadastrarUsuario().subscribe(res => {
        console.log(res);
        if (res.mensagem === 'Usuário Cadastrado Com Sucesso !') {
            const newUsuarios = this.usuarios.slice(0);
            newUsuarios.push(this.cadUs.cadastroUsuario.usuario);
            this.usuarios = newUsuarios;
            this.userService.getUsuarios().subscribe(array => {
                this.usuarios = array;
            });
            this.closeModal();
        }
      });
    }
}
