# Dhizuku Connection Tester

## How to use

1. init
2. requestPermission
3. bindUserService
4. (exec DPM features)
5. stopUserService
6. unbindUserService

> [!IMPORTANT]
> In **Dhizuku v2.9** and later, after performing DPM processing, **be sure to call `stopUserService` and `unbindUserService` before closing the app**.  
> Otherwise we will keep getting `DeadObjectException` unless we restart the server from the Dhizuku app.
