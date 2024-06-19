SUMMARY = "PCI utilities"
DESCRIPTION = 'The PCI Utilities package contains a library for portable access \
to PCI bus configuration space and several utilities based on this library.'
HOMEPAGE = "https://mj.ucw.cz/sw/pciutils/"
SECTION = "console/utils"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
# Can drop make-native when all systems have make 4.3
# https://git.savannah.gnu.org/cgit/make.git/commit/?id=b90fabc8d6f34fb37d428dc0fb1b8b1951a9fbed
# causes space issues in lib/libpci.pc
DEPENDS = "make-native"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/pciutils/pciutils-${PV}.tar.xz"
SRC_URI[sha256sum] = "f185d116d5ff99b797497efce8f19f1ee8ccc5a668b97a159e3d13472f674154"

inherit multilib_header pkgconfig update-alternatives

PACKAGECONFIG ??= "hwdb kmod zlib"
PACKAGECONFIG[hwdb] = "HWDB=yes,HWDB=no,udev"
PACKAGECONFIG[kmod] = "LIBKMOD=yes,LIBKMOD=no,kmod"
PACKAGECONFIG[zlib] = "ZLIB=yes,ZLIB=no,zlib"

# Configuration options
EXTRA_OEMAKE += "${PACKAGECONFIG_CONFARGS} DNS=yes SHARED=yes"
# Construct a HOST that matches what lib/configure expects
EXTRA_OEMAKE += "HOST="${HOST_ARCH}-${HOST_OS}""
# Toolchain. We need to pass CFLAGS via CC as this is the only variable
# available to the caller without clobbering assignments (notably, -fPIC)
EXTRA_OEMAKE += "CC="${CC} ${CFLAGS}" AR="${AR}" STRIP= LDFLAGS="${LDFLAGS}""
# Paths
EXTRA_OEMAKE += "PREFIX=${prefix} LIBDIR=${libdir} SBINDIR=${sbindir} SHAREDIR=${datadir} MANDIR=${mandir}"

do_install () {
	oe_runmake DESTDIR=${D} install install-lib

	install -d ${D}${bindir}

	oe_multilib_header pci/config.h
}

PACKAGES =+ "${PN}-ids libpci"

FILES:${PN}-ids = "${datadir}/pci.ids*"
SUMMARY:${PN}-ids = "PCI utilities - device ID database"
DESCRIPTION:${PN}-ids = "Package providing the PCI device ID database for pciutils."
RDEPENDS:${PN} += "${PN}-ids"

FILES:libpci = "${libdir}/libpci.so.*"
# The versioned symbols in libpci appear to be causing relocations
INSANE_SKIP:libpci += "textrel"

ALTERNATIVE:${PN} = "lspci"
ALTERNATIVE_PRIORITY = "100"
