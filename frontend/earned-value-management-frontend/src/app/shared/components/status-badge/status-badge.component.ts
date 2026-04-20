import { Component, input, computed } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  templateUrl: './status-badge.component.html',
})
export class StatusBadgeComponent {
  readonly interpretation = input.required<string>();

  protected readonly label = computed(() => {
    const value = this.interpretation();
    if (value.startsWith('Under budget')) return 'Bajo Presupuesto';
    if (value.startsWith('Over budget')) return 'Sobrecosto';
    if (value.startsWith('On budget')) return 'En Presupuesto';
    if (value.startsWith('Ahead of schedule')) return 'Adelantado';
    if (value.startsWith('Behind schedule')) return 'Atrasado';
    if (value.startsWith('On schedule')) return 'En Cronograma';
    return 'N/A';
  });

  protected readonly badgeClasses = computed(() => {
    const value = this.interpretation();
    const base = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold';
    if (value.startsWith('Under budget') || value.startsWith('Ahead of schedule')) {
      return `${base} bg-emerald-100 text-emerald-800`;
    }
    if (value.startsWith('On budget') || value.startsWith('On schedule')) {
      return `${base} bg-blue-100 text-blue-800`;
    }
    if (value.startsWith('Over budget') || value.startsWith('Behind schedule')) {
      return `${base} bg-red-100 text-red-800`;
    }
    return `${base} bg-slate-100 text-slate-600`;
  });
}
