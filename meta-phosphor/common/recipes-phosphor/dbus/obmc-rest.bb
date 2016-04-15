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
        "
SRC_URI += "git://github.com/openbmc/phosphor-rest-server"

SRCREV = "fe90e0c579edbc19699e5aa7abbe4dd29b4dd112"

S = "${WORKDIR}/git"
