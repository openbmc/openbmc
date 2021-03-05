SUMMARY = "Lossless data compression library"
DESCRIPTION = "A portable lossless data compression library written in \
ANSI C that offers pretty fast compression and *extremely* fast decompression. "
HOMEPAGE = "http://www.oberhumer.com/opensource/lzo/"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/lzo_init.c;beginline=5;endline=25;md5=9ae697ca01829b0a383c5d2d163e0108"

SRC_URI = "http://www.oberhumer.com/opensource/lzo/download/lzo-${PV}.tar.gz \
           file://0001-Use-memcpy-instead-of-reinventing-it.patch \
	   file://0001-Add-pkgconfigdir-to-solve-the-undefine-error.patch \
           file://run-ptest \
           "

SRC_URI[md5sum] = "39d3f3f9c55c87b1e5d6888e1420f4b5"
SRC_URI[sha256sum] = "c0f892943208266f9b6543b3ae308fab6284c5c90e627931446fb49b4221a072"

inherit autotools ptest

EXTRA_OECONF = "--enable-shared"

do_install_ptest() {
	t=${D}${PTEST_PATH}
	cp ${S}/util/check.sh $t
	cp ${B}/minilzo/testmini $t
	for i in tests/align tests/chksum lzotest/lzotest examples/simple
		do cp ${B}/`dirname $i`/.libs/`basename $i` $t; \
	done
}


BBCLASSEXTEND = "native nativesdk"
