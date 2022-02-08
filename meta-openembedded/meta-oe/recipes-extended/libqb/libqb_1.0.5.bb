SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

# v1.0.5
SRCREV = "d08dbcf08b0da418bce9b5427dfd89522916322a"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git;branch=version_1;protocol=https \
           file://0001-build-fix-configure-script-neglecting-re-enable-out-.patch \
          "
S = "${WORKDIR}/git"

# otherwise do_configure fails
# configure:21609: checking whether linker workaround for orphan sections usable
# configure:21639: i586-oe-linux-gcc  -m32 -march=i586 --sysroot=WORKDIR/libqb/1.0.3+gitAUTOINC+c235284b5f-r0/recipe-sysroot -o conftest  -O -fno-omit-frame-pointer -g -feliminate-unused-debug-types -fdebug-prefix-map=WORKDIR/libqb/1.0.3+gitAUTOINC+c235284b5f-r0=/usr/src/debug/libqb/1.0.3+gitAUTOINC+c235284b5f-r0 -fdebug-prefix-map=WORKDIR/libqb/1.0.3+gitAUTOINC+c235284b5f-r0/recipe-sysroot= -fdebug-prefix-map=WORKDIR/libqb/1.0.3+gitAUTOINC+c235284b5f-r0/recipe-sysroot-native=  -pipe  -pthread -D_REENTRANT  -Wl,-O1 -Wl,--hash-style=gnu -Wl,--as-needed -Wl,conftest.ld conftest.c  >&5
# WORKDIR/libqb/1.0.3+gitAUTOINC+c235284b5f-r0/recipe-sysroot-native/usr/bin/i586-oe-linux/../../libexec/i586-oe-linux/gcc/i586-oe-linux/8.1.0/ld: error: conftest.ld: SECTIONS seen after other input files; try -T/--script
# WORKDIR/libqb/1.0.3+gitAUTOINC+c235284b5f-r0/recipe-sysroot-native/usr/bin/i586-oe-linux/../../libexec/i586-oe-linux/gcc/i586-oe-linux/8.1.0/ld: internal error in write_sections, at ../../gold/reloc.cc:791
# collect2: error: ld returned 1 exit status
NOSECTION_FALLBACK = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '--enable-nosection-fallback', '', d)}"
NOSECTION_FALLBACK_toolchain-clang_mips64 = "--enable-nosection-fallback"

EXTRA_OECONF += "${NOSECTION_FALLBACK}"

CFLAGS += "-pthread -D_REENTRANT"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}

BBCLASSEXTEND = "native"
