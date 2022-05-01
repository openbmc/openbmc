SUMMARY = "SpiderMonkey is Mozilla's JavaScript engine written in C/C++"
HOMEPAGE = "https://developer.mozilla.org/en-US/docs/Mozilla/Projects/SpiderMonkey"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}esr/source/firefox-${PV}esr.source.tar.xz \
           file://0001-Cargo.toml-do-not-abort-on-panic.patch \
           file://0002-moz.configure-do-not-look-for-llvm-objdump.patch \
           file://0003-rust.configure-do-not-try-to-find-a-suitable-upstrea.patch \
           file://0004-use-asm-sgidefs.h.patch \
           file://fix-musl-build.patch \
           file://0001-build-do-not-use-autoconf-s-config.sub-to-canonicali.patch \
           file://riscv32.patch \
           file://0005-nojit-32bit-arch-fix.patch \
           file://0006-Fix-build-on-powerpc.patch \
           file://0001-util.configure-fix-one-occasionally-reproduced-confi.patch \
           file://0001-rewrite-cargo-host-linker-in-python3.patch  \
           "
SRC_URI[sha256sum] = "d483a853cbf5c7f93621093432e3dc0b7ed847f2a5318b964828d19f9f087f3a"

S = "${WORKDIR}/firefox-${@d.getVar("PV").replace("esr", "")}"

inherit pkgconfig perlnative python3native rust

DEPENDS += "zlib cargo-native python3 icu"
DEPENDS:remove:mipsarch = "icu"
DEPENDS:remove:powerpc:toolchain-clang = "icu"

B = "${WORKDIR}/build"

export PYTHONPATH = "${S}/build:${S}/third_party/python/PyYAML/lib3:${S}/testing/mozbase/mozfile:${S}/python/mozboot:${S}/third_party/python/distro:${S}/testing/mozbase/mozinfo:${S}/config:${S}/testing/mozbase/manifestparser:${S}/third_party/python/pytoml:${S}/testing/mozbase/mozprocess:${S}/third_party/python/six:${S}/python/mozbuild:${S}/python/mozbuild/mozbuild:${S}/python/mach:${S}/third_party/python/jsmin:${S}/python/mozversioncontrol"

export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CPPFLAGS = "${BUILD_CPPFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"

export AS = "${CC}"

export RUSTFLAGS

JIT ?= ""
JIT:mipsarch = "--disable-jit"
ICU ?= "--with-system-icu"
ICU:mipsarch = ""
ICU:powerpc:toolchain-clang = ""

do_configure() {
    cd ${B}
    python3 ${S}/configure.py \
        --enable-project=js \
        --target=${HOST_SYS} \
        --host=${BUILD_SYS} \
        --prefix=${prefix} \
        --libdir=${libdir} \
        --disable-jemalloc \
        --disable-strip \
        ${JIT} \
        ${ICU}
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

inherit multilib_script multilib_header

MULTILIB_SCRIPTS += " ${PN}-dev:${bindir}/js91-config"

do_install:append() {
    oe_multilib_header mozjs-91/js-config.h
    sed -e 's@${STAGING_DIR_HOST}@@g' \
        -i ${D}${bindir}/js91-config
    rm -f ${D}${libdir}/libjs_static.ajs
}

PACKAGES =+ "lib${BPN}"
FILES:lib${BPN} += "${libdir}/lib*"
