SUMMARY = "stdexec: experimental P2300 implementation"
HOMEPAGE = "https://github.com/NVIDIA/stdexec"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e982d844baa4df1c80de75470e0c5cb"

PV = "0.11.0+git${SRCPV}"
PR = "r1"

inherit pkgconfig meson

SRC_URI += "git://github.com/NVIDIA/stdexec;branch=main;protocol=https"
SRCREV = "5f94dbac91de3c4869fe695b7fe4d0ed66c0612d"
