import {Directive, ElementRef, HostListener} from '@angular/core';

@Directive({
    selector: '[appTitleCase]'
})
export class TitlecaseMaskDirective {
    private el: HTMLInputElement;
    constructor(private elementRef: ElementRef) {
        this.el = this.elementRef.nativeElement;
    }
    @HostListener('keyup', ['$event'])
    onKeyUp(event) {
        if (event.key !== 'Backspace') {
            this.el.value = event.target.value.replace(/\w\S*/g, (txt => txt[0].toUpperCase() + txt.substr(1).toLowerCase() ));
        }
    }
}
