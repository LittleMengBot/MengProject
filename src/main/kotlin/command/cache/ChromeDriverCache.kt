package command.cache

import mu.KotlinLogging
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.IOException
import java.lang.IllegalStateException

private val logger = KotlinLogging.logger {}

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
        } catch (e: IOException) {
            logger.error("----------\nChrome Driver init failed.$e")
            null
        } catch (e: IllegalStateException) {
            logger.error("----------\nChrome Driver init failed.$e")
            null
        }
    }
}
