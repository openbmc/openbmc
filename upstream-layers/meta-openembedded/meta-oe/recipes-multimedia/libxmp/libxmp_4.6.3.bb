SUMMARY = "Extended Module Player Library"
HOMEPAGE = "http://xmp.sourceforge.net/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=59;md5=ea030bd80f99071b0d3f9a9f752d1ca8"

inherit cmake pkgconfig

SRC_URI = "git://github.com/libxmp/libxmp.git;protocol=https;branch=master;tag=libxmp-${PV}"
SRCREV = "bed660f8e530d399c38f27a5a7732f4e79740585"

