import { TipoPagamento } from './enums';

export interface PagamentoRequest {
  valor: number;
  tipo: TipoPagamento;
  formaPagamento?: string;
  codigoTransacaoGateway?: string;
  reservaId: number;
}

export interface PagamentoResponse {
  id: number;
  dataPagamento: string;
  valor: number;
  tipo: TipoPagamento;
  formaPagamento?: string;
  codigoTransacaoGateway?: string;
  reservaId: number;
}
