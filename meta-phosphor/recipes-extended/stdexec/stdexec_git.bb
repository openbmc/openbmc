SUMMARY = "stdexec: experimental P2300 implementation"
HOMEPAGE = "https://github.com/NVIDIA/stdexec"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e982d844baa4df1c80de75470e0c5cb"

PV = "0.11.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit pkgconfig meson

SRC_URI += "git://github.com/NVIDIA/stdexec;branch=main;protocol=https"
SRCREV = "36a92fd776c835abd4dc5e62d43cf040c20a9add"
