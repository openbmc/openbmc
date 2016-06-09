SUMMARY = "Phosphor DBUS REST Server"
DESCRIPTION = "Phosphor DBUS REST manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-rest-server"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools

SYSTEMD_SERVICE_${PN} = "obmc-rest.service obmc-rest.socket"

RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        obmc-mapper \
        python-gevent \
        python-bottle \
        python-spwd \
        python-netserver \
        pyphosphor \
        "
SRC_URI += "git://github.com/bradbishop/phosphor-rest-server"

SRCREV = "7fa86fb39145985e92765a0458ddd7561cabc256"

S = "${WORKDIR}/git"
