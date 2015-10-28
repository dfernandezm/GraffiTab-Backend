environments {
    local {
        databaseDomainAndName = 'localhost:3307/graffitab'
        databaseUsername = 'root'
        databasePassword = ''

        loggingLogPath = '/tmp/graffitab.log'
        loggingEmailTo = 'dummy@gmail.com'
        loggingEmailFrom = 'dummy@gmail.com'
        smtpHost = 'smtp.gmail.com'
        loggingDefaultLogLevel = 'debug'
    }
    
    herokuDev {
        databaseDomainAndName = "$dbHost:3306/$dbName"
        databaseUsername = "$dbUser"
        databasePassword = "$dbPassword"
        loggingLogPath = '/app/graffitab.log'
        loggingEmailTo = 'dummy@gmail.com'
        loggingEmailFrom = 'dummy@gmail.com'
        smtpHost = 'smtp.gmail.com'
        loggingDefaultLogLevel = 'debug'
    }
}
