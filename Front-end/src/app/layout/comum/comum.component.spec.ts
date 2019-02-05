/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ComumComponent } from './comum.component';

describe('ComumComponent', () => {
  let component: ComumComponent;
  let fixture: ComponentFixture<ComumComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ComumComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
