SUMMARY = "A library which provides easy access to huge pages of memory"
HOMEPAGE = "https://github.com/libhugetlbfs/libhugetlbfs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LGPL-2.1;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "sysfsutils"
RDEPENDS_${PN} += "bash python3-core"
RDEPENDS_${PN}-tests += "bash python3-core"

PV = "2.23"
PE = "1"

SRCREV = "6b126a4d7da9490fa40fe7e1b962edcb939feddc"
SRC_URI = " \
    git://github.com/libhugetlbfs/libhugetlbfs.git;protocol=https \
    file://skip-checking-LIB32-and-LIB64-if-they-point-to-the-s.patch \
    file://libhugetlbfs-avoid-search-host-library-path-for-cros.patch \
    file://tests-Makefile-install-static-4G-edge-testcases.patch \
    file://0001-run_test.py-not-use-hard-coded-path-.-obj-hugeadm.patch \
    file://libhugetlbfs-elf_i386-avoid-search-host-library-path.patch \
    file://0001-include-stddef.h-for-ptrdiff_t.patch \
    file://0002-Mark-glibc-specific-code-so.patch \
    file://0003-alloc.c-Avoid-sysconf-_SC_LEVEL2_CACHE_LINESIZE-on-l.patch \
    file://0004-shm.c-Mark-glibc-specific-changes-so.patch \
    file://0005-Include-dirent.h-for-ino_t.patch \
    file://0006-include-limits.h-for-PATH_MAX.patch \
    file://0001-huge_page_setup_helper-use-python3-interpreter.patch \
    file://0001-Revert-ld.hugetlbfs-fix-Ttext-segment-argument-on-AA.patch \
    file://0001-tests-makefile-Append-CPPFLAGS-rather-then-override.patch \
"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(i.86|x86_64|powerpc|powerpc64|aarch64|arm).*-linux*"

LIBARGS = "LIB32=${baselib} LIB64=${baselib}"
LIBHUGETLBFS_ARCH = "${TARGET_ARCH}"
LIBHUGETLBFS_ARCH_powerpc = "ppc"
LIBHUGETLBFS_ARCH_powerpc64 = "ppc64"
LIBHUGETLBFS_ARCH_powerpc64le = "ppc64le"
EXTRA_OEMAKE = "'ARCH=${LIBHUGETLBFS_ARCH}' 'OPT=${CFLAGS}' 'CC=${CC}' ${LIBARGS} BUILDTYPE=NATIVEONLY V=2"
PARALLEL_MAKE = ""
CFLAGS += "-fexpensive-optimizations -frename-registers -fomit-frame-pointer -g0"

export HUGETLB_LDSCRIPT_PATH="${S}/ldscripts"

TARGET_CC_ARCH += "${LDFLAGS}"

#The CUSTOM_LDSCRIPTS doesn't work with the gold linker
inherit cpan-base
do_configure() {
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ld-is-gold', d)}" ]; then
      sed -i 's/CUSTOM_LDSCRIPTS = yes/CUSTOM_LDSCRIPTS = no/'  Makefile
    fi
}

do_install() {
        oe_runmake PREFIX=${prefix} DESTDIR=${D}  \
          INST_TESTSDIR32=${libdir}/libhugetlbfs/tests \
          INST_TESTSDIR64=${libdir}/libhugetlbfs/tests \
          install-tests
}


PACKAGES =+ "${PN}-tests "
FILES_${PN} += "${libdir}/*.so"
FILES_${PN}-dev = "${includedir}"
FILES_${PN}-dbg += "${libdir}/libhugetlbfs/tests/obj32/.debug ${libdir}/libhugetlbfs/tests/obj64/.debug"
FILES_${PN}-tests += "${libdir}/libhugetlbfs/tests"

INSANE_SKIP_${PN} = "dev-so"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
