SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32431d1b650010945da4e078011c8fa"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "517595492dde570a4fd6b6a76f644440c1ba51e2338c8a671d7f0475fda8f9fd"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-fastnumbers python3-icu"
