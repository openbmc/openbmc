SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19de1f406e29e68f579e7e82e0758ce3"

SRC_URI[sha256sum] = "cc1c8b182eb3013e24bd475ff2e9295af86c1a38eb1aff128dac8962a9ce3c03"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "tar.gz"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-core \
"

BBCLASSEXTEND = "native nativesdk"
