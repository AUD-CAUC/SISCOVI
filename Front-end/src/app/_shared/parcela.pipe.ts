import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'parcelaPipe'
})
export class ParcelaPipe implements PipeTransform {
    transform(value: number) {
        if (value === 0) {
           return 'Única';
        }
        if (value === 1) {
            return 'Primeira';
        }
        if (value === 2) {
            return 'Segunda';
        }
        if (value === 3) {
            return 'Terceira';
        }
    }
}
