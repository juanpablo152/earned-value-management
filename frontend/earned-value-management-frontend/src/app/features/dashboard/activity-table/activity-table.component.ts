import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe, CurrencyPipe } from '@angular/common';
import { Activity } from '../../../core/models/activity.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-activity-table',
  imports: [RouterLink, DecimalPipe, CurrencyPipe, StatusBadgeComponent],
  templateUrl: './activity-table.component.html',
})
export class ActivityTableComponent {
  readonly projectId = input.required<number>();
  readonly activities = input.required<Activity[]>();
  readonly deleteActivity = output<number>();
}
