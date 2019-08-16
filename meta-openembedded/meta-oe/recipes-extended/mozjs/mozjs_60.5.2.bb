SUMMARY = "SpiderMonkey is Mozilla's JavaScript engine written in C/C++"
HOMEPAGE = "https://developer.mozilla.org/en-US/docs/Mozilla/Projects/SpiderMonkey"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

SRC_URI = "https://dev.gentoo.org/~axs/distfiles/mozjs-60.5.2.tar.bz2 \
           file://0001-js.pc.in-do-not-include-RequiredDefines.h-for-depend.patch \
           file://0010-fix-cross-compilation-on-i586-targets.patch \
           file://0001-do-not-create-python-environment.patch \
           file://0002-fix-cannot-find-link.patch \
           file://0003-workaround-autoconf-2.13-detection-failed.patch \
           file://0004-do-not-use-autoconf-2.13-to-refresh-old.configure.patch \
           file://0005-fix-do_compile-failed-on-mips.patch \
           file://add-riscv-support.patch \
           file://0001-mozjs-fix-coredump-caused-by-getenv.patch \
           file://format-overflow.patch \
           file://0001-To-fix-build-error-on-arm32BE.patch \
           file://JS_PUBLIC_API.patch \
           file://0001-riscv-Disable-atomic-operations.patch \
           file://fallback-to-2011-C++-standard.patch \
           "
SRC_URI_append_libc-musl = " \
           file://0006-support-musl.patch \
           file://0001-js-Fix-build-with-musl.patch \
           "
SRC_URI_append_mipsarchn32 = " \
           file://0001-fix-compiling-failure-on-mips64-n32-bsp.patch \
           "
SRC_URI[md5sum] = "023ed014e9e93d01620d121bc06a3589"
SRC_URI[sha256sum] = "f51039c997415fd0f13f8e01966b4a8ff80cbf90deb8b14c18827104a369cc0d"

inherit autotools pkgconfig perlnative pythonnative

inherit distro_features_check
CONFLICT_DISTRO_FEATURES_mipsarchn32 = "ld-is-gold"

DEPENDS += "nspr zlib python-six-native python-pytoml-native \
            python-jsmin-native python-futures-native \
            python-which-native"

# Disable null pointer optimization in gcc >= 6
# https://bugzilla.redhat.com/show_bug.cgi?id=1328045
CFLAGS += "-fno-tree-vrp -fno-strict-aliasing -fno-delete-null-pointer-checks"
CXXFLAGS += "-fno-tree-vrp -fno-strict-aliasing -fno-delete-null-pointer-checks"

# nspr's package-config is ignored so set libs manually
EXTRA_OECONF = " \
    --target=${TARGET_SYS} \
    --host=${BUILD_SYS} \
    --prefix=${prefix} \
    --libdir=${libdir} \
    --disable-tests --disable-strip --disable-optimize \
    --disable-jemalloc \
    --with-nspr-libs='-lplds4 -lplc4 -lnspr4' \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', "--enable-gold", '--disable-gold', d)} \
"

# Without this, JS_Init() will fail for mips64.
EXTRA_OECONF_append_mips64 = " --with-intl-api=build"

EXTRA_OECONF_append_mipsarch = " --disable-ion"
EXTRA_OECONF_append_riscv64 = " --disable-ion"
EXTRA_OECONF_append_riscv32 = " --disable-ion"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR},--x-includes=no --x-libraries=no,virtual/libx11"

EXTRA_OEMAKE_task-compile += "BUILD_OPT=1 OS_LDFLAGS='-Wl,-latomic ${LDFLAGS}'"
EXTRA_OEMAKE_task-install += "STATIC_LIBRARY_NAME=js_static"

export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CPPFLAGS = "${BUILD_CPPFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"

do_configure() {
    export SHELL="/bin/sh"
    export TMP="${B}"
    ${S}/js/src/configure ${EXTRA_OECONF}
}

do_compile_prepend() {
    export SHELL="/bin/sh"
    export S
    export PYTHONPATH
    cd ${S}
    for sub_dir in python testing/mozbase; do
        for module_dir in `ls $sub_dir -1`;do
            [ $module_dir = "virtualenv" ] && continue
            if [ -d "${S}/$sub_dir/$module_dir" ];then
                PYTHONPATH="$PYTHONPATH:${S}/$sub_dir/$module_dir"
            fi
        done
    done
    PYTHONPATH="$PYTHONPATH:${S}/config:${S}/build"
    cd -
}

do_install_prepend() {
    export SHELL="/bin/sh"
    export S
    export PYTHONPATH
    cd ${S}
    for sub_dir in python testing/mozbase; do
        for module_dir in `ls $sub_dir -1`;do
            [ $module_dir = "virtualenv" ] && continue
            if [ -d "${S}/$sub_dir/$module_dir" ];then
                PYTHONPATH="$PYTHONPATH:${S}/$sub_dir/$module_dir"
            fi
        done
    done
    PYTHONPATH="$PYTHONPATH:${S}/config:${S}/build"
    cd -
}

PACKAGES =+ "lib${BPN}"
FILES_lib${BPN} += "${libdir}/lib*.so"
FILES_${PN}-dev += "${bindir}/js60-config"

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:2172: Error: shifts in CMP/MOV instructions are only supported in unified syntax -- `mov r2,r1,LSR#20'
#| {standard input}:2173: Error: unshifted register required -- `bic r2,r2,#(1<<11)'
#| {standard input}:2174: Error: unshifted register required -- `orr r1,r1,#(1<<20)'
#| {standard input}:2176: Error: instruction not supported in Thumb16 mode -- `subs r2,r2,#0x300'
#| {standard input}:2178: Error: instruction not supported in Thumb16 mode -- `subs r5,r2,#52'
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv4 = "arm"

DISABLE_STATIC = ""
