import { FilialResponse } from './filial.model';

export interface Funcionario {
  id: number;
  nome: string;
  email: string;
  cpf?: string;
  telefone?: string;
  ativo: boolean;
  dataCadastro: string;
  matricula: string;
  filial: FilialResponse;
}

export interface FuncionarioRequest {
  nome: string;
  email: string;
  senha?: string;
  cpf?: string;
  telefone?: string;
  ativo: boolean;
  matricula: string;
  filialId: number;
}

export interface FuncionarioResponse {
  id: number;
  nome: string;
  email: string;
  cpf?: string;
  telefone?: string;
  ativo: boolean;
  dataCadastro: string;
  matricula: string;
  filial: FilialResponse;
}
