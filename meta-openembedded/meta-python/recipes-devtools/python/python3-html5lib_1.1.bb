SUMMARY = "HTML parser based on the WHATWG HTML specifcation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ba5ada9e6fead1fdc32f43c9f10ba7c"

SRC_URI[md5sum] = "6748742e2ec4cb99287a6bc82bcfe2b0"
SRC_URI[sha256sum] = "b2e5b40261e20f354d198eae92afc10d750afb487ed5e50f9c4eaf07c184146f"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-webencodings \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
