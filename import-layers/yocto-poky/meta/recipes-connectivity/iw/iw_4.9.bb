SUMMARY = "nl80211 based CLI configuration utility for wireless devices"
DESCRIPTION = "iw is a new nl80211 based CLI configuration utility for \
wireless devices. It supports almost all new drivers that have been added \
to the kernel recently. "
HOMEPAGE = "http://wireless.kernel.org/en/users/Documentation/iw"
SECTION = "base"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=878618a5c4af25e9b93ef0be1a93f774"

DEPENDS = "libnl"

SRC_URI = "http://www.kernel.org/pub/software/network/iw/${BP}.tar.gz \
           file://0001-iw-version.sh-don-t-use-git-describe-for-versioning.patch \
           file://separate-objdir.patch \
"

SRC_URI[md5sum] = "06e96ab7a5c652f8eaed6f71533a9e0f"
SRC_URI[sha256sum] = "12f921f3dbe0f33c309f5f2891cccf5325c94bd48dceeb102de183f5f048a9e2"

inherit pkgconfig

EXTRA_OEMAKE = "\
    -f '${S}/Makefile' \
    \
    'PREFIX=${prefix}' \
    'SBINDIR=${sbindir}' \
    'MANDIR=${mandir}' \
"
B = "${WORKDIR}/build"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}
