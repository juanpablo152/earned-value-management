import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'projects', pathMatch: 'full' },
  {
    path: 'projects',
    loadComponent: () =>
      import('./features/projects/project-list/project-list.component').then(
        (m) => m.ProjectListComponent,
      ),
  },
  {
    path: 'projects/new',
    loadComponent: () =>
      import('./features/projects/project-form/project-form.component').then(
        (m) => m.ProjectFormComponent,
      ),
  },
  {
    path: 'projects/:id/edit',
    loadComponent: () =>
      import('./features/projects/project-form/project-form.component').then(
        (m) => m.ProjectFormComponent,
      ),
  },
  {
    path: 'projects/:id',
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(
        (m) => m.DashboardComponent,
      ),
  },
  {
    path: 'projects/:id/activities/new',
    loadComponent: () =>
      import('./features/dashboard/activity-form/activity-form.component').then(
        (m) => m.ActivityFormComponent,
      ),
  },
  {
    path: 'projects/:id/activities/:activityId/edit',
    loadComponent: () =>
      import('./features/dashboard/activity-form/activity-form.component').then(
        (m) => m.ActivityFormComponent,
      ),
  },
  { path: '**', redirectTo: 'projects' },
];
