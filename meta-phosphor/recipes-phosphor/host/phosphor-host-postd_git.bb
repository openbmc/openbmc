SUMMARY = "Phosphor OpenBMC Post Code Daemon"
DESCRIPTION = "Phosphor OpenBMC Post Code Daemon"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-postd"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson
inherit pkgconfig
inherit systemd

PACKAGECONFIG ?= ""
PACKAGECONFIG[7seg] = "-D7seg=enabled,-D7seg=disabled,,udev"

DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "systemd"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-host-postd"
SRCREV = "ba5258f7158e8a0e61043290aff0873358509158"

SNOOP_DEVICE ?= "aspeed-lpc-snoop0"
POST_CODE_BYTES ?= "1"

SERVICE_FILE = "lpcsnoop.service"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} += "${SERVICE_FILE}"

EXTRA_OEMESON += "-Dsnoop-device=${SNOOP_DEVICE}"
EXTRA_OEMESON += "-Dpost-code-bytes=${POST_CODE_BYTES}"
EXTRA_OEMESON += "-Dsystemd-target=multi-user.target"

POSTCODE_SEVENSEG_DEVICE ?= "seven_seg_disp_val"
SERVICE_FILE_7SEG = " \
  postcode-7seg@.service \
  postcode-7seg@${POSTCODE_SEVENSEG_DEVICE}.service \
"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', '7seg', '${SERVICE_FILE_7SEG}', '', d)}"
