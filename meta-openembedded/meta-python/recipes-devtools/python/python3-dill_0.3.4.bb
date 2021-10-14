SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41adceb584fdde8323ddf9ad23c07fe5"

SRC_URI[sha256sum] = "9f9734205146b2b353ab3fec9af0070237b6ddae78452af83d2fca84d739e675"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-multiprocessing \
"
