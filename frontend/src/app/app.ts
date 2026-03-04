import { Component, signal, OnInit, Renderer2, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {

  private readonly renderer = inject(Renderer2);

  readonly isDark = signal<boolean>(false);

  ngOnInit(): void {
    const saved = localStorage.getItem('theme');
    if (saved === 'dark') {
      this.isDark.set(true);
      this.renderer.setAttribute(document.documentElement, 'data-theme', 'dark');
    }
  }

  toggleTheme(): void {
    const next = !this.isDark();
    this.isDark.set(next);
    localStorage.setItem('theme', next ? 'dark' : 'light');
    this.renderer.setAttribute(document.documentElement, 'data-theme', next ? 'dark' : 'light');
  }
}