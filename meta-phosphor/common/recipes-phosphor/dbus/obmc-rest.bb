SUMMARY = "Phosphor DBUS REST Server"
DESCRIPTION = "Phosphor DBUS REST manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-rest-server"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools

RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        obmc-mapper \
        python-rocket \
        python-bottle \
        python-spwd \
        python-netserver \
        pyphosphor \
        "
SRC_URI += "git://github.com/openbmc/phosphor-rest-server"

SRCREV = "b41507f3b9c9a79ccd0ef6f48ac839b306a604b7"

S = "${WORKDIR}/git"
