SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd13dafd4eeb0802bb6efea6b4a4bdbc"

SRC_URI[sha256sum] = "511f5b0804e92dd77dc33adf9c947787e3f9e9c5a96b12162f0557a7c4ce21fb"

PYPI_PACKAGE = "Pyro4"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-logging \
    python3-serpent \
    python3-threading \
    "
