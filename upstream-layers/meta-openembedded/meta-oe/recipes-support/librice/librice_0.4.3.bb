SUMMARY = "Repository containing an (sans-IO) implementation of ICE (RFC8445) protocol written in the Rust programming language"
HOMEPAGE = "https://github.com/ystreet/librice"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d"

SRC_URI += " git://github.com/ystreet/librice.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "afd70fdd7ca96a39824c55e6b373bac97311f2ba"

DEPENDS += "openssl clang-native"

inherit cargo_c cargo-update-recipe-crates pkgconfig

require ${PN}-crates.inc

CARGO_BUILD_FLAGS += " --workspace"

export LIBCLANG_PATH = "${STAGING_LIBDIR_NATIVE}/libclang.so"
