if (-not $env:DB_PASSWORD) {
    Write-Host "DB_PASSWORD is not set. Using an empty MySQL password." -ForegroundColor Yellow
}

.\mvnw.cmd spring-boot:run
