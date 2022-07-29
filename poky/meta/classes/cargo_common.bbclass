##
## Purpose:
## This class is to support building with cargo. It
## must be different than cargo.bbclass because Rust
## now builds with Cargo but cannot use cargo.bbclass
## due to dependencies and assumptions in cargo.bbclass
## that Rust & Cargo are already installed. So this
## is used by cargo.bbclass and Rust
##

# add crate fetch support
inherit rust-common

# Where we download our registry and dependencies to
export CARGO_HOME = "${WORKDIR}/cargo_home"

# The pkg-config-rs library used by cargo build scripts disables itself when
# cross compiling unless this is defined. We set up pkg-config appropriately
# for cross compilation, so tell it we know better than it.
export PKG_CONFIG_ALLOW_CROSS = "1"

# Don't instruct cargo to use crates downloaded by bitbake. Some rust packages,
# for example the rust compiler itself, come with their own vendored sources.
# Specifying two [source.crates-io] will not work.
CARGO_DISABLE_BITBAKE_VENDORING ?= "0"

# Used by libstd-rs to point to the vendor dir included in rustc src
CARGO_VENDORING_DIRECTORY ?= "${CARGO_HOME}/bitbake"

CARGO_RUST_TARGET_CCLD ?= "${RUST_TARGET_CCLD}"
cargo_common_do_configure () {
	mkdir -p ${CARGO_HOME}/bitbake

	cat <<- EOF > ${CARGO_HOME}/config
	# EXTRA_OECARGO_PATHS
	paths = [
	$(for p in ${EXTRA_OECARGO_PATHS}; do echo \"$p\",; done)
	]
	EOF

	cat <<- EOF >> ${CARGO_HOME}/config

	# Local mirror vendored by bitbake
	[source.bitbake]
	directory = "${CARGO_VENDORING_DIRECTORY}"
	EOF

	if [ ${CARGO_DISABLE_BITBAKE_VENDORING} = "0" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		[source.crates-io]
		replace-with = "bitbake"
		local-registry = "/nonexistant"
		EOF
	fi

	cat <<- EOF >> ${CARGO_HOME}/config

	[http]
	# Multiplexing can't be enabled because http2 can't be enabled
	# in curl-native without dependency loops
	multiplexing = false

	# Ignore the hard coded and incorrect path to certificates
	cainfo = "${STAGING_ETCDIR_NATIVE}/ssl/certs/ca-certificates.crt"

	EOF

	cat <<- EOF >> ${CARGO_HOME}/config

	# HOST_SYS
	[target.${HOST_SYS}]
	linker = "${CARGO_RUST_TARGET_CCLD}"
	EOF

	if [ "${HOST_SYS}" != "${BUILD_SYS}" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		# BUILD_SYS
		[target.${BUILD_SYS}]
		linker = "${RUST_BUILD_CCLD}"
		EOF
	fi

	# Put build output in build directory preferred by bitbake instead of
	# inside source directory unless they are the same
	if [ "${B}" != "${S}" ]; then
		cat <<- EOF >> ${CARGO_HOME}/config

		[build]
		# Use out of tree build destination to avoid poluting the source tree
		target-dir = "${B}/target"
		EOF
	fi

	cat <<- EOF >> ${CARGO_HOME}/config

	[term]
	progress.when = 'always'
	progress.width = 80
	EOF
}

oe_cargo_fix_env () {
	export CC="${RUST_TARGET_CC}"
	export CXX="${RUST_TARGET_CXX}"
	export CFLAGS="${CFLAGS}"
	export CXXFLAGS="${CXXFLAGS}"
	export AR="${AR}"
	export TARGET_CC="${RUST_TARGET_CC}"
	export TARGET_CXX="${RUST_TARGET_CXX}"
	export TARGET_CFLAGS="${CFLAGS}"
	export TARGET_CXXFLAGS="${CXXFLAGS}"
	export TARGET_AR="${AR}"
	export HOST_CC="${RUST_BUILD_CC}"
	export HOST_CXX="${RUST_BUILD_CXX}"
	export HOST_CFLAGS="${BUILD_CFLAGS}"
	export HOST_CXXFLAGS="${BUILD_CXXFLAGS}"
	export HOST_AR="${BUILD_AR}"
}

EXTRA_OECARGO_PATHS ??= ""

EXPORT_FUNCTIONS do_configure
