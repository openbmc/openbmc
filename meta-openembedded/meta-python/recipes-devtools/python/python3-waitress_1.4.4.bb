SUMMARY = "A WSGI server for Python"
DESCRIPTION = "Waitress is meant to be a production-quality pure-Python WSGI \
    server with very acceptable performance."
HOMEPAGE = "https://github.com/Pylons/waitress"
SECTION = "devel/python"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

SRC_URI[md5sum] = "079c3c4902b1cb5d0a917276ee70f1df"
SRC_URI[sha256sum] = "1bb436508a7487ac6cb097ae7a7fe5413aefca610550baf58f0940e51ecfb261"

inherit setuptools3 pypi
