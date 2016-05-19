SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit allarch
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd
inherit setuptools

DBUS_SERVICES += "org.openbmc.ObjectMapper"
RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        "
SRC_URI += "git://github.com/openbmc/phosphor-objmgr"

SRCREV = "f7aa902e465bfe1cf19b9079f1555633a88e7190"

S = "${WORKDIR}/git"
