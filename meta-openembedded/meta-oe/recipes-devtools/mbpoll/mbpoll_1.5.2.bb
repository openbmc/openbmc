SUMMARY = "Command line utility to communicate with ModBus slave (RTU or TCP)"
DESCRIPTION = "mbpoll can: read discrete inputs; read and write binary outputs \
(coil); read input registers; read and write output registers (holding register). \
The reading and writing registers may be in decimal, hexadecimal or floating single \
precision."
LICENSE = "GPL-3.0-only"
HOMEPAGE = "https://github.com/epsilonrt/mbpoll"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"
DEPENDS = "libmodbus (>=3.1.4)"

SRC_URI = "git://github.com/epsilonrt/mbpoll;protocol=https;branch=master"
SRCREV = "a0bd6c08d3d15b086f2104477295c0705aed366a"

S = "${WORKDIR}/git"

inherit pkgconfig cmake
