SUMMARY = "Extended Module Player Library"
HOMEPAGE = "http://xmp.sourceforge.net/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=59;md5=b25470e2863502c9ac9886f09c21a8f9"

inherit cmake pkgconfig

SRC_URI = "git://github.com/libxmp/libxmp.git;protocol=https;branch=master;tag=libxmp-${PV}"
SRCREV = "a13276d27feabcf9ee4f982913f718ee05a65cb7"

