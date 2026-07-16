SUMMARY = "Automatically generates Rust FFI bindings to C and C++ libraries."
HOMEPAGE = "https://rust-lang.github.io/rust-bindgen/"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0b9a98cb3dcdefcceb145324693fda9b"

inherit rust cargo cargo-update-recipe-crates

SRC_URI += "git://github.com/rust-lang/rust-bindgen.git;protocol=https;nobranch=1;tag=v${PV}"
SRCREV = "d874de8d646d9b8a3e7ba2db2bcd52f2fba8f1f5"

require ${BPN}-crates.inc

# bindgen uses the clang-sys crate to locate a 'clang' executable for querying
# default include paths. clang-sys's search order is: CLANG_PATH, libclang's dir,
# 'llvm-config --bindir', then PATH. When clang is the toolchain, the crossscripts
# llvm-config wrapper honours YOCTO_ALTERNATE_EXE_PATH and reports the target
# sysroot bindir, so clang-sys's 'llvm-config --bindir' step resolves to a target
# clang that cannot run on the build host (native bindgen then fails with
# "could not run executable .../clang-NN: No such file or directory", e.g. when
# building mesa with the rusticl/opencl PACKAGECONFIG). Set CLANG_PATH to the
# native clang so the runnable host binary is used first.
do_install:append:class-native() {
	create_wrapper ${D}/${bindir}/bindgen \
		LIBCLANG_PATH="${STAGING_LIBDIR_NATIVE}" \
		CLANG_PATH="${STAGING_BINDIR_NATIVE}/clang"
}

do_install:append:class-nativesdk() {
	create_wrapper ${D}/${bindir}/bindgen \
		LIBCLANG_PATH="${libdir}" \
		CLANG_PATH="${bindir}/clang"
}

BBCLASSEXTEND = "native nativesdk"
