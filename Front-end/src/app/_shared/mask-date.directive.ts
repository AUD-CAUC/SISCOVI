import {Directive, HostListener, ElementRef} from '@angular/core';
import {DatePipe} from '@angular/common';

@Directive({
  selector: '[appMaskDate]'
})
export class MaskDateDirective  {
  private el: HTMLInputElement;
    private regex = new RegExp(/^[0-9]*$/);
    private otherRegex = new RegExp(/^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/);
  constructor(
    private elementRef: ElementRef,
  ) {
    this.el = this.elementRef.nativeElement;
  }
  /*@HostListener('keyup', ['$event.target.value'])
  onKeyUp(value) {
      if (value.length === 2 || value.length === 5) {
          this.el.value = value + '/';
      }
  }*/
  @HostListener('keyup', ['$event'])
  onKeyUp(event) {
      if (event.key === 'Backspace') {
      }else {
          if (this.regex.test(event.key)) {
              if ( event.target.value.length === 2 || event.target.value.length === 5) {
                  this.el.value = event.target.value + '/';
              }
          }
      }
    }
    @HostListener('keydown', ['$event'])
    onKeyDown(event) {
        if (event.key === 'Backspace') {
        }else {
            if (this.regex.test(event.key)) {
                if ( event.target.value.length === 2 || event.target.value.length === 5) {
                    this.el.value = event.target.value + '/';
                }
            }
        }
    }
}
