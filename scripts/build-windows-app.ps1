Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Gera um aplicativo Windows clicavel usando jpackage.
# Saidas principais:
#   build\dist\CalculadoraTrelicas\CalculadoraTrelicas.exe
#   build\CalculadoraTrelicas-windows.zip

$AppName = "CalculadoraTrelicas"
$BuildDir = "build"
$ClassesDir = Join-Path $BuildDir "classes"
$JarDir = Join-Path $BuildDir "jar"
$DistDir = Join-Path $BuildDir "dist"
$JarFile = Join-Path $JarDir "$AppName.jar"
$ZipFile = Join-Path $BuildDir "$AppName-windows.zip"

function Assert-Command($Name, $InstallHint) {
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "$Name nao foi encontrado. $InstallHint"
    }
}

if (-not (Get-Command "javac" -ErrorAction SilentlyContinue)) {
    $JdkBin = Get-ChildItem "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Java", "C:\Program Files\Microsoft" -Recurse -Filter javac.exe -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending |
        Select-Object -First 1 |
        ForEach-Object { $_.DirectoryName }

    if ($JdkBin) {
        $env:Path = "$JdkBin;$env:Path"
    }
}

Assert-Command "javac" "Instale um JDK 17+ e adicione a pasta bin ao PATH."
Assert-Command "jar" "Instale um JDK 17+ e adicione a pasta bin ao PATH."
Assert-Command "jpackage" "Instale um JDK 17+ completo; o jpackage vem junto com o JDK."

Remove-Item $ClassesDir, $JarDir, $DistDir -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item $ZipFile -Force -ErrorAction SilentlyContinue

New-Item -ItemType Directory -Force -Path $ClassesDir, $JarDir, $DistDir | Out-Null

$Sources = @("app\Main.java")
$Sources += Get-ChildItem "UserInterface\*.java", "UserInterface3D\*.java", "model\*.java", "model3d\*.java", "solver\*.java", "solver3d\*.java", "enums\*.java" |
    ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d $ClassesDir @Sources
jar cfe $JarFile Main -C $ClassesDir .

jpackage `
    --type app-image `
    --name $AppName `
    --input $JarDir `
    --main-jar "$AppName.jar" `
    --main-class Main `
    --dest $DistDir `
    --java-options "-Dfile.encoding=UTF-8"

Compress-Archive -Path (Join-Path $DistDir $AppName) -DestinationPath $ZipFile -Force

Write-Host "Aplicativo gerado em: $(Join-Path $DistDir $AppName)"
Write-Host "Executavel: $(Join-Path $DistDir "$AppName\$AppName.exe")"
Write-Host "Arquivo para compartilhar: $ZipFile"
