SUMMARY = "A library which provides easy access to huge pages of memory"
HOMEPAGE = "https://github.com/libhugetlbfs/libhugetlbfs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LGPL-2.1;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "sysfsutils perl"
RDEPENDS_${PN} += "bash perl python python-io python-lang python-subprocess python-resource ${PN}-perl"
RDEPENDS_${PN}-tests += "bash"

PV = "2.20"
PE = "1"

SRCREV = "e44180072b796c0e28e53c4d01ef6279caaa2a99"
SRC_URI = " \
    git://github.com/libhugetlbfs/libhugetlbfs.git;protocol=https \
    file://skip-checking-LIB32-and-LIB64-if-they-point-to-the-s.patch \
    file://libhugetlbfs-avoid-search-host-library-path-for-cros.patch \
    file://tests-Makefile-install-static-4G-edge-testcases.patch \
    file://0001-run_test.py-not-use-hard-coded-path-.-obj-hugeadm.patch \
    file://libhugetlbfs-elf_i386-avoid-search-host-library-path.patch \
    file://Force-text-segment-alignment-to-0x08000000-for-i386-.patch \
"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(i.86|x86_64|powerpc|powerpc64|aarch64|arm).*-linux*"

LIBARGS = "LIB32=${baselib} LIB64=${baselib}"
LIBHUGETLBFS_ARCH = "${TARGET_ARCH}"
LIBHUGETLBFS_ARCH_powerpc = "ppc"
LIBHUGETLBFS_ARCH_powerpc64 = "ppc64"
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

    # fixup perl module directory hardcoded to perl5
    sed -i 's/perl5/perl/g'  Makefile

    # fixup to install perl module under $(LIBDIR)/perl/${@get_perl_version(d)}/TLBC
    # to avoid below error
    # Can't locate TLBC/OpCollect.pm in @INC
    sed -i '/^PMDIR/ s:perl:perl/${@get_perl_version(d)}:g' Makefile
}

do_install() {
        oe_runmake PREFIX=${prefix} DESTDIR=${D}  \
          INST_TESTSDIR32=${libdir}/libhugetlbfs/tests \
          INST_TESTSDIR64=${libdir}/libhugetlbfs/tests \
          install-tests
}


PACKAGES =+ "${PN}-perl ${PN}-tests "
FILES_${PN} += "${libdir}/*.so"
FILES_${PN}-dev = "${includedir}"
FILES_${PN}-dbg += "${libdir}/libhugetlbfs/tests/obj32/.debug ${libdir}/libhugetlbfs/tests/obj64/.debug"
FILES_${PN}-perl = "${libdir}/perl"
FILES_${PN}-tests += "${libdir}/libhugetlbfs/tests"

INSANE_SKIP_${PN} = "dev-so"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
