SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=58db8ac9e152dd9b700f4d39ff40a31a"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "57f85b72c688b09e053cdac302dd5b5b53df5f73ae20b4874fcbffd8bf783d11"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-fastnumbers python3-icu"
