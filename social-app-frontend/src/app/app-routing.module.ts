import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoutePaths } from "./core/enums/RoutePaths";
import { AuthGuard } from "./core/guards/auth.guard";

const routes: Routes = [
    {
        path: RoutePaths.AUTH_PATH,
        loadChildren: () => import(RoutePaths.AUTH_MODULE)
            .then(module => module.AuthModule)
    },
    {
        path: RoutePaths.CURRENT_PATH,
        redirectTo: RoutePaths.LOGIN_PATH,
        pathMatch: 'full'
    },
    {
        path: RoutePaths.HOME_PATH,
        loadChildren: () => import(RoutePaths.HOME_MODULE)
            .then(module => module.HomeModule),
        canActivate: [AuthGuard]
    },
    {
        path: RoutePaths.NOT_FOUND_PATH,
        loadChildren: () => import(RoutePaths.NOT_FOUND_MODULE)
            .then(module => module.NotFoundModule)
    },
    {
        path: RoutePaths.ERROR_MATCHER,
        redirectTo: RoutePaths.NOT_FOUND_PATH
    },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
