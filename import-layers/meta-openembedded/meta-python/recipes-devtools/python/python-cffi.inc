SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi ${PYTHON_PN}-pycparser"

SRC_URI[md5sum] = "a731487324b501c8295221b629d3f5f3"
SRC_URI[sha256sum] = "ab87dd91c0c4073758d07334c1e5f712ce8fe48f007b86f8238773963ee700a6"

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-io \
"

BBCLASSEXTEND = "native nativesdk"
