import { EvmIndicators } from './evm-indicators.model';

export interface Activity {
  id: number;
  name: string;
  budgetAtCompletion: number;
  plannedProgress: number;
  actualProgress: number;
  actualCost: number;
  indicators: EvmIndicators;
}

export interface ActivityRequest {
  name: string;
  budgetAtCompletion: number;
  plannedProgress: number;
  actualProgress: number;
  actualCost: number;
}
