SUMMARY = "Phosphor OpenBMC Post Code Daemon"
DESCRIPTION = "Phosphor OpenBMC Post Code Daemon"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-postd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "systemd"
DEPENDS += "libgpiod"
SRCREV = "d2d5a95934a340f194c6dffb7777a51c6357ebbb"
PACKAGECONFIG ?= ""
PACKAGECONFIG[7seg] = "-D7seg=enabled,-D7seg=disabled,,udev"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-host-postd;branch=master;protocol=https"

S = "${WORKDIR}/git"
SNOOP_DEVICE ?= "aspeed-lpc-snoop0"
SERVICE_FILE = "lpcsnoop.service"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "${SERVICE_FILE}"
SERVICE_FILE_7SEG = " \
  postcode-7seg@.service \
  postcode-7seg@${POSTCODE_SEVENSEG_DEVICE}.service \
"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', '7seg', '${SERVICE_FILE_7SEG}', '', d)}"

inherit meson
inherit pkgconfig
inherit systemd

EXTRA_OEMESON:append = " \
    -Dsnoop-device=${SNOOP_DEVICE} \
    -Dpost-code-bytes=${POST_CODE_BYTES} \
    -Dtests=disabled \
"

POST_CODE_BYTES ?= "1"
7SEG_GPIO ?= "0"
POSTCODE_SEVENSEG_DEVICE ?= "seven_seg_disp_val"
