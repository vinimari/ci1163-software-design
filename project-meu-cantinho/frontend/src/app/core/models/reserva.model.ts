import { StatusReserva } from './enums';

export interface ReservaRequest {
  dataEvento: string; // ISO date format
  valorTotal: number;
  observacoes?: string;
  status?: StatusReserva;
  usuarioId: number;
  espacoId: number;
}

export interface ReservaResponse {
  id: number;
  dataCriacao: string;
  dataEvento: string;
  valorTotal: number;
  observacoes?: string;
  status: StatusReserva;
  usuarioId: number;
  usuarioNome?: string;
  espacoId: number;
  espacoNome?: string;
  totalPago?: number;
  saldo?: number;
}
