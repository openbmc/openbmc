SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[md5sum] = "e10e7c8c97e5bade5e85ce6e89bdf1f4"
SRC_URI[sha256sum] = "ba9e1d60a89fd7ed075553b4a2074352902203f7fbd9b65b15048c05f0e3f3be"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
