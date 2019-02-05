import {Directive, ElementRef, HostListener} from '@angular/core';

@Directive({
    selector: '[appMaskPercentual]'
})
export class MaskPercentageDirective {
    private element: HTMLInputElement;
    private regex = new RegExp(/^[0-9]*$/);
    private digitado: number;
    constructor(private elementRef: ElementRef) {
        this.element = elementRef.nativeElement;
        this.element.value = '0,00%';
    }
    @HostListener('keyup', ['$event'])
    onKeyUp (event) {
        if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
            this.element.setSelectionRange(this.element.value.length - 1, this.element.value.length - 1 );
        }
        if (event.key === 'Backspace' || event.key === 'Delete') {
            if ((this.digitado)) {
                this.digitado = (this.digitado - (this.digitado % 10)) / 10;
                if (this.digitado === 0) {
                    this.element.value = '0,00%';
                }else {
                    this.element.value = (String(this.digitado / 100) + '%').replace('.', ',');
                }
            }else {
                this.element.value = '0,00%';
            }
            this.element.setSelectionRange(this.element.value.length - 1, this.element.value.length - 1 );
        }else {
            if (this.regex.test(event.key)) {
               if (this.digitado == null || this.digitado === 0) {
                   if (event.key !== '0') {
                       this.digitado = Number(event.key);
                       this.element.value = String((this.digitado / 100) + '%').replace('.', ',');
                   }else {
                       this.element.value = '0,00%';
                   }
               }else if ((event.key === '0') && (this.digitado === 0)) {
                   this.element.value = '0,00%';
               }else {
                   this.digitado =  (this.digitado * 10) + Number(event.key);
                   this.element.value = String((this.digitado / 100) + '%').replace('.', ',');
               }
                this.element.setSelectionRange(this.element.value.length - 1, this.element.value.length - 1);
            }
        }
    }
    @HostListener('click', ['$event'])
    onClick (event) {
        this.element.focus();
        if (this.digitado == null || this.digitado === 0) {
            this.element.value = '0,00%';
        }else {
            this.element.value = String(this.digitado / 100).replace('.', ',') + '%';
        }
        this.element.setSelectionRange(this.element.value.length - 1, this.element.value.length - 1 );
    }
    @HostListener('blur', ['$event'])
    onBlur(event) {
        if (this.digitado == null || this.digitado === 0) {
            this.element.value = '0,00%';
        }else {
            this.element.value = String(this.digitado / 100).replace('.', ',') + '%';
        }
    }
}
