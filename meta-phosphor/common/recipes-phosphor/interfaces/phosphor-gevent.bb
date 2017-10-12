SUMMARY = "Phosphor Rocket startup script"
DESCRIPTION = "Phosphor Rocket startup script."
PR = "r1"

inherit allarch
inherit setuptools
inherit obmc-phosphor-systemd

require phosphor-rest.inc

PROVIDES += "virtual/obmc-wsgihost"
RPROVIDES_${PN} += "virtual-obmc-wsgihost"

RDEPENDS_${PN} += " \
        python-gevent \
        "
RRECOMMENDS_${PN} += "python-gevent-websocket"


S = "${WORKDIR}/git/servers/gevent"

SYSTEMD_SERVICE_${PN} += "${PN}.service ${PN}.socket"
