import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'afirimativePipe'
})
export class AfirmativePipe implements PipeTransform {
  transform(value: string) {
    if (value === 'S' || value === 's') {
      return 'SIM';
    } else if (value === 'N' || value === 'n') {
      return 'NÃO';
    } else {
      return '-';
    }
  }
}
