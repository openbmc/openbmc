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

# the binary we will use
CARGO = "cargo"

# Enable build separation
B = "${WORKDIR}/build"

do_compile[progress] = "outof:\s+(\d+)/(\d+)"
cargo_do_compile () {
	export RUSTFLAGS="${RUSTFLAGS}"
	bbnote "Using rust targets from ${RUST_TARGET_PATH}"
	bbnote "cargo = $(which ${CARGO})"
	bbnote "${CARGO} build ${CARGO_BUILD_FLAGS} ${PACKAGECONFIG_CONFARGS} $@"
	"${CARGO}" build ${CARGO_BUILD_FLAGS} ${PACKAGECONFIG_CONFARGS} "$@"
}

cargo_do_install () {
	local have_installed=false
	for tgt in "${B}/target/${CARGO_TARGET_SUBDIR}/"*; do
		case $tgt in
		*.so|*.rlib)
			if [ -n "${CARGO_INSTALL_LIBRARIES}" ]; then
				install -d "${D}${rustlibdir}"
				install -m755 "$tgt" "${D}${rustlibdir}"
				have_installed=true
			fi
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
