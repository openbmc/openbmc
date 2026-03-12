SUMMARY = "Automatically generates Rust FFI bindings to C and C++ libraries."
HOMEPAGE = "https://rust-lang.github.io/rust-bindgen/"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0b9a98cb3dcdefcceb145324693fda9b"

inherit rust cargo cargo-update-recipe-crates

SRC_URI += "git://github.com/rust-lang/rust-bindgen.git;protocol=https;nobranch=1;tag=v${PV}"
SRCREV = "d874de8d646d9b8a3e7ba2db2bcd52f2fba8f1f5"

require ${BPN}-crates.inc

do_install:append:class-native() {
	create_wrapper ${D}/${bindir}/bindgen LIBCLANG_PATH="${STAGING_LIBDIR_NATIVE}"
}

BBCLASSEXTEND = "native nativesdk"
