DESCRIPTION = "Cryptographic modules for Python."
HOMEPAGE = "http://www.pycrypto.org/"
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=35f354d199e8cb7667b059a23578e63d"

DEPENDS += " gmp"

export HOST_SYS

inherit pypi autotools-brokensep distutils3

SRC_URI += "file://cross-compiling.patch \
            file://CVE-2013-7459.patch \
            file://0001-Replace-time.clock-with-time.process_time.patch \
           "

SRC_URI[md5sum] = "55a61a054aa66812daf5161a0d5d7eda"
SRC_URI[sha256sum] = "f2ce1e989b272cfcb677616763e0a2e7ec659effa67a88aa92b3a65528f60a3c"

do_compile[noexec] = "1"

# We explicitly call distutils_do_install, since we want it to run, but
# *don't* want the autotools install to run, since this package doesn't
# provide a "make install" target.
do_install() {
       distutils3_do_install
}

BBCLASSEXTEND = "native nativesdk"
