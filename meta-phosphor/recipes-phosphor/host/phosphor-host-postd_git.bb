SUMMARY = "Phosphor OpenBMC Post Code Daemon"
DESCRIPTION = "Phosphor OpenBMC Post Code Daemon"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-postd"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig
inherit systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "systemd"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-host-postd"
SRCREV = "14c5d6f7aff84c23d641b2add6f3d2c95743c508"

SNOOP_DEVICE ?= "aspeed-lpc-snoop0"
POST_CODE_BYTES ?= "1"

SERVICE_FILE = "lpcsnoop.service"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} += "${SERVICE_FILE}"
SYSTEMD_DEFAULT_TARGET ?= "obmc-standby.target"

EXTRA_OECONF = " \
  SNOOP_DEVICE="${SNOOP_DEVICE}" \
  POST_CODE_BYTES="${POST_CODE_BYTES}" \
  SYSTEMD_TARGET="${SYSTEMD_DEFAULT_TARGET}" \
"
