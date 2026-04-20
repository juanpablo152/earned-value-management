import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ProjectService } from '../../../core/services/project.service';
import { Project } from '../../../core/models/project.model';

@Component({
  selector: 'app-project-list',
  imports: [RouterLink, DatePipe],
  templateUrl: './project-list.component.html',
})
export class ProjectListComponent implements OnInit {
  private readonly projectService = inject(ProjectService);

  readonly projects = signal<Project[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loadProjects();
  }

  async onDelete(project: Project): Promise<void> {
    try {
      await this.projectService.delete(project.id);
      await this.loadProjects();
    } catch (error) {
      console.error(error);
    }
  }

  private async loadProjects(): Promise<void> {
    this.loading.set(true);
    try {
      const data = await this.projectService.findAll();
      this.projects.set(data);
    } catch (error) {
      console.error(error);
    } finally {
      this.loading.set(false);
    }
  }
}
