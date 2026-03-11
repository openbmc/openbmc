SUMMARY = "Arbitrary precision calculator language"
HOMEPAGE = "http://www.gnu.org/software/bc/bc.html"
DESCRIPTION = "bc is an arbitrary precision numeric processing language. Syntax is similar to C, but differs in many substantial areas. It supports interactive execution of statements."

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://bc/bcdefs.h;endline=17;md5=f4a0239b216b8407783955e74938f190 \
                    file://dc/dc.h;endline=18;md5=bad31533d57fe5948c996f9ef6643206 \
                    file://lib/number.c;endline=20;md5=cf43068cc88f837731dc53240456cfaf"

SECTION = "base"
DEPENDS = "flex-native"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://run-ptest"
SRC_URI[sha256sum] = "ae470fec429775653e042015edc928d07c8c3b2fc59765172a330d3d87785f86"

inherit autotools texinfo update-alternatives ptest

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"

do_install_ptest() {
        install ${S}/Test/*.b ${D}${PTEST_PATH}
}

ALTERNATIVE:${PN} = "bc dc"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"
