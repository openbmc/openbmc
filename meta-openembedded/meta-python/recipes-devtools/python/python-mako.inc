SUMMARY = "A super-fast templating language that borrows the best ideas from the existing templating languages"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1bb21fa2d2f7a534c884b990430a6863"

PYPI_PACKAGE = "Mako"

inherit pypi

SRC_URI[md5sum] = "d739720f3f0d6a1cb6a96ea7a4760c3e"
SRC_URI[sha256sum] = "0728c404877cd4ca72c409c0ea372dc5f3b53fa1ad2bb434e1d216c0444ff1fd"

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
