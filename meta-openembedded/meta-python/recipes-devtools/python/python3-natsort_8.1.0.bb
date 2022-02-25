SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=58db8ac9e152dd9b700f4d39ff40a31a"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "c7c1f3f27c375719a4dfcab353909fe39f26c2032a062a8c80cc844eaaca0445"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-fastnumbers python3-icu"
