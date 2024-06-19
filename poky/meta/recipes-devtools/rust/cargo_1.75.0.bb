SUMMARY = "Cargo, a package manager for Rust."
HOMEPAGE = "https://crates.io"
LICENSE = "MIT | Apache-2.0"
SECTION = "devel"

DEPENDS = "openssl zlib curl ca-certificates libssh2"

LIC_FILES_CHKSUM = " \
    file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d \
    file://LICENSE-APACHE;md5=71b224ca933f0676e26d5c2e2271331c \
    file://LICENSE-THIRD-PARTY;md5=f257ad009884cb88a3a87d6920e7180a \
"

require rust-source.inc
require rust-snapshot.inc

S = "${RUSTSRC}/src/tools/cargo"
CARGO_VENDORING_DIRECTORY = "${RUSTSRC}/vendor"

inherit cargo pkgconfig

DEBUG_PREFIX_MAP += "-fdebug-prefix-map=${RUSTSRC}/vendor=${TARGET_DBGSRC_DIR}"

do_cargo_setup_snapshot () {
	${UNPACKDIR}/rust-snapshot-components/${CARGO_SNAPSHOT}/install.sh --prefix="${WORKDIR}/${CARGO_SNAPSHOT}" --disable-ldconfig
	# Need to use uninative's loader if enabled/present since the library paths
	# are used internally by rust and result in symbol mismatches if we don't
	if [ ! -z "${UNINATIVE_LOADER}" -a -e "${UNINATIVE_LOADER}" ]; then
		patchelf-uninative ${WORKDIR}/${CARGO_SNAPSHOT}/bin/cargo --set-interpreter ${UNINATIVE_LOADER}
	fi
}

addtask cargo_setup_snapshot after do_unpack before do_configure
do_cargo_setup_snapshot[dirs] += "${WORKDIR}/${CARGO_SNAPSHOT}"
do_cargo_setup_snapshot[vardepsexclude] += "UNINATIVE_LOADER"


do_compile:prepend () {
	export RUSTC_BOOTSTRAP="1"
}

do_install () {
	install -d "${D}${bindir}"
	install -m 755 "${B}/target/${CARGO_TARGET_SUBDIR}/cargo" "${D}${bindir}"
}

do_install:append:class-nativesdk() {
	# To quote the cargo docs, "Cargo also sets the dynamic library path when compiling
	# and running binaries with commands like `cargo run` and `cargo test`". Sadly it
	# sets to libdir but not base_libdir leading to symbol mismatches depending on the
	# host OS. Fully set LD_LIBRARY_PATH to contain both to avoid this.
	create_wrapper ${D}/${bindir}/cargo LD_LIBRARY_PATH=${libdir}:${base_libdir}
}

# Disabled due to incompatibility with libgit2 0.28.x (https://github.com/rust-lang/git2-rs/issues/458, https://bugs.gentoo.org/707746#c1)
# as shipped by Yocto Dunfell.
# According to https://github.com/rust-lang/git2-rs/issues/458#issuecomment-522567539, there are no compatibility guarantees between
# libgit2-sys and arbitrary system libgit2 versions, so better keep this turned off.
#export LIBGIT2_SYS_USE_PKG_CONFIG = "1"

# Needed for pkg-config to be used
export LIBSSH2_SYS_USE_PKG_CONFIG = "1"

# When building cargo-native we don't have cargo-native to use and depend on,
# so we must use the locally set up snapshot to bootstrap the build.
BASEDEPENDS:remove:class-native = "cargo-native"
CARGO:class-native = "${WORKDIR}/${CARGO_SNAPSHOT}/bin/cargo"

DEPENDS:append:class-nativesdk = " nativesdk-rust"
RUSTLIB:append:class-nativesdk = " -L ${STAGING_DIR_HOST}/${SDKPATHNATIVE}/usr/lib/rustlib/${RUST_HOST_SYS}/lib"
RUSTLIB_DEP:class-nativesdk = ""

BBCLASSEXTEND = "native nativesdk"
