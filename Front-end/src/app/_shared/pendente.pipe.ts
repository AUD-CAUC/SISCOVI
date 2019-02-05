import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'pendentePipe'
})
export class PendentePipe implements PipeTransform {
    transform(value: string) {
        if (value === 'S' || value === 's') {
           return 'APROVADO';
        }
        if (value === 'N' || value === 'n') {
            return 'NEGADO';
        }
    }
}