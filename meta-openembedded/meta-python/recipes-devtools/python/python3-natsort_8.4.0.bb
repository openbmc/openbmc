SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32431d1b650010945da4e078011c8fa"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "45312c4a0e5507593da193dedd04abb1469253b601ecaf63445ad80f0a1ea581"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-fastnumbers python3-icu"
