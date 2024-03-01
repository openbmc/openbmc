SUMMARY = "PCI utilities"
DESCRIPTION = 'The PCI Utilities package contains a library for portable access \
to PCI bus configuration space and several utilities based on this library.'
HOMEPAGE = "http://atrey.karlin.mff.cuni.cz/~mj/pciutils.shtml"
SECTION = "console/utils"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
# Can drop make-native when all systems have make 4.3
# https://git.savannah.gnu.org/cgit/make.git/commit/?id=b90fabc8d6f34fb37d428dc0fb1b8b1951a9fbed
# causes space issues in lib/libpci.pc
DEPENDS = "zlib kmod make-native"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/pciutils/pciutils-${PV}.tar.xz \
           file://configure.patch"

SRC_URI[sha256sum] = "3f472ad864473de5ba17f765cc96ef5f33e1b730918d3adda6f945a2a9290df4"

inherit multilib_header pkgconfig update-alternatives

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'hwdb', '', d)}"
PACKAGECONFIG[hwdb] = "HWDB=yes,HWDB=no,udev"

PCI_CONF_FLAG = "ZLIB=yes DNS=yes SHARED=yes STRIP= LIBDIR=${libdir}"

# see configure.patch
do_configure () {
	(
	  cd lib && \
	  # PACKAGECONFIG_CONFARGS for this recipe could only possibly contain 'HWDB=yes/no',
	  # so we put it before ./configure
	  ${PCI_CONF_FLAG} ${PACKAGECONFIG_CONFARGS} ./configure ${PV} ${datadir} ${TARGET_OS} ${TARGET_ARCH}
	)
}

export PREFIX = "${prefix}"
export SBINDIR = "${sbindir}"
export SHAREDIR = "${datadir}"
export MANDIR = "${mandir}"

EXTRA_OEMAKE = "-e MAKEFLAGS= ${PCI_CONF_FLAG}"

ASNEEDED = ""

# The configure script breaks if the HOST variable is set
HOST[unexport] = "1"

do_install () {
	oe_runmake DESTDIR=${D} install install-lib

	install -d ${D}${bindir}

	oe_multilib_header pci/config.h
}

PACKAGES =+ "${PN}-ids libpci"
FILES:${PN}-ids = "${datadir}/pci.ids*"
FILES:libpci = "${libdir}/libpci.so.*"
SUMMARY:${PN}-ids = "PCI utilities - device ID database"
DESCRIPTION:${PN}-ids = "Package providing the PCI device ID database for pciutils."
RDEPENDS:${PN} += "${PN}-ids"

ALTERNATIVE:${PN} = "lspci"
ALTERNATIVE_PRIORITY = "100"
