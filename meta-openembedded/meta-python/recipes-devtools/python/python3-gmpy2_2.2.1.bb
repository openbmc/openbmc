SUMMARY = "GMP/MPIR, MPFR, and MPC interface to Python 2.6+ and 3.x"
SECTION = "devel/python"
LICENSE = "GPL-3.0-only | LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS += "gmp mpfr libmpc"

SRC_URI[sha256sum] = "e83e07567441b78cb87544910cb3cc4fe94e7da987e93ef7622e76fb96650432"

inherit pypi python_setuptools_build_meta python3native

BBCLASSEXTEND = "native nativesdk"
