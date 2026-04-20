import {
  Component,
  input,
  effect,
  ElementRef,
  viewChild,
  AfterViewInit,
} from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { Activity } from '../../../core/models/activity.model';

Chart.register(...registerables);

@Component({
  selector: 'app-evm-chart',
  templateUrl: './evm-chart.component.html',
})
export class EvmChartComponent implements AfterViewInit {
  readonly activities = input.required<Activity[]>();
  readonly canvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('chartCanvas');

  private chart: Chart | null = null;
  private viewReady = false;

  constructor() {
    effect(() => {
      const data = this.activities();
      if (this.viewReady) {
        this.renderChart(data);
      }
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.renderChart(this.activities());
  }

  private renderChart(activities: Activity[]): void {
    if (this.chart) {
      this.chart.destroy();
    }

    const canvas = this.canvasRef().nativeElement;
    const labels = activities.map((a) => a.name);
    const pvData = activities.map((a) => a.indicators?.plannedValue ?? 0);
    const evData = activities.map((a) => a.indicators?.earnedValue ?? 0);
    const acData = activities.map((a) => a.actualCost ?? 0);

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'PV (Valor Planificado)',
            data: pvData,
            backgroundColor: 'rgba(99, 102, 241, 0.7)',
            borderColor: 'rgb(99, 102, 241)',
            borderWidth: 1,
          },
          {
            label: 'EV (Valor Ganado)',
            data: evData,
            backgroundColor: 'rgba(16, 185, 129, 0.7)',
            borderColor: 'rgb(16, 185, 129)',
            borderWidth: 1,
          },
          {
            label: 'AC (Costo Real)',
            data: acData,
            backgroundColor: 'rgba(239, 68, 68, 0.7)',
            borderColor: 'rgb(239, 68, 68)',
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top' },
        },
        scales: {
          y: {
            beginAtZero: true,
            title: { display: true, text: 'Valor ($)' },
          },
        },
      },
    });
  }
}
