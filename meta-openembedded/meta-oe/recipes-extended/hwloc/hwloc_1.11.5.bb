SUMMARY = "Portable Hardware Locality (hwloc) software package"
DESCRIPTION = "The Portable Hardware Locality (hwloc) software package \
 provides a portable abstraction of the hierarchical topology of modern \
 architectures."
HOMEPAGE = "https://www.open-mpi.org/software/hwloc/"
SECTION = "base"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3282e20dc3cec311deda3c6d4b1f990b"

SRC_URI = "https://www.open-mpi.org/software/${PN}/v1.11/downloads/${BP}.tar.bz2"
SRC_URI[md5sum] = "96c34136ff416d2b13a7821c27477bed"
SRC_URI[sha256sum] = "95d80286dfe658a3f79e2ac90698782bb36e5504f4bac1bba2394ba14dbbad24"

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
