import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, ResolveData, RouterStateSnapshot } from '@angular/router';

@Component({
  selector: 'app-midia-pesquisa',
  imports: [],
  templateUrl: './midia-pesquisa.html',
  styleUrl: './midia-pesquisa.css',
})
export class MidiaPesquisa implements OnInit {
  static readonly RECURSO = 'recurso'

  recurso: any

  constructor(private readonly rota: ActivatedRoute) { }

  ngOnInit() {
    this.recurso = this.rota.snapshot.data[MidiaPesquisa.RECURSO]
  }
}

export const MIDIA_PESQUISA_RESOLVEDORES: ResolveData = {}
MIDIA_PESQUISA_RESOLVEDORES[MidiaPesquisa.RECURSO] = (rota: ActivatedRouteSnapshot, estado: RouterStateSnapshot) => {
  return inject(HttpClient).get('/backend/midia/pesquisa?expandir=true')
}