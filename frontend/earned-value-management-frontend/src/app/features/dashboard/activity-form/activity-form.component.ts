import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ActivityService } from '../../../core/services/activity.service';
import { FormValidationService } from '../../../core/services/form-validation.service';

@Component({
  selector: 'app-activity-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './activity-form.component.html',
})
export class ActivityFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly activityService = inject(ActivityService);
  readonly validation = inject(FormValidationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly isEditMode = signal(false);
  readonly submitting = signal(false);
  projectId = 0;
  private activityId = 0;

  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    budgetAtCompletion: [0, [Validators.required, Validators.min(0)]],
    plannedProgress: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    actualProgress: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    actualCost: [0, [Validators.required, Validators.min(0)]],
  });

  async ngOnInit(): Promise<void> {
    this.projectId = +this.route.snapshot.paramMap.get('id')!;
    const activityId = this.route.snapshot.paramMap.get('activityId');

    if (activityId) {
      this.isEditMode.set(true);
      this.activityId = +activityId;
      try {
        const activity = await this.activityService.findById(this.projectId, this.activityId);
        this.form.patchValue({
          name: activity.name,
          budgetAtCompletion: activity.budgetAtCompletion,
          plannedProgress: activity.plannedProgress,
          actualProgress: activity.actualProgress,
          actualCost: activity.actualCost,
        });
      } catch (error) {
        console.error(error);
        this.router.navigate(['/projects', this.projectId]);
      }
    }
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) return;
    this.submitting.set(true);
    const request = this.form.getRawValue();

    try {
      if (this.isEditMode()) {
        await this.activityService.update(this.projectId, this.activityId, request);
      } else {
        await this.activityService.create(this.projectId, request);
      }
      this.router.navigate(['/projects', this.projectId]);
    } catch (error) {
      console.error(error);
      this.submitting.set(false);
    }
  }
}
