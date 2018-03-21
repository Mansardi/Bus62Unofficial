package transport.client.bus62unofficial.common

/**
 * Created by Denis on 20.03.2018.
 */
object Constants {
    const val TASK_TICK: Long = 500
    const val TIME_TICK = 500
    const val TASK_DURATION = 1000 // 1 seconds

    object Requests {
        const val STATIONS = "getStations.php?city=ryazan&info=12345"
        const val STATION_FORECAST = "getStationForecasts.php?sid=%S&type=0&city=ryazan"
    }

    const val HTTP_ERROR = "Ошибка соединения"
}