SUMMARY = "Automated text and program generation tool"
DESCRIPTION = "AutoGen is a tool designed to simplify the creation and\
 maintenance of programs that contain large amounts of repetitious text.\
 It is especially valuable in programs that have several blocks of text\
 that must be kept synchronized."
HOMEPAGE = "http://www.gnu.org/software/autogen/"
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/autogen/rel${PV}/autogen-${PV}.tar.gz \
           file://increase-timeout-limit.patch \
           file://mk-tpl-config.sh-force-exit-value-to-be-0-in-subproc.patch \
           file://fix-script-err-when-processing-libguile.patch \
           file://0001-config-libopts.m4-regenerate-it-from-config-libopts..patch \
           file://0002-autoopts-mk-tpl-config.sh-fix-perl-path.patch \
"

SRC_URI[md5sum] = "551d15ccbf5b5fc5658da375d5003389"
SRC_URI[sha256sum] = "805c20182f3cb0ebf1571d3b01972851c56fb34348dfdc38799fd0ec3b2badbe"

UPSTREAM_CHECK_URI = "http://ftp.gnu.org/gnu/autogen/"
UPSTREAM_CHECK_REGEX = "rel(?P<pver>\d+(\.\d+)+)/"

DEPENDS = "guile-native libtool-native libxml2-native"

inherit autotools texinfo native pkgconfig

# autogen-native links against libguile which may have been relocated with sstate
# these environment variables ensure there isn't a relocation issue
export GUILE_LOAD_PATH = "${STAGING_DATADIR_NATIVE}/guile/2.0"
export GUILE_LOAD_COMPILED_PATH = "${STAGING_LIBDIR_NATIVE}/guile/2.0/ccache"

export POSIX_SHELL = "/usr/bin/env sh"

do_install_append () {
	create_wrapper ${D}/${bindir}/autogen \
		GUILE_LOAD_PATH=${STAGING_DATADIR_NATIVE}/guile/2.0 \
		GUILE_LOAD_COMPILED_PATH=${STAGING_LIBDIR_NATIVE}/guile/2.0/ccache
}
