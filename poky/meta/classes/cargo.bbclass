##
## Purpose:
## This class is used by any recipes that are built using
## Cargo.

inherit cargo_common

# the binary we will use
CARGO = "cargo"

# We need cargo to compile for the target
BASEDEPENDS:append = " cargo-native"

# Ensure we get the right rust variant
DEPENDS:append:class-target = " virtual/${TARGET_PREFIX}rust ${RUSTLIB_DEP}"
DEPENDS:append:class-nativesdk = " virtual/${TARGET_PREFIX}rust ${RUSTLIB_DEP}"
DEPENDS:append:class-native = " rust-native"

# Enable build separation
B = "${WORKDIR}/build"

# In case something fails in the build process, give a bit more feedback on
# where the issue occured
export RUST_BACKTRACE = "1"

# The directory of the Cargo.toml relative to the root directory, per default
# assume there's a Cargo.toml directly in the root directory
CARGO_SRC_DIR ??= ""

# The actual path to the Cargo.toml
MANIFEST_PATH ??= "${S}/${CARGO_SRC_DIR}/Cargo.toml"

RUSTFLAGS ??= ""
BUILD_MODE = "${@['--release', ''][d.getVar('DEBUG_BUILD') == '1']}"
CARGO_BUILD_FLAGS = "-v --target ${HOST_SYS} ${BUILD_MODE} --manifest-path=${MANIFEST_PATH}"

# This is based on the content of CARGO_BUILD_FLAGS and generally will need to
# change if CARGO_BUILD_FLAGS changes.
BUILD_DIR = "${@['release', 'debug'][d.getVar('DEBUG_BUILD') == '1']}"
CARGO_TARGET_SUBDIR="${HOST_SYS}/${BUILD_DIR}"
oe_cargo_build () {
	export RUSTFLAGS="${RUSTFLAGS}"
	export RUST_TARGET_PATH="${RUST_TARGET_PATH}"
	bbnote "cargo = $(which ${CARGO})"
	bbnote "rustc = $(which ${RUSTC})"
	bbnote "${CARGO} build ${CARGO_BUILD_FLAGS} $@"
	"${CARGO}" build ${CARGO_BUILD_FLAGS} "$@"
}

do_compile[progress] = "outof:\s+(\d+)/(\d+)"
cargo_do_compile () {
	oe_cargo_fix_env
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
