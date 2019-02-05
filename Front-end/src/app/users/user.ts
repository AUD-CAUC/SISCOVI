// import { Area } from '../areas/area';

import {Contrato} from '../contratos/contrato';
import {Profile} from './profile';

export class User {
  id: number;
  username: string;
  perfil: Profile;
  area_id: number;
  active: boolean;
  created: Date;
  modified: Date;
  areas_pai: Object;
  areas_child: Object;
}
