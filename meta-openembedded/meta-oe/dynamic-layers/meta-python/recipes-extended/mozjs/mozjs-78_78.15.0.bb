SUMMARY = "SpiderMonkey is Mozilla's JavaScript engine written in C/C++"
HOMEPAGE = "https://developer.mozilla.org/en-US/docs/Mozilla/Projects/SpiderMonkey"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

SRC_URI = " \
    https://archive.mozilla.org/pub/firefox/releases/${PV}esr/source/firefox-${PV}esr.source.tar.xz \
    file://0001-rust.configure-Skip-all-target-manipulations.patch \
    file://0002-build-do-not-use-autoconf-s-config.sub-to-canonicali.patch \
    file://0003-Do-not-check-binaries-after-build.patch \
    file://0004-Cargo.toml-do-not-abort-on-panic.patch \
    file://0005-Fixup-compatibility-of-mozbuild-with-Python-3.10.patch \
    file://0006-use-asm-sgidefs.h.patch \
    file://0007-fix-musl-build.patch \
    file://0008-riscv.patch \
    file://0009-riscv-Disable-atomic-operations.patch \
    file://0010-riscv-Set-march-correctly.patch \
    file://0011-replace-include-by-code-to-fix-arm-build.patch \
    file://0012-Add-SharedArrayRawBufferRefs-to-public-API.patch \
    file://0013-util.configure-fix-one-occasionally-reproduced-confi.patch \
    file://0014-rewrite-cargo-host-linker-in-python3.patch \
"

SRC_URI[sha256sum] = "a4438d84d95171a6d4fea9c9f02c2edbf0475a9c614d968ebe2eedc25a672151"
S = "${WORKDIR}/firefox-${@d.getVar("PV").replace("esr", "")}"

DEPENDS = " \
    autoconf-2.13-native \
    icu-native \
    icu \
    cargo-native \
    zlib \
    python3-six \
    python3-six-native \
"

inherit autotools pkgconfig rust python3native siteinfo

JIT ?= ""
JIT:mipsarch = "--disable-jit"

EXTRA_OECONF = " \
    --target=${TARGET_SYS} \
    --host=${BUILD_SYS} \
    --prefix=${prefix} \
    --libdir=${libdir} \
    --x-includes=${STAGING_INCDIR} \
    --x-libraries=${STAGING_LIBDIR} \
    --without-system-icu \
    --disable-tests --disable-strip --disable-optimize \
    --disable-jemalloc \
    --with-system-icu \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', "--enable-gold", '--disable-gold', d)} \
    ${JIT} \
"
# Note: Python with mozilla build is a mess: E.g: python-six: to get an error
# free configure we need:
# * python3-six-native in DEPENDS
# * python3-six in DEPENDS
# * path to python-six shipped by mozilla in PYTHONPATH
prepare_python_and_rust() {
    if [ ! -f ${B}/PYTHONPATH ]; then
        oldpath=`pwd`
        cd ${S}
        # Add mozjs python-modules necessary
        PYTHONPATH="${S}/build:${S}/config"
        PYTHONPATH="$PYTHONPATH:${S}/third_party/python/distro:${S}/third_party/python/jsmin"
        PYTHONPATH="$PYTHONPATH:${S}/third_party/python/pytoml:${S}/third_party/python/six"
        PYTHONPATH="$PYTHONPATH:${S}/third_party/python/pyyaml/lib3:${S}/third_party/python/which"
        for sub_dir in python testing/mozbase; do
            for module_dir in `ls $sub_dir -1`;do
                [ $module_dir = "virtualenv" ] && continue
                if [ -d "${S}/$sub_dir/$module_dir" ];then
                    PYTHONPATH="$PYTHONPATH:${S}/$sub_dir/$module_dir"
                fi
            done
        done
        # looks odd but it's huge and we want to see what's in there
        echo "$PYTHONPATH" > ${B}/PYTHONPATH
        cd "$oldpath"
    fi

    export PYTHONPATH=`cat ${B}/PYTHONPATH`

    export RUST_TARGET_PATH="${RUST_TARGET_PATH}"
    export RUST_TARGET="${TARGET_SYS}"
    export RUSTFLAGS="${RUSTFLAGS}"
}

export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CPPFLAGS = "${BUILD_CPPFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"
# otherwise we are asked for yasm...
export AS = "${CC}"

CPPFLAGS:append:mips:toolchain-clang = " -fpie"
CPPFLAGS:append:mipsel:toolchain-clang = " -fpie"

do_configure() {
    prepare_python_and_rust

    cd ${S}/js/src
    autoconf213 --macrodir=${STAGING_DATADIR_NATIVE}/autoconf213 old-configure.in > old-configure

    cd ${B}
    # * use of /tmp can causes problems on heavily loaded hosts
    # * with mozjs-78 we get without:
    # | Path specified in LOCAL_INCLUDES (..) resolves to the topsrcdir or topobjdir (<tmpdir>/oe-core-glibc/work/cortexa72-mortsgna-linux/mozjs-78/78.15.0-r0/firefox-78.15.0/js/src), which is not allowed
    mkdir -p "${B}/lcl_tmp"
    TMPDIR="${B}/lcl_tmp"  CFLAGS="${CFLAGS}" CXXFLAGS="${CXXFLAGS}" ${S}/js/src/configure ${EXTRA_OECONF}

    # inspired by what fedora [1] does: for big endian rebuild icu dat
    # this avoids gjs qemu crash on mips at gir creation
    # [1] https://src.fedoraproject.org/rpms/mozjs78/blob/rawhide/f/mozjs78.spec
    if [ ${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} = "big" -a ! -e ${S}/config/external/icu/data/icudt67b.dat ]; then
        echo "Do big endian icu dat-convert..."
        icupkg -tb ${S}/config/external/icu/data/icudt67l.dat ${S}/config/external/icu/data/icudt67b.dat
        rm -f ${S}/config/external/icu/data/icudt*l.dat
    fi
}

do_compile:prepend() {
    prepare_python_and_rust
}

do_install:prepend() {
    prepare_python_and_rust
}

do_install:append() {
    # tidy up installation
    chmod -x ${D}${libdir}/pkgconfig/*.pc
    sed -i 's:\x24{includedir}/mozjs-78/js/RequiredDefines.h:js/RequiredDefines.h:g' ${D}${libdir}/pkgconfig/*.pc

    rm -f ${D}${libdir}/libjs_static.ajs
}

ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv4 = "arm"

DISABLE_STATIC = ""

PACKAGES =+ "lib${BPN}"
FILES:lib${BPN} += "${libdir}/lib*"
