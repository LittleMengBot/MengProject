package command.cache

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

object ChromeDriverCache {

    private val options = ChromeOptions()
    var driver: ChromeDriver? = null

    fun init(): Boolean {
        options.addArguments("--headless")
        options.addArguments("--disable-gpu")
        options.addArguments("--no-sandbox")
        options.addArguments("--hide-scrollbars")
        return try {
            driver = ChromeDriver(options)
            true
        }catch (e: Exception){
            e.printStackTrace()
            println("----------\nChrome Driver init failed.")
            false
        }
    }

}