SUMMARY = "Journal IPMI SEL Logger"
DESCRIPTION = "Utility to write IPMI SEL records to the journal"

# SEL Logger has the ability to monitor and automatically log SEL records for
# various types of events, but this is disabled by default.  The following
# flags can be set in a .bbappend to enable specific types of event
# monitoring:
#
#   SEL_LOGGER_MONITOR_THRESHOLD_EVENTS:
#      Monitors and logs SEL records for threshold sensor events

inherit cmake systemd
S = "${WORKDIR}/git"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS += " \
  boost \
  sdbusplus \
  systemd \
  phosphor-logging \
  "

SRC_URI = "git://github.com/openbmc/phosphor-sel-logger.git;protocol=git"
SRCREV = "e9da2599f10f1b696f61c97f8c8036acfde05e18"

PV = "0.1+git${SRCPV}"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.Logging.IPMI.service"

PACKAGECONFIG ??= ""
PACKAGECONFIG[log-threshold] = "-DSEL_LOGGER_MONITOR_THRESHOLD_EVENTS=ON,-DSEL_LOGGER_MONITOR_THRESHOLD_EVENTS=OFF,"
PACKAGECONFIG[log-pulse] = "-DREDFISH_LOG_MONITOR_PULSE_EVENTS=ON,-DREDFISH_LOG_MONITOR_PULSE_EVENTS=OFF,"
PACKAGECONFIG[log-watchdog] = "-DSEL_LOGGER_MONITOR_WATCHDOG_EVENTS=ON,-DSEL_LOGGER_MONITOR_WATCHDOG_EVENTS=OFF,"
PACKAGECONFIG[log-alarm] = "-DSEL_LOGGER_MONITOR_THRESHOLD_ALARM_EVENTS=ON,-DSEL_LOGGER_MONITOR_THRESHOLD_ALARM_EVENTS=OFF,"
PACKAGECONFIG[send-to-logger] = "-DSEL_LOGGER_SEND_TO_LOGGING_SERVICE=ON,-DSEL_LOGGER_SEND_TO_LOGGING_SERVICE=OFF,phosphor-logging"
