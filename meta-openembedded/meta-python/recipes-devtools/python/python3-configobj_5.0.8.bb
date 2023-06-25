SUMMARY = "Config file reading, writing and validation."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d6f99b84d9a94610c62e48fa2e59e72"

PYPI_PACKAGE = "configobj"
SRC_URI[sha256sum] = "6f704434a07dc4f4dc7c9a745172c1cad449feb548febd9f7fe362629c627a97"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-pprint \
    python3-six \
"
