SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=58db8ac9e152dd9b700f4d39ff40a31a"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "feb87e0ce1dc1f8f3f21e18a85216c790e746d76a5ff6889563394605f504a2b"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-fastnumbers python3-icu"
