import { PerfilUsuario } from './enums';

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha: string;
  cpf?: string;
  telefone?: string;
  ativo?: boolean;
}

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
  cpf?: string;
  telefone?: string;
  ativo: boolean;
  dataCadastro: string;
}

export interface ClienteRequest extends UsuarioRequest {}

export interface ClienteResponse extends UsuarioResponse {}

export interface AdministradorRequest extends UsuarioRequest {}

export interface AdministradorResponse extends UsuarioResponse {}

export interface FuncionarioRequest extends UsuarioRequest {
  matricula?: string;
  filialId?: number;
}

export interface FuncionarioResponse extends UsuarioResponse {
  matricula?: string;
  filialId?: number;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  usuario: UsuarioResponse;
}
