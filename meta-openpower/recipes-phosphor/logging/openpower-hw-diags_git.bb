inherit meson systemd

SUMMARY = "ATTN and HwDiags Support"
DESCRIPTION = "Attention Handler and Hardware Diagnostics"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/openpower-hw-diags"

SYSTEMD_SERVICE_${PN} = "attn_handler.service"
PV = "0.1+git${SRCPV}"
SRCREV = "5e622d870609b79b9a210596a42e4a110f210d99"

S = "${WORKDIR}/git"

DEPENDS = "boost libgpiod pdbg phosphor-logging sdbusplus openpower-libhei"
FILES_${PN} += "${UNITDIR}/attn_handler.service"
