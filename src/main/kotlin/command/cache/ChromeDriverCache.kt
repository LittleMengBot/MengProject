package command.cache

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

object ChromeDriverCache {

    private val options = ChromeOptions()

    fun init(): ChromeDriver? {
        options.addArguments("--headless")
        options.addArguments("--disable-gpu")
        options.addArguments("--no-sandbox")
        options.addArguments("--hide-scrollbars")
        options.addArguments("--proxy-server=socks5://127.0.0.1:9050")
        return try {
            ChromeDriver(options)
        }catch (e: Exception){
            e.printStackTrace()
            println("----------\nChrome Driver init failed.")
            null
        }
    }

}