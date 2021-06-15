# Enable threshold monitoring
EXTRA_OECMAKE_append_gbs = "-DSEL_LOGGER_SEND_TO_LOGGING_SERVICE=ON \
                            -DSEL_LOGGER_MONITOR_THRESHOLD_ALARM_EVENTS=ON \
                           "
