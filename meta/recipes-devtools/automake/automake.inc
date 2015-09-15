SUMMARY = "A GNU tool for automatically generating Makefiles"
DESCRIPTION = "Automake is a tool for automatically generating `Makefile.in' files compliant with the GNU Coding \
Standards. Automake requires the use of Autoconf."
LICENSE = "GPLv2"
HOMEPAGE = "http://www.gnu.org/software/automake/"
SECTION = "devel"

SRC_URI = "${GNU_MIRROR}/automake/automake-${PV}.tar.gz"

inherit autotools texinfo

do_configure() {
	oe_runconf
}

export AUTOMAKE = "${@bb.utils.which('automake', d.getVar('PATH', True))}"

FILES_${PN} += "${datadir}/automake* ${datadir}/aclocal*"
