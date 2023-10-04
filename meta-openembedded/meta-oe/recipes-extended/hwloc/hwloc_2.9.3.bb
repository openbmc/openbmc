SUMMARY = "Portable Hardware Locality (hwloc) software package"
DESCRIPTION = "The Portable Hardware Locality (hwloc) software package \
 provides a portable abstraction of the hierarchical topology of modern \
 architectures."
HOMEPAGE = "https://www.open-mpi.org/software/hwloc/"
SECTION = "base"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=79179bb373cd55cbd834463a514fb714"

SRC_URI = "https://www.open-mpi.org/software/${BPN}/v2.9/downloads/${BP}.tar.bz2"
SRC_URI[sha256sum] = "5c4062ce556f6d3451fc177ffb8673a2120f81df6835dea6a21a90fbdfff0dec"
UPSTREAM_CHECK_URI = "https://www.open-mpi.org/software/hwloc/v2.9/"

inherit autotools bash-completion pkgconfig

DEPENDS += "ncurses udev zlib"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'libselinux', '', d)}"

PACKAGECONFIG ?= "pci libxml2 ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[libxml2] = "--enable-libxml2,--disable-libxml2,libxml2,libxml2"
PACKAGECONFIG[x11] = "--with-x,--without-x,virtual/libx11 cairo,cairo"
PACKAGECONFIG[pci] = "--enable-pci,--disable-pci,libpciaccess,libpciaccess"

# Split hwloc library into separate subpackage
PACKAGES:prepend = " libhwloc "
FILES:libhwloc += "${libdir}/libhwloc.so.*"
RDEPENDS:${PN} += "libhwloc (= ${EXTENDPKGV})"
