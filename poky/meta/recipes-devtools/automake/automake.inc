SUMMARY = "A GNU tool for automatically generating Makefiles"
DESCRIPTION = "Automake is a tool for automatically generating `Makefile.in' files compliant with the GNU Coding \
Standards. Automake requires the use of Autoconf."
LICENSE = "GPL-2.0-only"
HOMEPAGE = "http://www.gnu.org/software/automake/"
SECTION = "devel"

SRC_URI = "${GNU_MIRROR}/automake/automake-${PV}.tar.gz"

inherit autotools texinfo

do_configure() {
	# We can end up patching macros, which would then mean autoreconf
	# Cheat by saying everything is up to date.
	touch ${S}/aclocal.m4 ${S}/Makefile.in ${S}/configure
	oe_runconf
}

export AUTOMAKE = "${@bb.utils.which('automake', d.getVar('PATH'))}"

FILES:${PN} += "${datadir}/automake* ${datadir}/aclocal*"
