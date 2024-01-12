SUMMARY = "GMP/MPIR, MPFR, and MPC interface to Python 2.6+ and 3.x"
SECTION = "devel/python"
LICENSE = "GPL-3.0-only | LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS += "gmp mpfr libmpc"

PYPI_PACKAGE = "gmpy2"
SRC_URI[sha256sum] = "bc297f1fd8c377ae67a4f493fc0f926e5d1b157e5c342e30a4d84dc7b9f95d96"

inherit pypi setuptools3 python3native
SRC_URI += "file://0001-src-fix-python-3.12-builds.patch"

BBCLASSEXTEND = "native nativesdk"
