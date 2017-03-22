SUMMARY = "Compressor/archiver for creating and modifying .zip files"
HOMEPAGE = "http://www.info-zip.org"
SECTION = "console/utils"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=04d43c5d70b496c032308106e26ae17d"

PR = "r2"

S = "${WORKDIR}/zip30"

SRC_URI = "${SOURCEFORGE_MIRROR}/infozip/Zip%203.x%20%28latest%29/3.0/zip30.tar.gz \
           file://fix-security-format.patch"

SRC_URI[md5sum] = "7b74551e63f8ee6aab6fbc86676c0d37"
SRC_URI[sha256sum] = "f0e8bb1f9b7eb0b01285495a2699df3a4b766784c1765a8f1aeedf63c0806369"

# zip.inc sets CFLAGS, but what Makefile actually uses is
# CFLAGS_NOOPT.  It will also force -O3 optimization, overriding
# whatever we set.
EXTRA_OEMAKE = "'CC=${CC}' 'BIND=${CC}' 'AS=${CC} -c' 'CPP=${CPP}' \
		'CFLAGS=-I. -DUNIX ${CFLAGS}' \
		'CFLAGS_NOOPT=-I. -DUNIX ${CFLAGS}' \
		'INSTALL=install' 'INSTALL_D=install -d' \
		'BINFLAGS=0755'"

do_compile() {
	oe_runmake -f unix/Makefile flags IZ_BZIP2=no_such_directory
	sed -i 's#LFLAGS1=""#LFLAGS1="${LDFLAGS}"#' flags
	oe_runmake -f unix/Makefile generic IZ_BZIP2=no_such_directory
}

do_install() {
	oe_runmake -f unix/Makefile prefix=${D}${prefix} \
		   BINDIR=${D}${bindir} MANDIR=${D}${mandir}/man1 \
		   install
}

BBCLASSEXTEND = "native"

# exclude version 2.3.2 which triggers a false positive
UPSTREAM_CHECK_REGEX = "^zip(?P<pver>(?!232).+)\.tgz"
