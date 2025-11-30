import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService, ReservaService } from '../../../../core/services';
import { ClienteResponse } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-clientes-list',
  standalone: true,
  imports: [CommonModule, LoadingComponent, ButtonComponent],
  templateUrl: './clientes-list.component.html',
  styleUrls: ['./clientes-list.component.scss']
})
export class ClientesListComponent implements OnInit {
  clientes: ClienteResponse[] = [];
  clientesFiltrados: ClienteResponse[] = [];
  loading = false;
  searchTerm = '';

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.loading = true;

    this.clienteService.getAll().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.clientesFiltrados = clientes;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        alert('Erro ao carregar clientes');
        console.error('Erro ao carregar clientes:', err);
      }
    });
  }

  onSearchChange(term: string): void {
    this.searchTerm = term.toLowerCase();
    this.filterClientes();
  }

  filterClientes(): void {
    if (!this.searchTerm) {
      this.clientesFiltrados = this.clientes;
      return;
    }

    this.clientesFiltrados = this.clientes.filter(cliente =>
      cliente.nome.toLowerCase().includes(this.searchTerm) ||
      cliente.email.toLowerCase().includes(this.searchTerm) ||
      (cliente.cpf && cliente.cpf.includes(this.searchTerm))
    );
  }

  viewCliente(id: number): void {
    this.router.navigate(['/admin/clientes', id]);
  }

  toggleAtivo(cliente: ClienteResponse): void {
    if (confirm(`Tem certeza que deseja ${cliente.ativo ? 'desativar' : 'ativar'} este cliente?`)) {
      this.loading = true;
      this.clienteService.toggleAtivo(cliente.id, !cliente.ativo).subscribe({
        next: () => {
          this.loadClientes();
        },
        error: (err) => {
          this.loading = false;
          alert('Erro ao atualizar cliente');
          console.error('Erro ao atualizar cliente:', err);
        }
      });
    }
  }
}
