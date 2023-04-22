import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { catchError, Observable, of } from "rxjs";
import { LocalStorageService } from "./local-storage.service";
import { RegisterRequest } from "../data/register-request";
import { AuthResponse } from "../data/auth-response";
import { AuthApiCalls } from "../enums/AuthApiCalls";
import { StorageKeys } from "../enums/StorageKeys";
import { LoginRequest } from "../data/login-request";
import { AuthInterface } from "../interfaces/AuthInterface";
import { TokenResponse } from "../data/token-response";

@Injectable({
    providedIn: 'root'
})
export class AuthService implements AuthInterface {
    constructor(private http: HttpClient,
                private localStorageService: LocalStorageService) {
    }

    isUserAuthenticated(): boolean {
        const authToken: string = this.localStorageService.getValueFromStorage(StorageKeys.AUTH_TOKEN);
        const refreshToken: string = this.localStorageService.getValueFromStorage(StorageKeys.REFRESH_TOKEN);

        if (authToken === "") {
            return false;
        }
        const response: Observable<TokenResponse> = this.http.post<TokenResponse>(AuthApiCalls.IS_USER_AUTHENTICATED,
            this.buildAuthRequest(authToken, refreshToken));

        let isAuthTokenValid: boolean = false;
        let isRefreshTokenValid: boolean = false;

        response.subscribe((data: TokenResponse): void => {
            isAuthTokenValid = data.isAuthTokenValid;
            isRefreshTokenValid = data.isRefreshTokenValid;
        });

        if (isAuthTokenValid) {
            return true;
        }
        if (!isRefreshTokenValid) {
            return false;
        }
        this.refreshUsersToken(refreshToken);

        return true;
    }

    registerUser(registerRequest: RegisterRequest): void {
        const response: Observable<AuthResponse> = this.http.post<AuthResponse>(AuthApiCalls.REGISTER_URL, registerRequest)
            .pipe(catchError(() => of(JSON.parse(AuthApiCalls.ERROR_FOUND))));

        response.subscribe((data: AuthResponse): void => {
            this.addData(data);
        });
        console.log((this.localStorageService.getValueFromStorage(StorageKeys.AUTH_TOKEN)));
        console.log(this.localStorageService.getValueFromStorage(StorageKeys.REFRESH_TOKEN));
    }

    loginUser(loginRequest: LoginRequest): void {
        const response: Observable<AuthResponse> = this.http.post<AuthResponse>(AuthApiCalls.LOGIN_URL, loginRequest)
            .pipe(catchError(() => of(JSON.parse(AuthApiCalls.ERROR_FOUND))));

        response.subscribe((data: AuthResponse): void => {
            this.addData(data);
        });
        console.log((this.localStorageService.getValueFromStorage(StorageKeys.AUTH_TOKEN)));
        console.log(this.localStorageService.getValueFromStorage(StorageKeys.REFRESH_TOKEN));
    }

    refreshUsersToken(refreshToken: string): void {
        const response: Observable<AuthResponse> = this.http.post<AuthResponse>(AuthApiCalls.REFRESH_URL,
            this.buildRefreshToken(refreshToken))
            .pipe(catchError(() => of(JSON.parse(AuthApiCalls.ERROR_FOUND))));

        response.subscribe((data: AuthResponse): void => {
            this.localStorageService.removeValueFromStorage(StorageKeys.AUTH_TOKEN);
            this.localStorageService.removeValueFromStorage(StorageKeys.REFRESH_TOKEN);

            this.addData(data);
        });
        console.log((this.localStorageService.getValueFromStorage(StorageKeys.AUTH_TOKEN)));
        console.log(this.localStorageService.getValueFromStorage(StorageKeys.REFRESH_TOKEN));
    }

    private buildAuthRequest(authToken: string, refreshToken: string) {
        return JSON.parse(`{${ authToken }, ${ refreshToken }}`);
    }

    private buildRefreshToken(refreshToken: string) {
        return JSON.parse(`{${ refreshToken }`);
    }

    private addData(data: AuthResponse): void {
        console.log(data);

        if (data.isEmpty()) {
            return;
        }
        this.localStorageService.addValueIntoStorage(StorageKeys.AUTH_TOKEN, data.authToken);
        this.localStorageService.addValueIntoStorage(StorageKeys.REFRESH_TOKEN, data.refreshToken);
    }
}
