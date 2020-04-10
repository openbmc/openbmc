SUMMARY = "Perl interface to the libstatgrab library"
DESCRIPTION = "Unix::Statgrab is a wrapper for libstatgrab as available from \
<http://www.i-scream.org/libstatgrab/>. It is a reasonably portable attempt \
to query interesting stats about your computer. It covers information on \
the operating system, CPU, memory usage, network interfaces, hard-disks \
etc."

HOMEPAGE = "https://metacpan.org/release/Unix-Statgrab"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+ | LGPL-2.1+"
DEPENDS += "libcapture-tiny-perl-native"
DEPENDS += "libconfig-autoconf-perl-native"
DEPENDS += "libstatgrab"
RDEPENDS_${PN} += "\
    libstatgrab \
    perl-module-autoloader \
    perl-module-carp \
    perl-module-dynaloader \
    perl-module-exporter \
    perl-module-strict \
    perl-module-vars \
    perl-module-warnings \
"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RE/REHSACK/Unix-Statgrab-${PV}.tar.gz"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
    file://${COMMON_LICENSE_DIR}/GPL-1.0;md5=e9e36a9de734199567a4d769498f743d \
    file://${COMMON_LICENSE_DIR}/LGPL-2.1;md5=1a6d268fd218675ffea8be556788b780 \
"

SRC_URI[md5sum] = "a6bc06b3f7749f7d77a2b1bd13402821"
SRC_URI[sha256sum] = "16a29f7acaeec081bf0e7303ba5ee24fda1d21a1104669b837745f3ea61d6afa"

S = "${WORKDIR}/Unix-Statgrab-${PV}"

inherit cpan pkgconfig ptest-perl

BBCLASSEXTEND = "native"
