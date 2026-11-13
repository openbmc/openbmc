SUMMARY = "Extended Module Player Library"
HOMEPAGE = "http://xmp.sourceforge.net/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=59;md5=b25470e2863502c9ac9886f09c21a8f9"

inherit cmake pkgconfig

SRC_URI = "git://github.com/libxmp/libxmp.git;protocol=https;branch=master;tag=libxmp-${PV}"
SRCREV = "5d4f6aa2c7dcdf8e9cd8129fe579d45b36a302da"

