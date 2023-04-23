import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from "./register.component";
import { RoutePaths } from "../../../core/enums/RoutePaths";

const routes: Routes = [
    {
        path: RoutePaths.CURRENT_PATH,
        component: RegisterComponent
    },
    {
        path: RoutePaths.LOGIN_PATH,
        loadChildren: () => import(RoutePaths.LOGIN_MODULE).then(module => module.LoginModule)
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class RegisterRoutingModule {
}
