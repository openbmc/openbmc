SUMMARY = "Extremely fast non-cryptographic hash algorithm"
DESCRIPTION = "xxHash is an extremely fast non-cryptographic hash algorithm, \
working at speeds close to RAM limits."
HOMEPAGE = "http://www.xxhash.com/"
LICENSE = "BSD-2-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13be6b481ff5616f77dda971191bb29b \
                    file://cli/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    "

SRC_URI = "git://github.com/Cyan4973/xxHash.git;branch=release;protocol=https"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

SRCREV = "bbb27a5efb85b92a0486cf361a8635715a53f6ba"

S = "${WORKDIR}/git"

CFLAGS += "${@bb.utils.contains('SELECTED_OPTIMIZATION', '-Og', '-DXXH_NO_INLINE_HINTS', '', d)}"

do_compile () {
	oe_runmake all
}

do_install () {
	oe_runmake DESTDIR=${D} install
}

BBCLASSEXTEND = "native nativesdk"
