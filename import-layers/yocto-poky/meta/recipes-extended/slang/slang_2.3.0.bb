SUMMARY = "The shared library for the S-Lang extension language"

DESCRIPTION = "S-Lang is an interpreted language and a programming library.  The \
S-Lang language was designed so that it can be easily embedded into \
a program to provide the program with a powerful extension language. \
The S-Lang library, provided in this package, provides the S-Lang \
extension language.  S-Lang's syntax resembles C, which makes it easy \
to recode S-Lang procedures in C if you need to."

HOMEPAGE = "http://www.jedsoft.org/slang/"
SECTION = "libs"
DEPENDS = "ncurses virtual/libiconv"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a52a18a472d4f7e45479b06563717c02"


SRC_URI = "http://www.jedsoft.org/releases/${BPN}/${BP}.tar.bz2 \
           file://rpathfix.patch \
           file://fix-check-pcre.patch \
           file://slang-fix-the-iconv-existence-checking.patch \
           file://0001-Fix-error-conflicting-types-for-posix_close.patch \
           file://no-x.patch \
          "
SRC_URI[md5sum] = "3bcc790460d52db1316c20395b7ac2f1"
SRC_URI[sha256sum] = "f95224060f45e0d8212a5039b339afa5f1a94a1bb0298e796104e5b12e926129"

UPSTREAM_CHECK_URI = "http://www.jedsoft.org/releases/slang/"
PREMIRRORS_append = "\n http://www.jedsoft.org/releases/slang/.* http://www.jedsoft.org/releases/slang/old/ \n"

inherit autotools-brokensep
CLEANBROKEN = "1"

EXTRA_OECONF = "--without-onig"

PACKAGECONFIG ??= "pcre"
PACKAGECONFIG[pcre] = "--with-pcre,--without-pcre,pcre"
PACKAGECONFIG[png] = "--with-png,--without-png,libpng"
PACKAGECONFIG[zlib] = "--with-z,--without-z,zlib"

do_configure_prepend() {
    # slang keeps configure.ac and rest of autoconf files in autoconf/ directory
    # we have to go there to be able to run gnu-configize cause it expects configure.{in,ac}
    # to be present. Resulting files land in autoconf/autoconf/ so we need to move them.
    cd ${S}/autoconf && gnu-configize --force && mv autoconf/config.* .
    cd ${B}
}

do_install() {
	oe_runmake install DESTDIR=${D} -e 'INST_LIB_DIR=${STAGING_DIR_HOST}/usr/lib'
}

FILES_${PN} += "${libdir}/${BPN}/v2/modules/ ${datadir}/slsh/"

PARALLEL_MAKE = ""
PARALLEL_MAKEINST = ""

BBCLASSEXTEND = "native"
