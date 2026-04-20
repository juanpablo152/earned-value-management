import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';

interface ErrorMessageMap {
  [key: string]: (errors: ValidationErrors) => string;
}

@Injectable({ providedIn: 'root' })
export class FormValidationService {
  private readonly errorMessages: ErrorMessageMap = {
    required: () => 'Este campo es obligatorio.',
    maxlength: (errors) =>
      `No debe exceder ${errors['maxlength'].requiredLength} caracteres.`,
    min: (errors) =>
      `El valor minimo permitido es ${errors['min'].min}.`,
    max: (errors) =>
      `El valor maximo permitido es ${errors['max'].max}.`,
  };

  getError(control: AbstractControl | null): string | null {
    if (!control || !control.errors || !control.touched) return null;

    const errorKey = Object.keys(control.errors)[0];
    const messageFn = this.errorMessages[errorKey];
    return messageFn ? messageFn(control.errors) : 'Campo invalido.';
  }
}
