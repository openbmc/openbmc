SUMMARY = "Valgrind memory debugger and instrumentation framework"
HOMEPAGE = "http://valgrind.org/"
DESCRIPTION = "Valgrind is an instrumentation framework for building dynamic analysis tools. There are Valgrind tools that can automatically detect many memory management and threading bugs, and profile your programs in detail."
BUGTRACKER = "http://valgrind.org/support/bug_reports.html"
LICENSE = "GPLv2 & GPLv2+ & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://include/pub_tool_basics.h;beginline=6;endline=29;md5=41c410e8d3f305aee7aaa666b2e4f366 \
                    file://include/valgrind.h;beginline=1;endline=56;md5=ad3b317f3286b6b704575d9efe6ca5df \
                    file://COPYING.DOCS;md5=24ea4c7092233849b4394699333b5c56"

DEPENDS = " \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'boost', '', d)} \
        "

SRC_URI = "https://sourceware.org/pub/valgrind/valgrind-${PV}.tar.bz2 \
           file://fixed-perl-path.patch \
           file://Added-support-for-PPC-instructions-mfatbu-mfatbl.patch \
           file://run-ptest \
           file://remove-for-aarch64 \
           file://remove-for-all \
           file://taskset_nondeterministic_tests \
           file://0004-Fix-out-of-tree-builds.patch \
           file://0005-Modify-vg_test-wrapper-to-support-PTEST-formats.patch \
           file://0001-Remove-tests-that-fail-to-build-on-some-PPC32-config.patch \
           file://use-appropriate-march-mcpu-mfpu-for-ARM-test-apps.patch \
           file://avoid-neon-for-targets-which-don-t-support-it.patch \
           file://valgrind-make-ld-XXX.so-strlen-intercept-optional.patch \
           file://0001-makefiles-Drop-setting-mcpu-to-cortex-a8-on-arm-arch.patch \
           file://0001-str_tester.c-Limit-rawmemchr-test-to-glibc.patch \
           file://0001-sigqueue-Rename-_sifields-to-__si_fields-on-musl.patch \
           file://0002-context-APIs-are-not-available-on-musl.patch \
           file://0003-correct-include-directive-path-for-config.h.patch \
           file://0005-tc20_verifywrap.c-Fake-__GLIBC_PREREQ-with-musl.patch \
           file://0001-memcheck-arm64-Define-__THROW-if-not-already-defined.patch \
           file://0002-memcheck-x86-Define-__THROW-if-not-defined.patch \
           file://0003-tests-seg_override-Replace-__modify_ldt-with-syscall.patch \
           file://0001-fix-opcode-not-supported-on-mips32-linux.patch \
           file://0001-Guard-against-__GLIBC_PREREQ-for-musl-libc.patch \
           file://0001-Make-local-functions-static-to-avoid-assembler-error.patch \
           file://0001-Return-a-valid-exit_code-from-vg_regtest.patch \
           file://0001-valgrind-filter_xml_frames-do-not-filter-usr.patch \
           file://0001-memcheck-vgtests-remove-fullpath-after-flags.patch \
           file://s390x_vec_op_t.patch \
           file://0001-none-tests-fdleak_cmsg.stderr.exp-adjust-tmp-paths.patch \
           file://0001-memcheck-tests-Fix-timerfd-syscall-test.patch \
           file://0001-Add-missing-musl.supp.patch \
           file://0001-drd-tests-swapcontext-Add-SIGALRM-handler-to-avoid-s.patch \
           file://6da22a4d246519cd1a638cfc7eff00cdd74413c4.patch \
           file://200b6a5a0ea3e1e154663b0fc575bfe2becf177d.patch \
           file://a1364805fc74b5690f763033c0c9b43f27613572.patch \
           file://52ed51fc35f8a6148c2940eb46932b02dd3b9b23.patch \
           "
SRC_URI[md5sum] = "afe11b5572c3121a781433b7c0ab741b"
SRC_URI[sha256sum] = "ad3aec668e813e40f238995f60796d9590eee64a16dff88421430630e69285a2"
UPSTREAM_CHECK_REGEX = "valgrind-(?P<pver>\d+(\.\d+)+)\.tar"

COMPATIBLE_HOST = '(i.86|x86_64|arm|aarch64|mips|powerpc|powerpc64).*-linux'

# patch 0001-memcheck-vgtests-remove-fullpath-after-flags.patch removes relative path
# argument. Change expected stderr files accordingly.
do_patch:append() {
    bb.build.exec_func('do_sed_paths', d)
}

do_sed_paths() {
    sed -i -e 's|tests/||' ${S}/memcheck/tests/badfree3.stderr.exp
    sed -i -e 's|tests/||' ${S}/memcheck/tests/varinfo5.stderr.exp
}

# valgrind supports armv7 and above
COMPATIBLE_HOST:armv4 = 'null'
COMPATIBLE_HOST:armv5 = 'null'
COMPATIBLE_HOST:armv6 = 'null'

# valgrind fails with powerpc soft-float
COMPATIBLE_HOST:powerpc = "${@bb.utils.contains('TARGET_FPU', 'soft', 'null', '.*-linux', d)}"

# X32 isn't supported by valgrind at this time
COMPATIBLE_HOST:linux-gnux32 = 'null'
COMPATIBLE_HOST:linux-muslx32 = 'null'

# Disable for some MIPS variants
COMPATIBLE_HOST:mipsarchr6 = 'null'
COMPATIBLE_HOST:linux-gnun32 = 'null'

# Disable for powerpc64 with musl
COMPATIBLE_HOST:libc-musl:powerpc64 = 'null'

# brokenseip is unfortunately required by ptests to pass
inherit autotools-brokensep ptest multilib_header

EXTRA_OECONF = "--enable-tls --without-mpicc"
EXTRA_OECONF += "${@['--enable-only32bit','--enable-only64bit'][d.getVar('SITEINFO_BITS') != '32']}"

# valgrind checks host_cpu "armv7*)", so we need to over-ride the autotools.bbclass default --host option
EXTRA_OECONF:append:arm = " --host=armv7${HOST_VENDOR}-${HOST_OS}"

EXTRA_OEMAKE = "-w"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl'"

# valgrind likes to control its own optimisation flags. It generally defaults
# to -O2 but uses -O0 for some specific test apps etc. Passing our own flags
# (via CFLAGS) means we interfere with that. Only pass DEBUG_FLAGS to it
# which fixes build path issue in DWARF.
SELECTED_OPTIMIZATION = "${DEBUG_FLAGS}"

do_configure:prepend () {
    rm -rf ${S}/config.h
    sed -i -e 's:$(abs_top_builddir):$(pkglibdir)/ptest:g' ${S}/none/tests/Makefile.am
    sed -i -e 's:$(top_builddir):$(pkglibdir)/ptest:g' ${S}/memcheck/tests/Makefile.am
}

do_install:append () {
    install -m 644 ${B}/default.supp ${D}/${libexecdir}/valgrind/
    oe_multilib_header valgrind/config.h
}

VALGRINDARCH ?= "${TARGET_ARCH}"
VALGRINDARCH:aarch64 = "arm64"
VALGRINDARCH:x86-64 = "amd64"
VALGRINDARCH:x86 = "x86"
VALGRINDARCH:mips = "mips32"
VALGRINDARCH:mipsel = "mips32"
VALGRINDARCH:mips64el = "mips64"
VALGRINDARCH:powerpc = "ppc"
VALGRINDARCH:powerpc64 = "ppc64"
VALGRINDARCH:powerpc64le = "ppc64le"

INHIBIT_PACKAGE_STRIP_FILES = "${PKGD}${libexecdir}/valgrind/vgpreload_memcheck-${VALGRINDARCH}-linux.so"

RDEPENDS:${PN} += "perl"

# valgrind needs debug information for ld.so at runtime in order to
# redirect functions like strlen.
RRECOMMENDS:${PN} += "${TCLIBC}-dbg"

RDEPENDS:${PN}-ptest += " bash coreutils curl file \
   gdb libgomp \
   perl \
   perl-module-file-basename perl-module-file-glob perl-module-getopt-long \
   perl-module-overloading perl-module-cwd perl-module-ipc-open3 \
   perl-module-carp perl-module-symbol \
   procps sed ${PN}-dbg ${PN}-src ${TCLIBC}-src gcc-runtime-dbg"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils"

# One of the tests contains a bogus interpreter path on purpose.
# Skip file dependency check
SKIP_FILEDEPS:${PN}-ptest = '1'
INSANE_SKIP:${PN}-ptest = "debug-deps"

do_compile_ptest() {
    oe_runmake check
}

do_install_ptest() {
    chmod +x ${B}/tests/vg_regtest

    # The test application binaries are not automatically installed.
    # Grab them from the build directory.
    #
    # The regression tests require scripts and data files that are not
    # copied to the build directory.  They must be copied from the
    # source directory.
    saved_dir=$PWD
    for parent_dir in ${S} ${B} ; do
        cd $parent_dir

        subdirs=" \
	   .in_place \
	   cachegrind/tests \
	   callgrind/tests \
	   dhat/tests \
	   drd/tests \
	   gdbserver_tests \
	   helgrind/tests \
	   lackey/tests \
	   massif/tests \
	   memcheck/tests \
	   none/tests \
	   tests \
	   exp-bbv/tests \
	"
        # Get the vg test scripts, filters, and expected files
        for dir in $subdirs ; do
            find $dir | cpio -pvdu ${D}${PTEST_PATH}
        done
        cd $saved_dir
    done

    # The scripts reference config.h so add it to the top ptest dir.
    cp ${B}/config.h ${D}${PTEST_PATH}
    install -D ${WORKDIR}/remove-for-aarch64 ${D}${PTEST_PATH}
    install -D ${WORKDIR}/remove-for-all ${D}${PTEST_PATH}
    install -D ${WORKDIR}/taskset_nondeterministic_tests ${D}${PTEST_PATH}

    # Add an executable need by none/tests/bigcode
    mkdir ${D}${PTEST_PATH}/perf
    cp ${B}/perf/bigcode ${D}${PTEST_PATH}/perf

    # Add an executable needed by memcheck/tests/vcpu_bz2
    cp ${B}/perf/bz2 ${D}${PTEST_PATH}/perf

    # Make the ptest dir look like the top level valgrind src dir
    # This is checked by the gdbserver_tests/make_local_links script
    mkdir ${D}${PTEST_PATH}/coregrind
    cp ${B}/coregrind/vgdb ${D}${PTEST_PATH}/coregrind

    # Add an executable needed by massif tests
    cp ${B}/massif/ms_print ${D}${PTEST_PATH}/massif/ms_print

    find ${D}${PTEST_PATH} \
        \( \
	   -name "Makefile*" \
        -o -name "*.o" \
	\) \
        -exec rm {} \;

    # These files need to be newer so touch them.
    touch ${D}${PTEST_PATH}/cachegrind/tests/a.c -r ${D}${PTEST_PATH}/cachegrind/tests/cgout-test

    # find *_annotate in ${bindir} for yocto build
    sed -i s:\.\./\.\./cachegrind/cg_annotate:${bindir}/cg_annotate: ${D}${PTEST_PATH}/cachegrind/tests/ann1.vgtest
    sed -i s:\.\./\.\./cachegrind/cg_annotate:${bindir}/cg_annotate: ${D}${PTEST_PATH}/cachegrind/tests/ann2.vgtest

    sed -i s:\.\./\.\./callgrind/callgrind_annotate:${bindir}/callgrind_annotate: ${D}${PTEST_PATH}/callgrind/tests/ann1.vgtest
    sed -i s:\.\./\.\./callgrind/callgrind_annotate:${bindir}/callgrind_annotate: ${D}${PTEST_PATH}/callgrind/tests/ann2.vgtest

    # point the expanded @abs_top_builddir@ of the host to PTEST_PATH
    sed -i s:${S}:${PTEST_PATH}:g \
        ${D}${PTEST_PATH}/memcheck/tests/linux/debuginfod-check.vgtest

    # handle multilib
    sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
    sed -i s:@libexecdir@:${libexecdir}:g ${D}${PTEST_PATH}/run-ptest
    sed -i s:@bindir@:${bindir}:g ${D}${PTEST_PATH}/run-ptest

    # This test fails on the host as well, using both 3.15 and git master (as of Jan 24 2020)
    # https://bugs.kde.org/show_bug.cgi?id=402833
    rm ${D}${PTEST_PATH}/memcheck/tests/overlap.vgtest

    # This is known failure see https://bugs.kde.org/show_bug.cgi?id=435732
    rm ${D}${PTEST_PATH}/memcheck/tests/leak_cpp_interior.vgtest

    # As the binary isn't stripped or debug-splitted, the source file isn't fetched
    # via dwarfsrcfiles either, so it needs to be installed manually.
    mkdir -p ${D}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${BP}/none/tests/
    install ${S}/none/tests/tls.c ${D}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${BP}/none/tests/
}

# avoid stripping some generated binaries otherwise some of the tests will fail
# run-strip-reloc.sh, run-strip-strmerge.sh and so on will fail
INHIBIT_PACKAGE_STRIP_FILES += "\
    ${PKGD}${PTEST_PATH}/none/tests/tls \
    ${PKGD}${PTEST_PATH}/none/tests/tls.so \
    ${PKGD}${PTEST_PATH}/none/tests/tls2.so \
    ${PKGD}${PTEST_PATH}/helgrind/tests/tc09_bad_unlock \
    ${PKGD}${PTEST_PATH}/memcheck/tests/manuel1 \
    ${PKGD}${PTEST_PATH}/drd/tests/pth_detached3 \
"
