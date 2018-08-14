SUMMARY = "Tools for using a Web Server Gateway Interface stack"
HOMEPAGE = "http://pythonpaste.org/"
LICENSE = "MIT"
RDEPENDS_${PN} = "python-six"

LIC_FILES_CHKSUM = "file://docs/license.txt;md5=1798f29d55080c60365e6283cb49779c"

SRC_URI[md5sum] = "1231e14eae62fa7ed76e9130b04bc61e"
SRC_URI[sha256sum] = "2346a347824c32641bf020c17967b49ae74d3310ec1bc9b958d4b84e2d985218"

PYPI_PACKAGE = "Paste"
inherit pypi setuptools

FILES_${PN} += "/usr/lib/*"
