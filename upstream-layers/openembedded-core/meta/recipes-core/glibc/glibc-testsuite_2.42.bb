# Recipe for cross-compiling and running the glibc test suite.
#
# Unlike regular recipes, this recipe does not install any files (do_install
# and do_populate_sysroot are disabled). Instead, the tests are run in-place
# from the build directory (B) via the do_check task:
#
#   bitbake glibc-testsuite -c check
#
# This is driven by the OE selftest in meta/lib/oeqa/selftest/cases/glibc.py,
# which supports two execution modes controlled by TOOLCHAIN_TEST_TARGET:
#
#   user (default):
#     Each test binary is run under QEMU user-space emulation on the host,
#     using the target sysroot. qemu-native is added to DEPENDS automatically.
#
#   ssh:
#     Each test binary is executed on a remote target over SSH. The caller
#     must set TOOLCHAIN_TEST_HOST, TOOLCHAIN_TEST_HOST_USER and
#     TOOLCHAIN_TEST_HOST_PORT. In the emulated selftest variant, the target
#     is a QEMU system (core-image-minimal) with the host build directory
#     NFS-mounted at the same path, so the tests can access their binaries
#     and data files directly.
#
# In both modes the check-test-wrapper script is passed as Autotools'
# test-wrapper, which intercepts each test invocation and forwards it to
# the appropriate execution backend.
#
# Running only specific tests is possible by using for example
#   make test t=misc/tst-syslog
# instead of
#   make check

require glibc_${PV}.bb
require glibc-tests.inc

inherit qemu

SRC_URI += "file://check-test-wrapper"

# strip provides
PROVIDES = ""

DEPENDS += "${@'qemu-native' if d.getVar('TOOLCHAIN_TEST_TARGET') == 'user' else ''}"

TOOLCHAIN_TEST_TARGET ??= "user"
TOOLCHAIN_TEST_HOST ??= "localhost"
TOOLCHAIN_TEST_HOST_USER ??= "root"
TOOLCHAIN_TEST_HOST_PORT ??= "2222"

do_check[nostamp] = "1"
do_check[network] = "1"
do_check:append () {
    chmod 0755 ${UNPACKDIR}/check-test-wrapper

    oe_runmake -i \
        GPROF="${TARGET_PREFIX}gprof" \
        QEMU_SYSROOT="${RECIPE_SYSROOT}" \
        QEMU_OPTIONS="${@qemu_target_binary(d)} ${QEMU_OPTIONS}" \
        SSH_HOST="${TOOLCHAIN_TEST_HOST}" \
        SSH_HOST_USER="${TOOLCHAIN_TEST_HOST_USER}" \
        SSH_HOST_PORT="${TOOLCHAIN_TEST_HOST_PORT}" \
        test-wrapper="${UNPACKDIR}/check-test-wrapper ${TOOLCHAIN_TEST_TARGET}" \
        check
}

inherit nopackages
deltask do_stash_locale
deltask do_install
deltask do_populate_sysroot
