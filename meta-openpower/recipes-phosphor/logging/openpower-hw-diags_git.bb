inherit meson systemd

SUMMARY = "ATTN and HwDiags Support"
DESCRIPTION = "Attention Handler and Hardware Diagnostics"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/openpower-hw-diags"

SYSTEMD_SERVICE_${PN} = "attn_handler.service"
PV = "0.1+git${SRCPV}"
SRCREV = "8af9e46f43d2ffa030275d17716571d4fcc8ad18"

S = "${WORKDIR}/git"

DEPENDS = "boost libgpiod pdbg phosphor-logging sdbusplus openpower-libhei \
           nlohmann-json"

FILES_${PN} += "${UNITDIR}/attn_handler.service"

# This is required so that libhei is installed with the chip data files.
RDEPENDS_${PN} += "openpower-libhei"
