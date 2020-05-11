require glibc_${PV}.bb

EXCLUDE_FROM_WORLD = "1"

# handle PN differences
FILESEXTRAPATHS_prepend := "${THISDIR}/glibc:"

# strip provides
PROVIDES = ""
# setup depends
INHIBIT_DEFAULT_DEPS = ""

python () {
    libc = d.getVar("PREFERRED_PROVIDER_virtual/libc")
    libclocale = d.getVar("PREFERRED_PROVIDER_virtual/libc-locale")
    if libc != "glibc" or libclocale != "glibc-locale":
        raise bb.parse.SkipRecipe("glibc-testsuite requires that virtual/libc is glibc")
}

DEPENDS += "glibc-locale libgcc gcc-runtime"

# remove the initial depends
DEPENDS_remove = "libgcc-initial"

inherit qemu

SRC_URI += "file://check-test-wrapper"

DEPENDS += "${@'qemu-native' if d.getVar('TOOLCHAIN_TEST_TARGET') == 'user' else ''}"

TOOLCHAIN_TEST_TARGET ??= "user"
TOOLCHAIN_TEST_HOST ??= "localhost"
TOOLCHAIN_TEST_HOST_USER ??= "root"
TOOLCHAIN_TEST_HOST_PORT ??= "2222"

do_check[dirs] += "${B}"
do_check[nostamp] = "1"
do_check () {
    chmod 0755 ${WORKDIR}/check-test-wrapper

    # clean out previous test results
    oe_runmake tests-clean
    # makefiles don't clean entirely (and also sometimes fails due to too many args)
    find ${B} -type f -name "*.out" -delete
    find ${B} -type f -name "*.test-result" -delete
    find ${B}/catgets -name "*.cat" -delete
    find ${B}/conform -name "symlist-*" -delete
    [ ! -e ${B}/timezone/testdata ] || rm -rf ${B}/timezone/testdata

    oe_runmake -i \
        QEMU_SYSROOT="${RECIPE_SYSROOT}" \
        QEMU_OPTIONS="${@qemu_target_binary(d)} ${QEMU_OPTIONS}" \
        SSH_HOST="${TOOLCHAIN_TEST_HOST}" \
        SSH_HOST_USER="${TOOLCHAIN_TEST_HOST_USER}" \
        SSH_HOST_PORT="${TOOLCHAIN_TEST_HOST_PORT}" \
        test-wrapper="${WORKDIR}/check-test-wrapper ${TOOLCHAIN_TEST_TARGET}" \
        check
}
addtask do_check after do_compile

inherit nopackages
deltask do_stash_locale
deltask do_install
