SUMMARY = "A WSGI server for Python"
DESCRIPTION = "Waitress is meant to be a production-quality pure-Python WSGI \
    server with very acceptable performance."
HOMEPAGE = "https://github.com/Pylons/waitress"
SECTION = "devel/python"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

SRC_URI[md5sum] = "4bffad7009d3824ae61ea6c0696e45f6"
SRC_URI[sha256sum] = "045b3efc3d97c93362173ab1dfc159b52cfa22b46c3334ffc805dbdbf0e4309e"

inherit setuptools3 pypi
