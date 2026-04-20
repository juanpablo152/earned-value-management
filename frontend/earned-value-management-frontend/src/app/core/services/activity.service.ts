import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Activity, ActivityRequest } from '../models/activity.model';

@Injectable({ providedIn: 'root' })
export class ActivityService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  private url(projectId: number): string {
    return `${this.baseUrl}/projects/${projectId}/activities`;
  }

  findAll(projectId: number): Promise<Activity[]> {
    return lastValueFrom(this.http.get<Activity[]>(this.url(projectId)));
  }

  findById(projectId: number, activityId: number): Promise<Activity> {
    return lastValueFrom(this.http.get<Activity>(`${this.url(projectId)}/${activityId}`));
  }

  create(projectId: number, request: ActivityRequest): Promise<Activity> {
    return lastValueFrom(this.http.post<Activity>(this.url(projectId), request));
  }

  update(projectId: number, activityId: number, request: ActivityRequest): Promise<Activity> {
    return lastValueFrom(this.http.put<Activity>(`${this.url(projectId)}/${activityId}`, request));
  }

  delete(projectId: number, activityId: number): Promise<void> {
    return lastValueFrom(this.http.delete<void>(`${this.url(projectId)}/${activityId}`));
  }
}
