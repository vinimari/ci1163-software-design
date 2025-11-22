export interface FilialRequest {
  nome: string;
  cidade: string;
  estado: string;
  endereco?: string;
  telefone?: string;
}

export interface FilialResponse {
  id: number;
  nome: string;
  cidade: string;
  estado: string;
  endereco?: string;
  telefone?: string;
  dataCadastro: string;
}
