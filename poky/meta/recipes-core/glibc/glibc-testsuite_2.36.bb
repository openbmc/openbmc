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
do_check:append () {
    chmod 0755 ${WORKDIR}/check-test-wrapper

    oe_runmake -i \
        QEMU_SYSROOT="${RECIPE_SYSROOT}" \
        QEMU_OPTIONS="${@qemu_target_binary(d)} ${QEMU_OPTIONS}" \
        SSH_HOST="${TOOLCHAIN_TEST_HOST}" \
        SSH_HOST_USER="${TOOLCHAIN_TEST_HOST_USER}" \
        SSH_HOST_PORT="${TOOLCHAIN_TEST_HOST_PORT}" \
        test-wrapper="${WORKDIR}/check-test-wrapper ${TOOLCHAIN_TEST_TARGET}" \
        check
}

inherit nopackages
deltask do_stash_locale
deltask do_install
deltask do_populate_sysroot
