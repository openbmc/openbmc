SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a41509b57cc475ed93f8cb1dbbfaeec1"

SRC_URI[sha256sum] = "3ebe3c479ad625c4553aca177444d89b486b1d84982eeacded644afc0cf797ca"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "tar.gz"

RDEPENDS:${PN} += "\
    python3-multiprocessing \
    python3-logging \
    python3-profile \
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
