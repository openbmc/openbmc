SUMMARY = "A library which provides easy access to huge pages of memory"
HOMEPAGE = "https://github.com/libhugetlbfs/libhugetlbfs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LGPL-2.1;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "sysfsutils"
RDEPENDS:${PN} += "bash python3-core"
RDEPENDS:${PN}-tests += "bash python3-core python3-resource"

PE = "1"

SRCREV = "1322884fb0d55dc55f53563c1aa6328d118997e7"
SRC_URI = " \
    git://github.com/libhugetlbfs/libhugetlbfs.git;protocol=https;branch=master \
    file://0001-skip-checking-LIB32-and-LIB64-if-they-point-to-the-s.patch \
    file://0002-libhugetlbfs-avoid-search-host-library-path-for-cros.patch \
    file://0003-tests-Makefile-install-static-4G-edge-testcases.patch \
    file://0004-run_test.py-not-use-hard-coded-path-.-obj-hugeadm.patch \
    file://0005-libhugetlbfs-elf_i386-avoid-search-host-library-path.patch \
    file://0006-include-stddef.h-for-ptrdiff_t.patch \
    file://0007-Mark-glibc-specific-code-so.patch \
    file://0008-alloc.c-Avoid-sysconf-_SC_LEVEL2_CACHE_LINESIZE-on-l.patch \
    file://0009-shm.c-Mark-glibc-specific-changes-so.patch \
    file://0010-Include-dirent.h-for-ino_t.patch \
    file://0011-include-limits.h-for-PATH_MAX.patch \
    file://0012-huge_page_setup_helper-use-python3-interpreter.patch \
    file://0013-elflink.c-include-libgen.h-for-basename.patch \
"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(i.86|x86_64|powerpc|powerpc64|aarch64|arm).*-linux*"

LIBARGS = "LIB32=${baselib} LIB64=${baselib}"
LIBHUGETLBFS_ARCH = "${TARGET_ARCH}"
LIBHUGETLBFS_ARCH:powerpc = "ppc"
LIBHUGETLBFS_ARCH:powerpc64 = "ppc64"
LIBHUGETLBFS_ARCH:powerpc64le = "ppc64le"
EXTRA_OEMAKE = "'ARCH=${LIBHUGETLBFS_ARCH}' 'OPT=${CFLAGS}' 'CC=${CC}' ${LIBARGS} BUILDTYPE=NATIVEONLY V=2"
PARALLEL_MAKE = ""
CFLAGS += "-fexpensive-optimizations -frename-registers -fomit-frame-pointer -g0"

export HUGETLB_LDSCRIPT_PATH="${S}/ldscripts"

TARGET_CC_ARCH += "${LDFLAGS}"

LDFLAGS += "-B${S}"

inherit autotools-brokensep cpan-base

#The CUSTOM_LDSCRIPTS doesn't work with the gold linker
do_configure:prepend() {
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ld-is-gold', d)}" ]; then
        sed -i 's/CUSTOM_LDSCRIPTS = yes/CUSTOM_LDSCRIPTS = no/'  Makefile.in
    fi

    ln -sf ld.hugetlbfs ${S}/ld
    ln -sf ld.hugetlbfs ${S}/ld.bfd
    ln -sf ld.hugetlbfs ${S}/ld.gold
    ln -sf ld.hugetlbfs ${S}/ld.lld
}

do_install() {
    oe_runmake PREFIX=${prefix} DESTDIR=${D}  \
        INST_TESTSDIR32=${libdir}/libhugetlbfs/tests \
        INST_TESTSDIR64=${libdir}/libhugetlbfs/tests \
        install-tests

    sed -i \
        -e 's|${RECIPE_SYSROOT_NATIVE}||g' \
        -e 's|${RECIPE_SYSROOT}||g' \
        -e 's|${S}||g' \
        `find ${D}${libdir}/libhugetlbfs/tests -name dummy.ldscript`
}

PACKAGES =+ "${PN}-tests "
FILES:${PN} += "${libdir}/*.so"
FILES:${PN}-dev = "${includedir}"
FILES:${PN}-dbg += "${libdir}/libhugetlbfs/tests/obj32/.debug ${libdir}/libhugetlbfs/tests/obj64/.debug"
FILES:${PN}-tests += "${libdir}/libhugetlbfs/tests"

INSANE_SKIP:${PN} = "dev-so"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
