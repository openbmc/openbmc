SUMMARY = "OSCam: Open Source Conditional Access Module"
HOMEPAGE = "http://www.streamboard.tv/oscam/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libusb1 openssl pcsc-lite"

SRC_URI = "svn://www.streamboard.tv/svn/oscam;module=trunk;protocol=http \
           "
SRCREV = "11718"
PV = "1.10+"

S = "${WORKDIR}/trunk"

inherit cmake

EXTRA_OECMAKE = "-DDEFAULT_CS_CONFDIR=${sysconfdir} -DCMAKE_BUILD_TYPE=Debug"

do_configure:append() {
    sed -i -e '1 s|${TOPDIR}|<TOPDIR>|g' ${B}/config.c
}
