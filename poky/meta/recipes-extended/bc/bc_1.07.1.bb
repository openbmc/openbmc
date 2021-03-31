SUMMARY = "Arbitrary precision calculator language"
HOMEPAGE = "http://www.gnu.org/software/bc/bc.html"
DESCRIPTION = "bc is an arbitrary precision numeric processing language. Syntax is similar to C, but differs in many substantial areas. It supports interactive execution of statements."

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://bc/bcdefs.h;endline=17;md5=4295c06df9e833519a342f7b5d43db06 \
                    file://dc/dc.h;endline=18;md5=36b8c600b63ee8c3aeade2764f6b2a4b \
                    file://lib/number.c;endline=20;md5=cf43068cc88f837731dc53240456cfaf"

SECTION = "base"
DEPENDS = "flex-native"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://no-gen-libmath.patch \
           file://libmath.h \
           file://0001-dc-fix-exit-code-of-q-command.patch"
SRC_URI[md5sum] = "cda93857418655ea43590736fc3ca9fc"
SRC_URI[sha256sum] = "62adfca89b0a1c0164c2cdca59ca210c1d44c3ffc46daf9931cf4942664cb02a"

inherit autotools texinfo update-alternatives

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"

do_compile_prepend() {
    cp -f ${WORKDIR}/libmath.h ${B}/bc/libmath.h
}

ALTERNATIVE_${PN} = "bc dc"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
