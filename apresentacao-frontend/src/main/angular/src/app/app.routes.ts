import { Routes } from '@angular/router';
import { MIDIA_PESQUISA_RESOLVEDORES, MidiaPesquisa } from './midia-pesquisa/midia-pesquisa';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/midia/pesquisa',
        pathMatch: 'full'
    },
    {
        path: 'midia/pesquisa',
        component: MidiaPesquisa,
        resolve: MIDIA_PESQUISA_RESOLVEDORES
    }
];