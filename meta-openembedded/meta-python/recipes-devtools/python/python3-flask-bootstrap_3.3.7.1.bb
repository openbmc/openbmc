DESCRIPTION = "An extension that includes Bootstrap in your project, without any boilerplate code."
LICENSE = "Apache-2.0 & MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=a03749709f06118a17349deb5a210619"

SRC_URI[sha256sum] = "cb08ed940183f6343a64e465e83b3a3f13c53e1baabb8d72b5da4545ef123ac8"

PYPI_PACKAGE = "Flask-Bootstrap"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-dominate \
    python3-flask \
    python3-visitor \
    "
