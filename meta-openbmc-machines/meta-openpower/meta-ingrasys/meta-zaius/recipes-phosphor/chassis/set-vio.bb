DESCRIPTION = "Zaius set VIO rails voltage"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

SYSTEMD_SERVICE_${PN} += "set-vio.service"

RDEPENDS_${PN} += "i2c-tools"
