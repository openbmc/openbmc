SUMMARY = "MyPaint brushes"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=65d3616852dbf7b1a6d4b53b00626032"

inherit autotools allarch

SRC_URI = "git://github.com/mypaint/mypaint-brushes.git;protocol=https;branch=v1.3.x"
SRCREV = "be9fdf9ef6c54e29c7499992f04e29114857b3fc"
PV = "1.3.0+git${SRCPV}"
S = "${WORKDIR}/git"

FILES_${PN} += "${datadir}/mypaint-data"
