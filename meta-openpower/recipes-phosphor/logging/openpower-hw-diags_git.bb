inherit meson systemd

SUMMARY = "ATTN and HwDiags Support"
DESCRIPTION = "Attention Handler and Hardware Diagnostics"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/openpower-hw-diags"

SYSTEMD_SERVICE_${PN} = "attn_handler.service"
PV = "0.1+git${SRCPV}"
SRCREV = "ff5f9a2dfb9f72dc2d76cf1b50c8f980e8823919"

S = "${WORKDIR}/git"

DEPENDS = "boost libgpiod pdbg phosphor-logging sdbusplus openpower-libhei"
FILES_${PN} += "${UNITDIR}/attn_handler.service"

# This is required so that libhei is installed with the chip data files.
RDEPENDS_${PN} += "openpower-libhei"
