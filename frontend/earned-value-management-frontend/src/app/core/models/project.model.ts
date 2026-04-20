import { Activity } from './activity.model';
import { EvmIndicators } from './evm-indicators.model';

export interface Project {
  id: number;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectDetail extends Project {
  activities: Activity[];
  consolidatedIndicators: EvmIndicators;
}

export interface ProjectRequest {
  name: string;
  description: string;
}
