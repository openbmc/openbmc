SUMMARY = "Foreign Function Interface for Python calling C code"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi ${PYTHON_PN}-pycparser"

SRC_URI[md5sum] = "b8fa7ccb87790531db3316ab17aa8244"
SRC_URI[sha256sum] = "563e0bd53fda03c151573217b3a49b3abad8813de9dd0632e10090f6190fdaf8"

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-io \
"

BBCLASSEXTEND = "native nativesdk"
