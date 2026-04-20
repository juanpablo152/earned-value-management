import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { FormValidationService } from '../../../core/services/form-validation.service';

@Component({
  selector: 'app-project-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './project-form.component.html',
})
export class ProjectFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);
  readonly validation = inject(FormValidationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly isEditMode = signal(false);
  readonly submitting = signal(false);
  private projectId = 0;

  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: [''],
  });

  async ngOnInit(): Promise<void> {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.projectId = +id;
      try {
        const project = await this.projectService.findById(this.projectId);
        this.form.patchValue({ name: project.name, description: project.description ?? '' });
      } catch (error) {
        console.error(error);
        this.router.navigate(['/projects']);
      }
    }
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) return;
    this.submitting.set(true);
    const request = this.form.getRawValue();
    try {
      if (this.isEditMode()) {
        await this.projectService.update(this.projectId, request);
      } else {
        await this.projectService.create(request);
      }
      this.router.navigate(['/projects']);
    } catch (error) {
      console.error(error);
      this.submitting.set(false);
    }
  }
}
