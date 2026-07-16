SUMMARY = "Extended Module Player Library"
HOMEPAGE = "http://xmp.sourceforge.net/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=59;md5=b25470e2863502c9ac9886f09c21a8f9"

inherit cmake pkgconfig

SRC_URI = "git://github.com/libxmp/libxmp.git;protocol=https;branch=master;tag=libxmp-${PV}"
SRCREV = "12f76e4bf1dd6765e3bf46cbc1ff35c4a4551671"

