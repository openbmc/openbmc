FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPENDS += " ncurses"

do_compile_prepend() {
	export LIBRARY_PATH=${STAGING_DIR_TARGET}/lib
}

