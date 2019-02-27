package telescope

import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ListingResults(currentPage: Int, lastPage: Int) {
    // Items
}

object Parse {
    val log: Logger = LoggerFactory.getLogger("Parse")

    fun parse(responseBody: String): ListingResults {
        Jsoup.parse(responseBody).run {
            // //*[@id="result-div"]/div[1]/nav[1]/ul/li[1]
            // Find pages
            val currentPage = select("#result-div > div:nth-child(1) > nav.listings-pagingation > ul > li.active > a").attr("data-page").toInt()
            val lastPage = select("#last").attr("data-page").toInt()

            // //*[@id="result-div"]/section/ul[2]/li[1]
            select("#result-div > section > ul.products > li > a").forEachIndexed { index, element ->
                log.info("Looking at index $index")

                val link = element.attr("href")
                val thumbnail = element.select("div.image img").attr("src")
                val badges = element.select("div.image badges li").flatMap { badgeElem -> badgeElem.classNames() }
                val titleTexts = element.select("span.data-container > div.product-data > div.title").first().textNodes()
                val title = titleTexts.get(0).text().trim()
                if(titleTexts.size > 1) {
                    val bidString = titleTexts.get(1)
                    // TODO clean up bigString with Regex for "Bids: 0 "
                }

                val priceString = element.select("span.data-container > div.product-price > div.price").first().text()
                // TODO Convert to currency

                val countdownString = element.select("span.data-container > div.product-price > div.countdown").first().text()

                log.debug("$title")

            }
            return ListingResults(currentPage, lastPage)
        }
    }
}


fun main(args: Array<String>) {
    val fetchListings = Download.fetchListings("telescope", 1)
    Parse.parse(fetchListings)
}