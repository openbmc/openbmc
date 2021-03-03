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

DEPENDS += "systemd sdbusplus boost phosphor-ipmi-host"

SRC_URI = "git://github.com/openbmc/phosphor-sel-logger.git;protocol=git"
SRCREV = "aaffc124b6f49d9bc267e65565bdd5d4c1db1aaf"

PV = "0.1+git${SRCPV}"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.Logging.IPMI.service"
