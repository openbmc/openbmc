SUMMARY = "Compressor/archiver for creating and modifying .zip files"
HOMEPAGE = "http://www.info-zip.org"
SECTION = "console/utils"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=04d43c5d70b496c032308106e26ae17d"

SRC_URI = "ftp://ftp.info-zip.org/pub/infozip/src/zip${@d.getVar('PV',1).replace('.', '')}.tgz"

EXTRA_OEMAKE = "'CC=${CC}' 'BIND=${CC}' 'AS=${CC} -c' 'CPP=${CPP}' \
		'CFLAGS=-I. -DUNIX ${CFLAGS}' 'INSTALL=install' \
		'BINFLAGS=0755' 'INSTALL_D=install -d'"

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
