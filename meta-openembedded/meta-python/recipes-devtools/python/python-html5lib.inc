SUMMARY = "HTML parser based on the WHATWG HTML specifcation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ba5ada9e6fead1fdc32f43c9f10ba7c"

SRC_URI[md5sum] = "942a0688d6bdf20d087c9805c40182ad"
SRC_URI[sha256sum] = "66cb0dcfdbbc4f9c3ba1a63fdb511ffdbd4f513b2b6d81b80cd26ce6b3fb3736"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-webencodings \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
