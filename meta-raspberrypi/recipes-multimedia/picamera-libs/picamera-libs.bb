SUMMARY = "Raspberrypi firmware libraries which are required by picamera library"
DESCRIPTION = "Raspberrypi firmware libraries required by picamera library"
LICENSE = "Broadcom-RPi"

LIC_FILES_CHKSUM = "file://opt/vc/LICENCE;md5=86e53f5f5909ee66900418028de11780"

include recipes-bsp/common/raspberrypi-firmware.inc

S = "${WORKDIR}/firmware-${SRCREV}"

do_install(){
    install -m 0755 -d ${D}${libdir}
    install -m 0755 ${S}/opt/vc/lib/*.so ${D}${libdir}
}

FILES:${PN} = "${libdir}"

#skipping the QA error since we are directly copying precompiled binaries
INSANE_SKIP:${PN} = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

COMPATIBLE_HOST = "null"
COMPATIBLE_HOST:rpi:libc-glibc = "(arm.*)-linux"
