SUMMARY = "OSCam: Open Source Conditional Access Module"
HOMEPAGE = "http://www.streamboard.tv/oscam/"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libusb1 openssl10 pcsc-lite"

SRC_URI = "svn://www.streamboard.tv/svn/oscam;module=trunk;protocol=http \
           file://0001-include-sys-sysmacros.h-for-major-minor-definitions.patch \
           "
SRCREV = "11431"
PV = "1.10+${SRCPV}"

S = "${WORKDIR}/trunk"

inherit cmake

EXTRA_OECMAKE = "-DDEFAULT_CS_CONFDIR=${sysconfdir} -DCMAKE_BUILD_TYPE=Debug"

