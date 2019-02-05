import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'cnpjPipe'
})
export class CnpjPipe implements PipeTransform {
  transform(value: string) {
    if (value) {
      value = value.toString();
      if (value.length === 14) {
        return value.substring(0, 2).concat('.')
                                    .concat(value.substring(2, 5))
                                    .concat('.')
                                    .concat(value.substring(5, 8))
                                    .concat('/')
                                    .concat(value.substring(8, 12))
                                    .concat('-')
                                    .concat(value.substring(12, 14));
      }
    }
    return value;
  }
}
