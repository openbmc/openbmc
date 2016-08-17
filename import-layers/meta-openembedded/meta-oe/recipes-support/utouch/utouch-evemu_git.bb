SUMMARY = "Kernel evdev device emulation"
DESCRIPTION = "The evemu library and tools are used to describe devices, record data, create devices and replay data from kernel evdev devices. "
HOMEPAGE = "http://bitmath.org/code/evemu/"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

inherit autotools

SRC_URI = "git://bitmath.org/git/evemu.git;protocol=http"
SRCREV = "9752b50e922572e4cd214ac45ed95e4ee410fe24"

PV = "1.0.5+git${SRCPV}"

S = "${WORKDIR}/git/"

PARALLEL_MAKE = ""
