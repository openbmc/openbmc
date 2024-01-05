#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

##
## Purpose:
## This class is used by any recipes that are built using
## Cargo.

inherit cargo_common
inherit rust-target-config

# the binary we will use
CARGO = "cargo"

# We need cargo to compile for the target
BASEDEPENDS:append = " cargo-native"

# Ensure we get the right rust variant
DEPENDS:append:class-target = " rust-native ${RUSTLIB_DEP}"
DEPENDS:append:class-nativesdk = " rust-native ${RUSTLIB_DEP}"
DEPENDS:append:class-native = " rust-native"

# Enable build separation
B = "${WORKDIR}/build"

# In case something fails in the build process, give a bit more feedback on
# where the issue occured
export RUST_BACKTRACE = "1"

RUSTFLAGS ??= ""
BUILD_MODE = "${@['--release', ''][d.getVar('DEBUG_BUILD') == '1']}"
# --frozen flag will prevent network access (which is required since only
# the do_fetch step is authorized to access network)
# and will require an up to date Cargo.lock file.
# This force the package being built to already ship a Cargo.lock, in the end
# this is what we want, at least, for reproducibility of the build.
CARGO_BUILD_FLAGS = "-v --frozen --target ${RUST_HOST_SYS} ${BUILD_MODE} --manifest-path=${CARGO_MANIFEST_PATH}"

# This is based on the content of CARGO_BUILD_FLAGS and generally will need to
# change if CARGO_BUILD_FLAGS changes.
BUILD_DIR = "${@['release', 'debug'][d.getVar('DEBUG_BUILD') == '1']}"
CARGO_TARGET_SUBDIR="${RUST_HOST_SYS}/${BUILD_DIR}"
oe_cargo_build () {
	export RUSTFLAGS="${RUSTFLAGS}"
	bbnote "Using rust targets from ${RUST_TARGET_PATH}"
	bbnote "cargo = $(which ${CARGO})"
	bbnote "${CARGO} build ${CARGO_BUILD_FLAGS} $@"
	"${CARGO}" build ${CARGO_BUILD_FLAGS} "$@"
}

do_compile[progress] = "outof:\s+(\d+)/(\d+)"
cargo_do_compile () {
	oe_cargo_build
}

cargo_do_install () {
	local have_installed=false
	for tgt in "${B}/target/${CARGO_TARGET_SUBDIR}/"*; do
		case $tgt in
		*.so|*.rlib)
			install -d "${D}${rustlibdir}"
			install -m755 "$tgt" "${D}${rustlibdir}"
			have_installed=true
			;;
		*examples)
			if [ -d "$tgt" ]; then
				for example in "$tgt/"*; do
					if [ -f "$example" ] && [ -x "$example" ]; then
						install -d "${D}${bindir}"
						install -m755 "$example" "${D}${bindir}"
						have_installed=true
					fi
				done
			fi
			;;
		*)
			if [ -f "$tgt" ] && [ -x "$tgt" ]; then
				install -d "${D}${bindir}"
				install -m755 "$tgt" "${D}${bindir}"
				have_installed=true
			fi
			;;
		esac
	done
	if ! $have_installed; then
		die "Did not find anything to install"
	fi
}

EXPORT_FUNCTIONS do_compile do_install
