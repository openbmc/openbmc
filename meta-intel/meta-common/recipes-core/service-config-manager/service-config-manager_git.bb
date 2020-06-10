SUMMARY = "Service configuration manager daemon to control service properties"
DESCRIPTION = "Daemon controls service properies like port, channels, state etc.."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake systemd

SRC_URI = "git://github.com/openbmc/service-config-manager"

PV = "0.1+git${SRCPV}"
SRCREV = "83241c09ec5acfb894da08c10dfd574034c33ff9"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "srvcfg-manager.service"

DEPENDS += " \
    systemd \
    sdbusplus \
    sdbusplus-native \
    phosphor-logging \
    boost \
    "
RDEPENDS_${PN} += " \
    libsystemd \
    sdbusplus \
    phosphor-logging \
    "
