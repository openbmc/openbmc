SRC_URI_gbs := "git://github.com/quanta-bmc/phosphor-sel-logger.git"
SRCREV_gbs := "6ded68355db28958836aedb40c0d4780d84d2b43"

# Enable threshold monitoring
EXTRA_OECMAKE_append_gbs = "-DSEL_LOGGER_MONITOR_THRESHOLD_EVENTS=ON"
