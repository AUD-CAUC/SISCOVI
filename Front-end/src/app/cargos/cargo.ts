import {Convencao} from '../convencoes-coletivas/convencao';

export class Cargo {
  codigo: number;
  nome: string;
  descricao: string;
  remuneracao: number;
  adicionais: number;
  trienios: number;
  loginAtualizacao: string;
  dataAtualizacao: Date;
  convencao: Convencao;
}
