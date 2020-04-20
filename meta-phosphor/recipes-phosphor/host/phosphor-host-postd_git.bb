SUMMARY = "Phosphor OpenBMC Post Code Daemon"
DESCRIPTION = "Phosphor OpenBMC Post Code Daemon"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-postd"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig
inherit systemd

PACKAGECONFIG ?= ""
PACKAGECONFIG[7seg] = "--enable-7seg,--disable-7seg,,udev"

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "systemd"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-host-postd"
SRCREV = "66efa632e25854ed5fba0166fe5c3bdd6761f20b"

SNOOP_DEVICE ?= "aspeed-lpc-snoop0"
POST_CODE_BYTES ?= "1"

SERVICE_FILE = "lpcsnoop.service"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} += "${SERVICE_FILE}"

EXTRA_OECONF = " \
  SNOOP_DEVICE="${SNOOP_DEVICE}" \
  POST_CODE_BYTES="${POST_CODE_BYTES}" \
  SYSTEMD_TARGET="multi-user.target" \
"

POSTCODE_SEVENSEG_DEVICE ?= "seven_seg_disp_val"
SERVICE_FILE_7SEG = " \
  postcode-7seg@.service \
  postcode-7seg@${POSTCODE_SEVENSEG_DEVICE}.service \
"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', '7seg', '${SERVICE_FILE_7SEG}', '', d)}"
