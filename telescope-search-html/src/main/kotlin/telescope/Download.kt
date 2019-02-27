package telescope

// curl 'https://www.shopgoodwill.com/Listings?st=telescope&sg=&c=&s=&lp=0&hp=999999&sbn=False&spo=False&snpo=False&socs=False&sd=False&sca=False&caed=2/3/2019%2012:00:00%20AM&cadb=7&scs=False&sis=False&col=0&p=1&ps=40&desc=False&ss=0&UseBuyerPrefs=true' \
//   -H 'Connection: keep-alive'
//   -H 'Upgrade-Insecure-Requests: 1'
//   -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36' \
//   -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8' \
//   -H 'Referer: https://www.shopgoodwill.com/Listings?st=telescope&sg=&c=&s=&lp=0&hp=999999&sbn=False&spo=False&snpo=False&socs=False&sd=False&sca=False&caed=2/3/2019%2012:00:00%20AM&cadb=7&scs=False&sis=False&col=0&p=2&ps=40&desc=False&ss=0&UseBuyerPrefs=true' \
//   -H 'Accept-Encoding: gzip, deflate, br' \
//   -H 'Accept-Language: en-US,en;q=0.9' \
//   -H 'Cookie: BNI_persistence=00000000000000000000000022c6410a0000bb01; ASP.NET_SessionId=a1rv3qlunmkrbeejpxon2hsd; _ga=GA1.2.1066742913.1547451623; cookieconsent_status=dismiss; _gid=GA1.2.794370924.1549260212; __RequestVerificationToken=tjKrKDq0mVI3WNRqfagoh38dlqrbJoZZ7q65T3EZlzP3SHyHcf4fadOM3rnZW6TP7dvVvE4nEhXb-P9xYAQ-Slzz5KZyRj8aq9861quQlik1; AuthenticatedBuyerToken=1c2b834b-105f-4061-974a-5a411ced7ca6' \
//   --compressed

// Page 2
// curl 'https://www.shopgoodwill.com/Listings?st=telescope&sg=&c=&s=&lp=0&hp=999999&sbn=False&spo=False&snpo=False&socs=False&sd=False&sca=False&caed=2/3/2019%2012:00:00%20AM&cadb=7&scs=False&sis=False&col=0&p=2&ps=40&desc=False&ss=0&UseBuyerPrefs=true' \
//   -H 'Connection: keep-alive' \
//   -H 'Upgrade-Insecure-Requests: 1' \
//   -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8' -H 'Referer: https://www.shopgoodwill.com/Listings?st=telescope&sg=&c=&s=&lp=0&hp=999999&sbn=False&spo=False&snpo=False&socs=False&sd=False&sca=False&caed=2/3/2019%2012:00:00%20AM&cadb=7&scs=False&sis=False&col=0&p=1&ps=40&desc=False&ss=0&UseBuyerPrefs=true' -H 'Accept-Encoding: gzip, deflate, br' -H 'Accept-Language: en-US,en;q=0.9' -H 'Cookie: BNI_persistence=00000000000000000000000022c6410a0000bb01; ASP.NET_SessionId=a1rv3qlunmkrbeejpxon2hsd; _ga=GA1.2.1066742913.1547451623; cookieconsent_status=dismiss; _gid=GA1.2.794370924.1549260212; __RequestVerificationToken=tjKrKDq0mVI3WNRqfagoh38dlqrbJoZZ7q65T3EZlzP3SHyHcf4fadOM3rnZW6TP7dvVvE4nEhXb-P9xYAQ-Slzz5KZyRj8aq9861quQlik1; AuthenticatedBuyerToken=1c2b834b-105f-4061-974a-5a411ced7ca6' --compressed
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


object Download {

    val log: Logger = LoggerFactory.getLogger("Download")

    val cacheDirectory: File = File(createTempDir("telescope").parentFile, "telescope-download")

    val client: OkHttpClient

    init {
        val cacheSize = 10L * 1024 * 1024 // 10 MiB
        val cache = Cache(cacheDirectory, cacheSize)


        val cookieJar = CookieJarMap()

        client = OkHttpClient.Builder()
                .cache(cache)
                .cookieJar(cookieJar)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
    }

    @Throws(IOException::class)
    fun fetchListings(search:String, page:Int): String {
        val url: HttpUrl = HttpUrl.Builder()
                .scheme("https")
                .host("www.shopgoodwill.com")
                .addPathSegment("Listings")
                .addQueryParameter("sl", search)
                .addQueryParameter("p", page.toString())
                .addQueryParameter("hp", "999999")
                .build()
        val request = Request.Builder()
                .url(url)
                .get()
                .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        val responseBody: String = response.body()!!.string()

        log.debug("Response response:          $response" + if(response.cacheResponse() != null) " (Cached)" else "")
        return responseBody
    }
}

fun main(args: Array<String>) {
    val fetchListings = Download.fetchListings("telescope", 1)
    println(fetchListings)
}