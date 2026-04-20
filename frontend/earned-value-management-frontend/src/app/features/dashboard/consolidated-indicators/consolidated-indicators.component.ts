import { Component, input } from '@angular/core';
import { DecimalPipe, CurrencyPipe } from '@angular/common';
import { EvmIndicators } from '../../../core/models/evm-indicators.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-consolidated-indicators',
  imports: [DecimalPipe, CurrencyPipe, StatusBadgeComponent],
  templateUrl: './consolidated-indicators.component.html',
})
export class ConsolidatedIndicatorsComponent {
  readonly indicators = input.required<EvmIndicators | null>();
}
