import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ProjectService } from '../../core/services/project.service';
import { ActivityService } from '../../core/services/activity.service';
import { ProjectDetail } from '../../core/models/project.model';
import { ActivityTableComponent } from './activity-table/activity-table.component';
import { ConsolidatedIndicatorsComponent } from './consolidated-indicators/consolidated-indicators.component';
import { EvmChartComponent } from './evm-chart/evm-chart.component';

@Component({
  selector: 'app-dashboard',
  imports: [
    RouterLink,
    DatePipe,
    ActivityTableComponent,
    ConsolidatedIndicatorsComponent,
    EvmChartComponent,
  ],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly projectService = inject(ProjectService);
  private readonly activityService = inject(ActivityService);

  readonly project = signal<ProjectDetail | null>(null);
  readonly loading = signal(true);

  private projectId = 0;

  ngOnInit(): void {
    this.projectId = +this.route.snapshot.paramMap.get('id')!;
    this.loadProjects();
  }

  async onDeleteActivity(activityId: number): Promise<void> {
    try {
      await this.activityService.delete(this.projectId, activityId);
      await this.loadProjects();
    } catch (error) {
      console.error(error);
    }
  }

  private async loadProjects(): Promise<void> {
    this.loading.set(true);
    try {
      const data = await this.projectService.findById(this.projectId);
      this.project.set(data);
    } catch (error) {
      console.error(error);
    } finally {
      this.loading.set(false);
    }
  }
}
