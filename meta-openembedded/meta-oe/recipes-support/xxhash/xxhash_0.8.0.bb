SUMMARY = "Extremely fast non-cryptographic hash algorithm"
DESCRIPTION = "xxHash is an extremely fast non-cryptographic hash algorithm, \
working at speeds close to RAM limits."
HOMEPAGE = "http://www.xxhash.com/"
LICENSE = "BSD-2-Clause & GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b335320506abb0505437e39295e799cb"

SRC_URI = "git://github.com/Cyan4973/xxHash.git;branch=release;protocol=git"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

SRCREV = "94e5f23e736f2bb67ebdf90727353e65344f9fc0"

S = "${WORKDIR}/git"

do_compile () {
	oe_runmake all
}

do_install () {
	oe_runmake DESTDIR=${D} install
}
