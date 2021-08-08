DESCRIPTION = "This is a library for polling gpsd in Python3"
HOMEPAGE = "https://github.com/MartijnBraam/gpsd-py3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=10;endline=10;md5=c2d9994c57f0444e39f1dab19af50254"
SRC_URI[md5sum] = "041ce56e8879e2104b4d54c8119cd529"
SRC_URI[sha256sum] = "2908d3bd78dfb6720ecfe22f97e139b5a4a198f38df3a77215cf644a33513192"

PYPI_PACKAGE = "gpsd-py3"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    python3 \
"
