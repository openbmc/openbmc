SUMMARY = "WDAT service"
DESCRIPTION = "WDAT service"
PR = "r1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

FILESEXTRAPATHS:append := "${THISDIR}/files:"

inherit cmake

S = "${WORKDIR}"
SRC_URI = "file://CMakeLists.txt \
		  file://wdat.cpp \
		  file://wdat@.service \
		  file://COPYING.MIT \
		  "

FILES:${PN} += "/lib/systemd/system"

do_install() {
    install -d ${D}${sbindir}
    install -Dm755 ${S}/build/wdat ${D}/${sbindir}/wdat
    install -d ${D}/lib/systemd/system
    install -Dm755 ${S}/wdat@.service ${D}/lib/systemd/system
}
