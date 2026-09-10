SUMMARY = "HTTP benchmarking tool with constant throughput and latency details"
DESCRIPTION = "wrk2 is a version of wrk modified to produce a constant throughput load and report accurate latency details."
HOMEPAGE = "https://github.com/AmpereTravis/wrk2-aarch64"
SECTION = "console/network"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
DEPENDS = "luajit luajit-native openssl zlib"
SRC_URI = "git://github.com/AmpereTravis/wrk2-aarch64.git;protocol=https;branch=master \
           file://0001-load-wrk-lua-from-filesystem.patch"
SRCREV = "59c09ad4442329ade4343e1ade683c4d12b0b370"

UPSTREAM_CHECK_COMMITS = "1"

do_configure() {
    sed -i \
    -e 's|LIBS := -lluajit|LIBS := -lluajit-5.1|' \
    -e 's|$(LDIR)/libluajit.a||' \
    -e 's|$(ODIR)/bytecode.o||g' \
    Makefile
    sed -i '/#include <x86intrin.h>/d' src/hdr_histogram.c
    sed -i '/#include <zlib.h>/d' src/hdr_histogram.c
    sed -i 's/struct luaL_reg/struct luaL_Reg/g' src/script.c
}

EXTRA_OEMAKE = "\
    LDIR=${RECIPE_SYSROOT}/usr/include/luajit-2.1 \
    LIBS='-lluajit-5.1 -lpthread -lm -lssl -lcrypto -ldl' \
    CC='${CC}' \
    CFLAGS='${CFLAGS} -I${RECIPE_SYSROOT}/usr/include/luajit-2.1' \
    LDFLAGS='${LDFLAGS} -L${RECIPE_SYSROOT}/usr/lib' \
"

do_configure:append() {
    sed -i \
    's/signed char c, \*\*header = headers;/signed char c; char **header = headers;/' \
    src/wrk.c
}

do_compile() {
    oe_runmake V=1
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 wrk ${D}${bindir}/wrk2
    install -d ${D}${datadir}/wrk2
    install -m 0644 src/wrk.lua ${D}${datadir}/wrk2/wrk.lua
}