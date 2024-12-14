SUMMARY = "SBLIM CMPI Provider Development Support"
DESCRIPTION = "\
This packages provides the C and C++ CMPI header files needed by provider \
developers and can be used standalone. If used for C++ provider development \
it is also necessary to have tog-pegasus-devel installed."
HOMEPAGE = "http://sblim.wiki.sourceforge.net/"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f63d3a4e701129e869e89d7dcac8982"
SECTION = "Development/Libraries"

SRC_URI = "${SOURCEFORGE_MIRROR}/sblim/${BP}.tar.bz2 \
           file://sblim-cmpi-devel-2.0.3-docdir.patch"
SRC_URI[sha256sum] = "1671cabff6b922b6fde897673d9fdafd56c9310f82a7eacc0547d596b9cdfea6"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/sblim/files/development%20pkgs/sblim-cmpi-devel/"
UPSTREAM_CHECK_REGEX = "${BPN}/(?P<pver>\d+(\.\d+)+)"

inherit autotools

BBCLASSEXTEND = "native"
