SUMMARY = "The squish library (abbreviated to libsquish) is an open source DXT compression library written in C++ "

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://alpha.cpp;beginline=3;endline=22;md5=6665e479f71feb92d590ea9ae9b9f6d5"

PV = "1.10+git${SRCPV}"

SRCREV = "52e7d93c5947f72380521116c05d97c528863ba8"
SRC_URI = "git://github.com/OpenELEC/libsquish.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "INSTALL_DIR=${D}${prefix}"

do_install() {
	install -d ${D}${includedir}
	install -d ${D}${libdir}/pkgconfig
	oe_runmake install 
}
