# BankApp
Banking application project for OOP classes at the University.

# Table of contents
1. [Errors](#Errors)
- [Error 1](#Error-1)
- [Error 12](#Error-12)
- [Error 123](#Error-123)
2. [Authors](#Authors)
2. [License](#License)

## Errors
List of errors which You can see in application.

### Error 1
This error is showing when application have problem with connection during log in to app. </br>
Check if Your database is online or if Your connection is invalid! </br>
To check connection you should have login and password to database and URL. </br>
</br>
Example connection to database 'schema' with user with login 'root' and password 'rootroot'
```Java
  Connection connection = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/schema",
                    "root", 
                    "rootroot");
```

### Error 12
This error is showing when application have problem with connection after log in to app. </br>
Check if Your database is online or if Your connection is invalid! </br>
To check connection you should have login and password to database and URL. </br>
</br>
Example connection to database 'schema' with user with login 'root' and password 'rootroot'
```Java
  Connection connection = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/schema",
                    "root", 
                    "rootroot");
```

### Error 123
This error is showing when data which You want use does not exist. </br>
You should check data in Your Database.

## Authors

- [@oskarpasko](https://www.github.com/oskarpasko)
- [@eltwor](https://www.github.com/eltwor)

## License

[MIT](https://choosealicense.com/licenses/mit/)
