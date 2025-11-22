import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { PerfilUsuario } from '../../../core/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  isAdmin = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    const user = this.authService.getCurrentUser();
    this.isAdmin = user?.perfil === PerfilUsuario.ADMIN || user?.perfil === PerfilUsuario.FUNCIONARIO;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
