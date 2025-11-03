#
# Configuration to use an external Arm binary toolchain
#

EXTERNAL_TOOLCHAIN ?= "/usr/local/arm-binary-toolchain/${TARGET_ARCH}"

# oe-core passes this by default because it assumes GCC 13. This can be removed when Arm GCC is 13.1+.
DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

TOOLCHAIN_PATH_ADD = "${EXTERNAL_TOOLCHAIN}/bin:"
PATH =. "${TOOLCHAIN_PATH_ADD}"

EAT_TARGET_SYS:arm ?= "${@ 'arm-none-linux-gnueabihf' if os.path.exists('${EXTERNAL_TOOLCHAIN}/bin/arm-none-linux-gnueabihf-gcc') else 'arm-linux-gnueabihf'}"
EAT_TARGET_SYS:aarch64 ?= "${@ 'aarch64-none-linux-gnu' if os.path.exists('${EXTERNAL_TOOLCHAIN}/bin/aarch64-none-linux-gnu-gcc') else 'aarch64-linux-gnu'}"
EAT_TARGET_SYS = "${TARGET_SYS}"
TARGET_PREFIX = "${EAT_TARGET_SYS}-"

EXTERNAL_TOOLCHAIN_LIBDIR = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/usr/lib', '/lib', d)}"
EXTERNAL_TOOLCHAIN_DYN_LINKER_PATH:arm ?= " -Wl,--dynamic-linker=${EXTERNAL_TOOLCHAIN_LIBDIR}/ld-linux-armhf.so.3"
EXTERNAL_TOOLCHAIN_DYN_LINKER_PATH:aarch64 ?= " -Wl,--dynamic-linker=${EXTERNAL_TOOLCHAIN_LIBDIR}/ld-linux-aarch64.so.1"
TARGET_LDFLAGS:append:class-target = "${EXTERNAL_TOOLCHAIN_DYN_LINKER_PATH}"

EAT_LIBDIR:arm = "lib"
EAT_LIBDIR:aarch64 = "lib64"

GCCMULTILIB:forcevariable = "--disable-multilib"
IMAGE_LINGUAS:forcevariable = ""

# Blacklist toolchain recipes as a belt-and-suspenders way to use the external toolchain
SKIP_RECIPE[glibc] = "Using external toolchain"
SKIP_RECIPE[libgcc] = "Using external toolchain"
SKIP_RECIPE[gcc-cross] = "Using external toolchain"
SKIP_RECIPE[gcc-cross-aarch64] = "Using external toolchain"
SKIP_RECIPE[gcc-cross-arm] = "Using external toolchain"
SKIP_RECIPE[gcc-runtime] = "Using external toolchain"
SKIP_RECIPE[gcc-sanitizers] = "Using external toolchain"

PREFERRED_PROVIDER_linux-libc-headers = "external-arm-toolchain"
PREFERRED_PROVIDER_linux-libc-headers-dev = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-initial = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}g++ = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-for-gcc = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}compilerlibs = "external-arm-toolchain"
PREFERRED_PROVIDER_glibc = "external-arm-toolchain"
PREFERRED_PROVIDER_libgcc = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/libc = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/libc-locale = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/libintl = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/libiconv = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/crypt = "external-arm-toolchain"
PREFERRED_PROVIDER_glibc-thread-db = "external-arm-toolchain"
PREFERRED_PROVIDER_glibc-mtrace = "external-arm-toolchain"
PREFERRED_PROVIDER_libc-mtrace = "external-arm-toolchain"
PREFERRED_PROVIDER_virtual/linux-libc-headers = "external-arm-toolchain"

PREFERRED_PROVIDER_gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "external-arm-sdk-toolchain-${TRANSLATED_TARGET_ARCH}"
PREFERRED_PROVIDER_binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "external-arm-sdk-toolchain-${TRANSLATED_TARGET_ARCH}"
PREFERRED_PROVIDER_gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "external-arm-sdk-toolchain-${TRANSLATED_TARGET_ARCH}"

TOOLCHAIN_OPTIONS = " --sysroot=${STAGING_DIR_HOST}"

DISTRO_FEATURES_LIBC = "ipv4 ipv6 libc-backtrace libc-big-macros libc-bsd libc-cxx-tests libc-catgets libc-crypt \
			libc-crypt-ufc libc-db-aliases libc-envz libc-fcvt libc-fmtmsg libc-fstab libc-ftraverse \
			libc-getlogin libc-idn libc-inet-anl libc-libm libc-libm-big \
			libc-locales libc-locale-code libc-charsets \
			libc-memusage libc-nis libc-nsswitch libc-rcmd libc-rtld-debug libc-spawn libc-streams libc-sunrpc \
			libc-utmp libc-utmpx libc-wordexp libc-posix-clang-wchar libc-posix-regexp libc-posix-regexp-glibc \
			libc-posix-wchar-io"

ENABLE_BINARY_LOCALE_GENERATION = "0"
GLIBC_INTERNAL_USE_BINARY_LOCALE = "precompiled"
LIBCOVERRIDE = ":libc-glibc"
LIBC_DEPENDENCIES:remove = "glibc-gconv-cp1252 glibc-gconv-ibm850 glibc-gconv-iso8859-1 glibc-gconv-iso8859-15 glibc-localedata-i18n"

ERROR_QA[type] ?= "list"
python toolchain_metadata_setup () {
    import subprocess
    if not isinstance(e, bb.event.ConfigParsed):
        return

    d = e.data
    l = d.createCopy()

    external_toolchain = l.getVar('EXTERNAL_TOOLCHAIN', True)
    if not external_toolchain or external_toolchain == 'UNDEFINED':
        bb.fatal("Error: EXTERNAL_TOOLCHAIN must be set to the path to your arm toolchain")

    if not os.path.isabs(external_toolchain):
        bb.fatal("Error: EXTERNAL_TOOLCHAIN path '%s' must be absolute path" % external_toolchain)

    if not os.path.exists(external_toolchain):
        bb.fatal("Error: EXTERNAL_TOOLCHAIN path '%s' does not exist" % external_toolchain)

    # The external toolchain may not have been built with the oe-core preferred
    # gnu hash setting, so ensure that the corresponding sanity check is not an error.
    error_qa = oe.data.typed_value('ERROR_QA', l)
    if 'ldflags' in error_qa:
        error_qa.remove('ldflags')
        d.setVar('ERROR_QA', ' '.join(error_qa))
}
addhandler toolchain_metadata_setup

def populate_toolchain_links(d):
    import errno
    import os
    from glob import glob

    d = d.createCopy()

    pattern = bb.data.expand('${EXTERNAL_TOOLCHAIN}/bin/${TARGET_PREFIX}*', d)
    files = glob(pattern)
    if not files:
        bb.fatal("Unable to populate toolchain binary symlinks")

    bindir = d.getVar('STAGING_BINDIR_TOOLCHAIN', True)
    bb.mkdirhier(bindir)
    for f in files:
        base = os.path.basename(f)
        newpath = os.path.join(bindir, base)
        try:
            os.symlink(f, newpath)
        except OSError as exc:
            if exc.errno == errno.EEXIST:
                break
            bb.fatal("Unable to populate toolchain binary symlink for %s: %s" % (newpath, exc))

require conf/distro/include/external-arm-toolchain-versions.inc
