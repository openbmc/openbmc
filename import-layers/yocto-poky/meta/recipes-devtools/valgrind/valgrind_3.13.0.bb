SUMMARY = "Valgrind memory debugger and instrumentation framework"
HOMEPAGE = "http://valgrind.org/"
BUGTRACKER = "http://valgrind.org/support/bug_reports.html"
LICENSE = "GPLv2 & GPLv2+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://include/pub_tool_basics.h;beginline=6;endline=29;md5=d4de0407239381463cf01dd276d7c22e \
                    file://include/valgrind.h;beginline=1;endline=56;md5=ad3b317f3286b6b704575d9efe6ca5df \
                    file://COPYING.DOCS;md5=24ea4c7092233849b4394699333b5c56"

X11DEPENDS = "virtual/libx11"
DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11DEPENDS}', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'boost', '', d)} \
        "

SRC_URI = "ftp://sourceware.org/pub/valgrind/valgrind-${PV}.tar.bz2 \
           file://fixed-perl-path.patch \
           file://Added-support-for-PPC-instructions-mfatbu-mfatbl.patch \
           file://run-ptest \
           file://0002-remove-rpath.patch \
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
           file://0004-pth_atfork1.c-Define-error-API-for-musl.patch \
           file://0005-tc20_verifywrap.c-Fake-__GLIBC_PREREQ-with-musl.patch \
           file://0006-pth_detached3.c-Dereference-pthread_t-before-adding-.patch \
           file://0001-memcheck-tests-Use-ucontext_t-instead-of-struct-ucon.patch \
           file://0001-memcheck-arm64-Define-__THROW-if-not-already-defined.patch \
           file://0002-memcheck-x86-Define-__THROW-if-not-defined.patch \
           file://0003-tests-seg_override-Replace-__modify_ldt-with-syscall.patch \
           file://link-gz-tests.patch \
           file://ppc-headers.patch \
           "
SRC_URI[md5sum] = "817dd08f1e8a66336b9ff206400a5369"
SRC_URI[sha256sum] = "d76680ef03f00cd5e970bbdcd4e57fb1f6df7d2e2c071635ef2be74790190c3b"
UPSTREAM_CHECK_REGEX = "valgrind-(?P<pver>\d+(\.\d+)+)\.tar"

COMPATIBLE_HOST = '(i.86|x86_64|arm|aarch64|mips|powerpc|powerpc64).*-linux'

# valgrind supports armv7 and above
COMPATIBLE_HOST_armv4 = 'null'
COMPATIBLE_HOST_armv5 = 'null'
COMPATIBLE_HOST_armv6 = 'null'

# X32 isn't supported by valgrind at this time
COMPATIBLE_HOST_linux-gnux32 = 'null'
COMPATIBLE_HOST_linux-muslx32 = 'null'

# Disable for some MIPS variants
COMPATIBLE_HOST_mipsarchn32 = 'null'
COMPATIBLE_HOST_mipsarchr6 = 'null'

inherit autotools ptest

EXTRA_OECONF = "--enable-tls --without-mpicc"
EXTRA_OECONF += "${@['--enable-only32bit','--enable-only64bit'][d.getVar('SITEINFO_BITS') != '32']}"

# valgrind checks host_cpu "armv7*)", so we need to over-ride the autotools.bbclass default --host option
EXTRA_OECONF_append_arm = " --host=armv7${HOST_VENDOR}-${HOST_OS}"
TARGET_CC_ARCH_remove_arm = "${@get_mcpu(d)}"

EXTRA_OEMAKE = "-w"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl'"

# valgrind likes to control its own optimisation flags. It generally defaults
# to -O2 but uses -O0 for some specific test apps etc. Passing our own flags
# (via CFLAGS) means we interfere with that. Only pass DEBUG_FLAGS to it
# which fixes build path issue in DWARF.
SELECTED_OPTIMIZATION = "${DEBUG_FLAGS}"

def get_mcpu(d):
    for arg in (d.getVar('TUNE_CCARGS') or '').split():
        if arg.startswith('-mcpu='):
            return arg
        else:
            continue
    return ""

do_configure_prepend () {
    rm -rf ${S}/config.h
}

do_install_append () {
    install -m 644 ${B}/default.supp ${D}/${libdir}/valgrind/
}

TUNE = "${@strip_mcpu(d)}"

RDEPENDS_${PN} += "perl"

# valgrind needs debug information for ld.so at runtime in order to
# redirect functions like strlen.
RRECOMMENDS_${PN} += "${TCLIBC}-dbg"

RDEPENDS_${PN}-ptest += " sed perl perl-module-file-glob"
RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-utils"

# One of the tests contains a bogus interpreter path on purpose.
# Skip file dependency check
SKIP_FILEDEPS_${PN}-ptest = '1'

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

        # exclude shell or the package won't install
        rm -rf none/tests/shell* 2>/dev/null

        subdirs="tests cachegrind/tests callgrind/tests drd/tests helgrind/tests massif/tests memcheck/tests none/tests"

        # Get the vg test scripts, filters, and expected files
        for dir in $subdirs ; do
            find $dir | cpio -pvdu ${D}${PTEST_PATH}
        done
        cd $saved_dir
    done

    # clean out build artifacts before building the rpm
    find ${D}${PTEST_PATH} \
         \( -name "Makefile*" \
        -o -name "*.o" \
        -o -name "*.c" \
        -o -name "*.S" \
        -o -name "*.h" \) \
        -exec rm {} \;

    # needed by massif tests
    cp ${B}/massif/ms_print ${D}${PTEST_PATH}/massif/ms_print

    # handle multilib
    sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
}
