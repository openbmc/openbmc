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
        python-rocket \
        "
SRC_URI += "git://github.com/bradbishop/phosphor-rest-server;branch=gevent"

SRCREV = "2c6fc760919cc214413874d60489e3643b639692"

S = "${WORKDIR}/git/servers/rocket"
