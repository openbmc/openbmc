SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=c9ba5acd829579492fd5eb306c7a0565"

SRC_URI[md5sum] = "c706e2790b016bd0ed4edd2d4ba4d147"
SRC_URI[sha256sum] = "2c6a5de3089009e3da7c5dde64a141dbc8551d5b7f6cf4ed7c2568d0cc520a8f"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-codecs \
"

BBCLASSEXTEND = "native nativesdk"
