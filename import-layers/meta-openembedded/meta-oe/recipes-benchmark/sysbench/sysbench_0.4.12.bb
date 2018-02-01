SUMMARY = "System performance benchmark"
HOMEPAGE = "http://github.com/akopytov/sysbench"
SECTION = "console/tests"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools

# The project has moved from Sourceforge to Launchpad, to Github. Use the source tarball from
# Launchpad until the next release is available from Github.
SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+files/${BPN}_${PV}.orig.tar.gz"

SRC_URI[md5sum] = "3a6d54fdd3fe002328e4458206392b9d"
SRC_URI[sha256sum] = "83fa7464193e012c91254e595a89894d8e35b4a38324b52a5974777e3823ea9e"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'largefile', d)}"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile,,"
PACKAGECONFIG[aio] = "--enable-aio,--disable-aio,libaio,"
PACKAGECONFIG[mysql] = "--with-mysql \
                        --with-mysql-includes=${STAGING_INCDIR}/mysql \
                        --with-mysql-libs=${STAGING_LIBDIR}, \
                        --without-mysql,mysql5"

do_configure_prepend() {
    touch ${S}/NEWS ${S}/AUTHORS
}
