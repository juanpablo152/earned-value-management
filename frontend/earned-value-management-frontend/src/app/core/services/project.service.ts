import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Project, ProjectDetail, ProjectRequest } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/projects`;

  findAll(): Promise<Project[]> {
    return lastValueFrom(this.http.get<Project[]>(this.baseUrl));
  }

  findById(id: number): Promise<ProjectDetail> {
    return lastValueFrom(this.http.get<ProjectDetail>(`${this.baseUrl}/${id}`));
  }

  create(request: ProjectRequest): Promise<Project> {
    return lastValueFrom(this.http.post<Project>(this.baseUrl, request));
  }

  update(id: number, request: ProjectRequest): Promise<Project> {
    return lastValueFrom(this.http.put<Project>(`${this.baseUrl}/${id}`, request));
  }

  delete(id: number): Promise<void> {
    return lastValueFrom(this.http.delete<void>(`${this.baseUrl}/${id}`));
  }
}
