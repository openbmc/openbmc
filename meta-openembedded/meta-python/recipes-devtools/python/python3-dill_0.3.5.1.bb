SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61f24e44fc855bde43ed5a1524a37bc4"

SRC_URI[sha256sum] = "d75e41f3eff1eee599d738e76ba8f4ad98ea229db8b085318aa2b3333a208c86"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "tar.gz"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-logging \
"
