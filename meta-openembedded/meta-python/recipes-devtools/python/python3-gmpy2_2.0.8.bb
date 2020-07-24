SUMMARY = "GMP/MPIR, MPFR, and MPC interface to Python 2.6+ and 3.x"
SECTION = "devel/python"
LICENSE = "GPL-3.0 | LGPL-3.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS += "gmp mpfr libmpc"

PYPI_PACKAGE = "gmpy2"
PYPI_PACKAGE_EXT = "zip"
SRC_URI[sha256sum] = "dd233e3288b90f21b0bb384bcc7a7e73557bb112ccf0032ad52aa614eb373d3f"

inherit pypi setuptools3
