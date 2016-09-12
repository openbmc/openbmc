SUMMARY = "Phosphor Rocket startup script"
DESCRIPTION = "Phosphor Rocket startup script."
HOMEPAGE = "http://github.com/openbmc/phosphor-rest-server"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit allarch
inherit setuptools
inherit obmc-phosphor-systemd

PROVIDES += "virtual/obmc-wsgihost"
RPROVIDES_${PN} += "virtual-obmc-wsgihost"

RDEPENDS_${PN} += " \
        python-gevent \
        "
SRC_URI += "git://github.com/openbmc/phosphor-rest-server"

SRCREV = "dc3fbfa7d1861a4bb65fb284228a6ea155ba37a5"

S = "${WORKDIR}/git/servers/gevent"

SYSTEMD_SERVICE_${PN} += "${PN}.service ${PN}.socket"
