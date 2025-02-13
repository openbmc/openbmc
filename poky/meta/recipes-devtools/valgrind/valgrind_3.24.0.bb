SUMMARY = "Valgrind memory debugger and instrumentation framework"
HOMEPAGE = "http://valgrind.org/"
DESCRIPTION = "Valgrind is an instrumentation framework for building dynamic analysis tools. There are Valgrind tools that can automatically detect many memory management and threading bugs, and profile your programs in detail."
BUGTRACKER = "http://valgrind.org/support/bug_reports.html"
LICENSE = "GPL-2.0-only & GPL-2.0-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://include/pub_tool_basics.h;beginline=6;endline=29;md5=41c410e8d3f305aee7aaa666b2e4f366 \
                    file://include/valgrind.h;beginline=1;endline=56;md5=ad3b317f3286b6b704575d9efe6ca5df \
                    file://COPYING.DOCS;md5=24ea4c7092233849b4394699333b5c56"

SRC_URI = "https://sourceware.org/pub/valgrind/valgrind-${PV}.tar.bz2 \
           file://fixed-perl-path.patch \
           file://Added-support-for-PPC-instructions-mfatbu-mfatbl.patch \
           file://use-appropriate-march-mcpu-mfpu-for-ARM-test-apps.patch \
           file://avoid-neon-for-targets-which-don-t-support-it.patch \
           file://0001-configure-Drop-setting-mcpu-cortex-a8-on-arm.patch \
           file://valgrind-make-ld-XXX.so-strlen-intercept-optional.patch \
           file://0001-makefiles-Drop-setting-mcpu-to-cortex-a8-on-arm-arch.patch \
           file://0001-sigqueue-Rename-_sifields-to-__si_fields-on-musl.patch \
           file://0003-correct-include-directive-path-for-config.h.patch \
           file://0001-valgrind-filter_xml_frames-do-not-filter-usr.patch \
           file://0001-memcheck-vgtests-remove-fullpath-after-flags.patch \
           file://s390x_vec_op_t.patch \
           file://0001-none-tests-fdleak_cmsg.stderr.exp-adjust-tmp-paths.patch \
           file://0001-memcheck-tests-Fix-timerfd-syscall-test.patch \
           file://0001-docs-Disable-manual-validation.patch \
           file://0001-tests-arm-Use-O-instead-of-O0.patch \
           "
SRC_URI[sha256sum] = "71aee202bdef1ae73898ccf7e9c315134fa7db6c246063afc503aef702ec03bd"
UPSTREAM_CHECK_REGEX = "valgrind-(?P<pver>\d+(\.\d+)+)\.tar"

COMPATIBLE_HOST = '(i.86|x86_64|arm|aarch64|mips|powerpc|powerpc64).*-linux'

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

inherit autotools-brokensep multilib_header

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
SELECTED_OPTIMIZATION = "${DEBUG_LEVELFLAG}"

# Split out various helper scripts to separate packages to avoid the
# main package depending on perl and python.
PACKAGES =+ "${PN}-cachegrind ${PN}-massif ${PN}-callgrind"

FILES:${PN}-cachegrind = "${bindir}/cg_*"
FILES:${PN}-massif = "${bindir}/ms_*"
FILES:${PN}-callgrind = "${bindir}/callgrind_*"

RDEPENDS:${PN}-cachegrind = "${PN} python3-core"
RDEPENDS:${PN}-massif = "${PN} perl"
RDEPENDS:${PN}-callgrind = "${PN} perl"

do_configure:prepend () {
    rm -rf ${S}/config.h
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

# valgrind needs debug information for ld.so at runtime in order to
# redirect functions like strlen.
RRECOMMENDS:${PN} += "${TCLIBC}-dbg"
