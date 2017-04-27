SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop"

PR = "r1"

inherit autotools \
        pkgconfig \

require ${PN}.inc

DEPENDS += " \
        phosphor-logging \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += " \
            phosphor-logging \
            "
SYSTEMD_SERVICE_${PN} += "op_checkstop_app.service"

S = "${WORKDIR}/git"
