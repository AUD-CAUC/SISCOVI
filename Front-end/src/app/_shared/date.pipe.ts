import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'datePipe'
})
export class DatePipe implements PipeTransform {
  transform(value: Object) {
    if (value === null || value === 'undefined') {
      const temp = '-';
      return temp;
    }
    return value;
  }
}
