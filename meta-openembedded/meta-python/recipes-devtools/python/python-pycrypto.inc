DESCRIPTION = "Cryptographic modules for Python."
HOMEPAGE = "http://www.pycrypto.org/"
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=35f354d199e8cb7667b059a23578e63d"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-pycrypto:"

DEPENDS += " gmp"

inherit pypi autotools-brokensep

SRC_URI += "file://cross-compiling.patch \
            file://CVE-2013-7459.patch \
           "

SRC_URI[md5sum] = "55a61a054aa66812daf5161a0d5d7eda"
SRC_URI[sha256sum] = "f2ce1e989b272cfcb677616763e0a2e7ec659effa67a88aa92b3a65528f60a3c"

do_compile[noexec] = "1"

BBCLASSEXTEND = "native nativesdk"
