SUMMARY = "Python module to interface with the pkg-config command line too"
HOMEPAGE = "http://github.com/matze/pkgconfig"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faa7f82be8f220bff6156be4790344fc"

SRC_URI = "git://github.com/matze/pkgconfig.git;branch=master;protocol=https"
SRCREV ?= "8af0102346847e8873af8e76ab3f34ba9da806e2"

RDEPENDS_${PN} = "pkgconfig \
                 ${PYTHON_PN}-shell \
                 "

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

