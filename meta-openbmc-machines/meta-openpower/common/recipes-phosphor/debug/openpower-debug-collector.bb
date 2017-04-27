SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop"

PR = "r1"

inherit autotools \
        pkgconfig \
        pythonnative \
        obmc-phosphor-systemd

require ${PN}.inc

DEPENDS += " \
        sdbusplus \
        sdbusplus-native \
        python-mako-native \
        phosphor-logging \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += " \
            sdbusplus \
            phosphor-logging \
            "
SYSTEMD_SERVICE_${PN} += "op_checkstop_app.service"

S = "${WORKDIR}/git"
