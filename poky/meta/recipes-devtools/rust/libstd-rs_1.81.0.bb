SUMMARY = "Rust standard libaries"
HOMEPAGE = "http://www.rust-lang.org"
SECTION = "devel"
LICENSE = "(MIT | Apache-2.0) & Unicode-TOU"
LIC_FILES_CHKSUM = "file://../../COPYRIGHT;md5=c2cccf560306876da3913d79062a54b9"

require rust-source.inc

# The dummy crate named `sysroot` represents the standard library target.
#
# See fd4c81f4c19e ("Add a `sysroot` crate to represent the standard library crates")
# https://github.com/rust-lang/rust/pull/108865/
S = "${RUSTSRC}/library/sysroot"

RUSTLIB_DEP = ""
inherit cargo

CVE_PRODUCT = "rust"

DEPENDS:append:libc-musl = " libunwind"
# rv32 does not have libunwind ported yet
DEPENDS:remove:riscv32 = "libunwind"
DEPENDS:remove:riscv64 = "libunwind"

# Embed bitcode in order to allow compiling both with and without LTO
RUSTFLAGS += "-Cembed-bitcode=yes"
# Needed so cargo can find libbacktrace
RUSTFLAGS += "-L ${STAGING_LIBDIR} -C link-arg=-Wl,-soname,libstd.so"

CARGO_FEATURES ?= "panic-unwind backtrace"
CARGO_BUILD_FLAGS += "--features '${CARGO_FEATURES}'"
CARGO_VENDORING_DIRECTORY = "${RUSTSRC}/vendor"

do_compile:prepend () {
    export CARGO_TARGET_DIR="${B}"
    # For Rust 1.13.0 and newer
    export RUSTC_BOOTSTRAP="1"
}

do_install () {
    mkdir -p ${D}${rustlibdir}

    # With the incremental build support added in 1.24, the libstd deps directory also includes dependency
    # files that get installed. Those are really only needed to incrementally rebuild the libstd library
    # itself and don't need to be installed.
    rm -f ${B}/${RUST_TARGET_SYS}/${BUILD_DIR}/deps/*.d
    cp ${B}/${RUST_TARGET_SYS}/${BUILD_DIR}/deps/* ${D}${rustlibdir}
}

BBCLASSEXTEND = "nativesdk"

# Since 1.70.0 upgrade this fails to build with gold:
# http://errors.yoctoproject.org/Errors/Details/708194/
# ld: error: version script assignment of  to symbol __rust_alloc_error_handler_should_panic failed: symbol not defined
LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '-fuse-ld=bfd', '', d)}"
