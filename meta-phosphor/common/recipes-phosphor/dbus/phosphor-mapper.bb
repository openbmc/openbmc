SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit pythonnative
inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"

DBUS_SERVICE_${PN} += "org.openbmc.ObjectMapper.service"
SYSTEMD_SERVICE_${PN} = "mapper-wait@.service"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        python-pygobject \
        "
SRC_URI += "git://github.com/openbmc/phosphor-objmgr"

SRCREV = "7122244c83092499dc8d7836da0a63a08c734856"

S = "${WORKDIR}/git"

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"
