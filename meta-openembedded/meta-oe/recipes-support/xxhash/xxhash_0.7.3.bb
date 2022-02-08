SUMMARY = "Extremely fast non-cryptographic hash algorithm"
DESCRIPTION = "xxHash is an extremely fast non-cryptographic hash algorithm, \
working at speeds close to RAM limits."
HOMEPAGE = "http://www.xxhash.com/"
LICENSE = "BSD-2-Clause & GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01a7eba4212ef1e882777a38585e7a9b"

SRC_URI = "git://github.com/Cyan4973/xxHash.git;branch=master;protocol=https"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

SRCREV = "d408e9b0606d07b1ddc5452ffc0ec8512211b174"

S = "${WORKDIR}/git"

do_compile () {
	oe_runmake all
}

do_install () {
	oe_runmake DESTDIR=${D} install
}
