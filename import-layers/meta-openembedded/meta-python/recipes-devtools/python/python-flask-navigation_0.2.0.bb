DESCRIPTION = "The navigation of Flask application."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=af2ec695dc1f3eb7bd74f79a68a0d789"

SRC_URI[md5sum] = "d1075ee02a3f69da37e5cadad3395f31"
SRC_URI[sha256sum] = "c42d30efa989ca877444a410e8a1cd2563546f9effe3d9fe388eedf7a6c69285"

PYPI_PACKAGE = "Flask-Navigation"

inherit pypi setuptools

RDEPENDS_${PN} = "python-blinker"
