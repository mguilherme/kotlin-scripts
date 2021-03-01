#!/usr/bin/env kotlinc -J--add-opens=java.base/java.util=ALL-UNNAMED -script
@file:CompilerOptions("-jvm-target", "11")
@file:DependsOn("org.seleniumhq.selenium:selenium-java:3.141.59")

import Idontime_bot_main.Config
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver

typealias Config = Pair<String, String>

// $ brew install chromedriver
// $ xattr -d com.apple.quarantine $(which chromedriver)

val driver = ChromeDriver()

// Config
val basePath = "https://idontime.vwgs.pt"
val movements = "$basePath/areas/as/asmovimentos.aspx"

val username = "<USERNAME>"
val password = "<PASSWORD>"

val entrance = "ctl00_cphContent_cbTipoMov_DDD_L_LBI3T0"
val exit = "ctl00_cphContent_cbTipoMov_DDD_L_LBI4T0"

//val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

val dates = listOf("22-02-2021", "23-02-2021", "24-02-2021", "25-02-2021", "26-02-2021")

// Actions
navigate()
login()

val test = dates.associateBy(
    { "$it 09:00" to entrance },
    { "$it 18:00" to exit }
).forEach { (entrance, exit) ->
    add(entrance)
    add(exit)
}

fun navigate() = driver.navigate().to(basePath)

fun login() = driver.run {
    findElement(By.id("cpLogin_txtUtilizador_I")).sendKeys(username)
    findElement(By.id("cpLogin_txtSenha")).sendKeys(password)
    findElement(By.id("cpLogin_btnEntrar_CD")).click()
}

fun add(config: Config) = driver.run {
    val (date, id) = config

    navigate().to(movements)
    Thread.sleep(1000)
    findElement(By.id("ctl00_ASPxSplitter_botoes_btnAdicionar")).click()
    Thread.sleep(1000)
    switchTo().frame("ctl00_ASPxSplitter_cphContent_popupEdita_CIF-1") // Pop-up frame

    // Select Date
    findElement(By.id("ctl00_cphContent_txtData_I")).run {
        clear()
        sendKeys(date) // Entrance
    }

    findElement(By.id("ctl00_cphContent_cbTipoMov_B-1Img")).click() // Dropdown
    Thread.sleep(1000)
    findElement(By.id(id)).click() // Entrada A
    Thread.sleep(1000)
    findElement(By.id("ctl00_btnGuardar")).click() // Save
}
