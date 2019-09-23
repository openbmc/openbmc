SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e35fd9f271d19d5f742f20a9d1f8bb8b"

SRC_URI[md5sum] = "1bbc1788588cc3c2c461cd98a830ba72"
SRC_URI[sha256sum] = "863a85c1c0a5103a12c05a35e59d336e1d665747e531256e061213e2e90f63f3"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/test* \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/__pycache__/test* \
"


RDEPENDS_${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
