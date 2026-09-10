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

# rice-c/build.rs builds the internal rice-proto C library in-tree (via
# "cargo cinstall") and then locates it through system-deps + pkg-config.
# The generated rice-proto.pc carries an absolute prefix pointing at the
# in-build OUT_DIR (.../rice-c-<hash>/out/rice-proto-cbuild). pkgconf prepends
# PKG_CONFIG_SYSROOT_DIR (=recipe-sysroot) to that already-absolute prefix,
# producing a doubled, non-existent include path and making build.rs panic
# with "Could not find rice-proto.h header". The internal lib is not staged in
# the sysroot, so no sysroot rewriting must be applied to its .pc file.
# Clear PKG_CONFIG_SYSROOT_DIR for the build so the internal prefix is used
# verbatim. openssl-sys is the only other pkg-config consumer here, so feed it
# the sysroot openssl explicitly (this bypasses its pkg-config probe entirely,
# see openssl-sys find_normal.rs) to keep it pointed at the target sysroot.
export PKG_CONFIG_SYSROOT_DIR = ""
export OPENSSL_LIB_DIR = "${STAGING_LIBDIR}"
export OPENSSL_INCLUDE_DIR = "${STAGING_INCDIR}"

export SYSTEM_DEPS_RICE_PROTO_BUILD_INTERNAL = "always"
