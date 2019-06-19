SUMMARY = "A super-fast templating language that borrows the best ideas from the existing templating languages"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df7e6c7c82990acf0228a55e00d29bc9"

PYPI_PACKAGE = "Mako"

inherit pypi

SRC_URI[md5sum] = "4a7aef27217a8e6c1a29447c9b1b00ab"
SRC_URI[sha256sum] = "0cfa65de3a835e87eeca6ac856b3013aade55f49e32515f65d999f91a2324162"

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
