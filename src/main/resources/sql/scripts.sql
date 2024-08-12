CREATE DATABASE coinalertapp;

\c coinalertapp  -- Use the created database

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    enabled INT NOT NULL
);

CREATE TABLE authorities (
    id SERIAL PRIMARY KEY,
    username VARCHAR(45) NOT NULL,
    authority VARCHAR(45) NOT NULL
);

INSERT INTO users (username, password, enabled) VALUES ('test', '1234', 1);
INSERT INTO authorities (username, authority) VALUES ('test', 'write');

    validateUser(loginForm: NgForm) {
        this.loginService.validateLoginDetails(this.model).subscribe(
            responseData => {
                this.model = <any> responseData.body;
                let xsrf = getCookie("XSRF-TOKEN", xsrf);
                window.sessionStorage.setItem("XSRF-TOKEN", xsrf);
                this.model.authStatus = 'AUTH';
                window.sessionStorage.setItem("userdetails", JSON.stringify(this.model));
                this.router.navigate(['dashboard']);
            }
        )
    }