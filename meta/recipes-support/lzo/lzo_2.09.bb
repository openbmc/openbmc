SUMMARY = "Lossless data compression library"
HOMEPAGE = "http://www.oberhumer.com/opensource/lzo/"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/lzo_init.c;beginline=5;endline=25;md5=355023835a9b9eeb70ab895395e951ff"

SRC_URI = "http://www.oberhumer.com/opensource/lzo/download/lzo-${PV}.tar.gz \
           file://0001-Use-memcpy-instead-of-reinventing-it.patch \
           file://acinclude.m4 \
           file://run-ptest \
           "

SRC_URI[md5sum] = "c7ffc9a103afe2d1bba0b015e7aa887f"
SRC_URI[sha256sum] = "f294a7ced313063c057c504257f437c8335c41bfeed23531ee4e6a2b87bcb34c"

inherit autotools ptest

EXTRA_OECONF = "--enable-shared"

do_configure_prepend () {
	cp ${WORKDIR}/acinclude.m4 ${S}/
}

do_install_ptest() {
	t=${D}${PTEST_PATH}
	cp ${S}/util/check.sh $t
	cp ${B}/minilzo/testmini $t
	for i in tests/align tests/chksum lzotest/lzotest examples/simple
		do cp ${B}/`dirname $i`/.libs/`basename $i` $t; \
	done
}


BBCLASSEXTEND = "native nativesdk"
