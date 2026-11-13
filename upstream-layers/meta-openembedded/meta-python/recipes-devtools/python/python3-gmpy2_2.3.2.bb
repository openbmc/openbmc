SUMMARY = "GMP/MPIR, MPFR, and MPC interface to Python 2.6+ and 3.x"
SECTION = "devel/python"
HOMEPAGE = "https://github.com/gmpy2/gmpy2"
LICENSE = "GPL-3.0-only OR LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS += "gmp mpfr libmpc python3-setuptools-scm-native"

SRC_URI[sha256sum] = "f20b7e2f8fd16f8d6846bb5b73359c3cc5aa41ec5cf266321d362f547c8fd097"

SRC_URI += "file://0001-Avoid-do_configure-requires-check-error.patch"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
