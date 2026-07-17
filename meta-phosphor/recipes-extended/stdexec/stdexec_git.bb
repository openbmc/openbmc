SUMMARY = "stdexec: experimental P2300 implementation"
HOMEPAGE = "https://github.com/NVIDIA/stdexec"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e982d844baa4df1c80de75470e0c5cb"

PV = "0.11.0+git${SRCPV}"
PR = "r1"

inherit pkgconfig meson

SRC_URI += "git://github.com/NVIDIA/stdexec;branch=main;protocol=https"
SRCREV = "711da5971a8e8e940763c11bf6bbeb1c1bb22c3a"
