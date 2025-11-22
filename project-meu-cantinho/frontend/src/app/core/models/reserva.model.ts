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
  usuario: {
    id: number;
    nome: string;
    email?: string;
    cpf?: string;
    telefone?: string;
  };
  espaco: {
    id: number;
    nome: string;
    capacidade?: number;
    filial: {
      id: number;
      nome: string;
      cidade?: string;
      estado?: string;
    };
  };
  totalPago?: number;
  saldo?: number;
}
