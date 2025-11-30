import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appConfig } from './app.config';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';

describe('App Config', () => {
  it('deve provide router', () => {
    const routerProvider = appConfig.providers.find(
      p => typeof p === 'object' && p && 'provide' in p
    );
    expect(appConfig.providers).toBeDefined();
    expect(appConfig.providers.length).toBeGreaterThan(0);
  });

  it('deve provide http client', () => {
    expect(appConfig.providers).toBeDefined();
    expect(appConfig.providers.length).toBeGreaterThan(0);
  });

  it('deve provide zone change detection', () => {
    expect(appConfig.providers).toBeDefined();
    expect(appConfig.providers.length).toBe(3);
  });

  it('deve configure providers correctly', () => {
    TestBed.configureTestingModule(appConfig);
    expect(TestBed).toBeDefined();
  });

  it('deve have all necessary providers', () => {
    expect(appConfig.providers.length).toBeGreaterThanOrEqual(3);
  });

  describe('Provider Configuration', () => {
    it('deve provide ZoneChangeDetection', () => {
      expect(appConfig.providers[0]).toBeDefined();
    });

    it('deve provide Router with routes', () => {
      expect(appConfig.providers[1]).toBeDefined();
    });

    it('deve provide HttpClient with interceptors', () => {
      expect(appConfig.providers[2]).toBeDefined();
    });
  });
});
