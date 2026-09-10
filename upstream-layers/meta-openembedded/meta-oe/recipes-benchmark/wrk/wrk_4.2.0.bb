SUMMARY = "HTTP benchmarking tool"
DESCRIPTION = "wrk is a modern HTTP benchmarking tool capable of generating \
significant load when run on a single multi-core CPU."
HOMEPAGE = "https://github.com/wg/wrk"
SECTION = "console/network"
LICENSE = "LicenseRef-wrk"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a1b7dfe4016fcf67ae4715f167a0043"

DEPENDS = "luajit luajit-native openssl"

SRC_URI = "git://github.com/wg/wrk.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "a211dd5a7050b1f9e8a9870b95513060e72ac4a0"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

# Use system luajit and openssl instead of bundled deps/
# luajit headers install to a versioned subdir, point include path explicitly
# LUA_PATH must point to luajit-native's jit.* modules for bytecode compilation (-b)
EXTRA_OEMAKE = "\
    WITH_LUAJIT=${RECIPE_SYSROOT}/usr \
    WITH_OPENSSL=${RECIPE_SYSROOT}/usr \
    CC='${CC}' \
    CFLAGS='${CFLAGS} -I${RECIPE_SYSROOT}/usr/include/luajit-2.1' \
"

do_compile() {
    export LUA_PATH="${RECIPE_SYSROOT_NATIVE}/usr/share/luajit-2.1/?.lua;;"
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 wrk ${D}${bindir}/wrk
}
