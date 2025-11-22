export interface EspacoRequest {
  nome: string;
  descricao?: string;
  capacidade: number;
  precoDiaria: number;
  ativo?: boolean;
  urlFotoPrincipal?: string;
  filialId: number;
}

export interface EspacoResponse {
  id: number;
  nome: string;
  descricao?: string;
  capacidade: number;
  precoDiaria: number;
  ativo: boolean;
  urlFotoPrincipal?: string;
  filialId: number;
  filialNome?: string;
}
