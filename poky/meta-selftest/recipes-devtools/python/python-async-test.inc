SUMMARY = "Python framework to process interdependent tasks in a pool of workers"
HOMEPAGE = "http://github.com/gitpython-developers/async"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=88df8e78b9edfd744953862179f2d14e"

inherit pypi

PYPI_PACKAGE = "async"

SRC_URI[md5sum] = "9b06b5997de2154f3bc0273f80bcef6b"
SRC_URI[sha256sum] = "ac6894d876e45878faae493b0cf61d0e28ec417334448ac0a6ea2229d8343051"

RDEPENDS:${PN} += "${PYTHON_PN}-threading"

BBCLASSEXTEND = "nativesdk"
