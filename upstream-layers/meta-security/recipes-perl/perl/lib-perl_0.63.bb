DESCRIPTION = "This is a small simple module which simplifies the \
manipulation of @INC at compile time. It is typically used to add extra \
directories to Perl's search path so that later 'use' or 'require' statements \
will find modules which are not located in the default search path."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
PR = "r0"

LIC_FILES_CHKSUM = "file://README;beginline=26;endline=30;md5=94b119f1a7b8d611efc89b5d562a1a50"

DEPENDS += "perl"

SRC_URI = "http://www.cpan.org/authors/id/S/SM/SMUELLER/lib-${PV}.tar.gz"

SRC_URI[md5sum] = "8607ac4e0d9d43585ec28312f52df67c"
SRC_URI[sha256sum] = "72f63db9220098e834d7a38231626bd0c9b802c1ec54a628e2df35f3818e5a00"

S = "${UNPACKDIR}/lib-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_install:append() {
   # Man pages here conflict wtih the main perl documentation
   for page in ${D}${mandir}/man*/*; do
        mv $page $(dirname $page)/${BPN}-$(basename $page)
    done
}
