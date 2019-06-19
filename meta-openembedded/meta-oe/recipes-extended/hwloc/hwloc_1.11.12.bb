SUMMARY = "Portable Hardware Locality (hwloc) software package"
DESCRIPTION = "The Portable Hardware Locality (hwloc) software package \
 provides a portable abstraction of the hierarchical topology of modern \
 architectures."
HOMEPAGE = "https://www.open-mpi.org/software/hwloc/"
SECTION = "base"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3282e20dc3cec311deda3c6d4b1f990b"

SRC_URI = "https://www.open-mpi.org/software/${BPN}/v1.11/downloads/${BP}.tar.bz2"
SRC_URI[md5sum] = "c2a2e4e23eeb719ed31a755684697cf9"
SRC_URI[sha256sum] = "ddfb7b9b4571551165b0fd824a340e58814c8c2b4af64c818579d4bc695a417d"

UPSTREAM_CHECK_URI = "https://www.open-mpi.org/software/hwloc/v1.11/"

inherit autotools pkgconfig

DEPENDS += "ncurses udev zlib"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'libselinux', '', d)}"

PACKAGECONFIG ?= "pci libxml2 ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[numactl] = "--enable-libnuma,--disable-libnuma,numactl,numactl"
PACKAGECONFIG[libxml2] = "--enable-libxml2,--disable-libxml2,libxml2,libxml2"
PACKAGECONFIG[x11] = "--with-x,--without-x,virtual/libx11 cairo,cairo"
PACKAGECONFIG[pci] = "--enable-pci,--disable-pci,libpciaccess,libpciaccess"

# Split hwloc library into separate subpackage
PACKAGES_prepend = " libhwloc "
FILES_libhwloc += "${libdir}/libhwloc.so.*"
RDEPENDS_${PN} += "libhwloc (= ${EXTENDPKGV})"
