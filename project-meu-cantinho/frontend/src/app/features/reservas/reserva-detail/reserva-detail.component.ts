import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-reserva-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './reserva-detail.component.html',
  styleUrl: './reserva-detail.component.scss'
})
export class ReservaDetailComponent {}
