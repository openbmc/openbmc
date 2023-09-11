SUMMARY = "System performance benchmark"
HOMEPAGE = "http://github.com/akopytov/sysbench"
SECTION = "console/tests"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libtool luajit concurrencykit"

inherit autotools-brokensep pkgconfig

# The project has moved from Sourceforge to Launchpad, to Github. Use the source tarball from
# Launchpad until the next release is available from Github.
SRC_URI = "git://github.com/akopytov/sysbench.git;protocol=https;branch=master"
SRCREV = "ebf1c90da05dea94648165e4f149abc20c979557"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(arm|aarch64|i.86|x86_64).*-linux*"

EXTRA_OECONF += "--enable-largefile --with-system-luajit --with-system-ck --without-gcc-arch --with-lib-prefix=no"
PACKAGECONFIG ??= ""
PACKAGECONFIG[aio] = "--enable-aio,--disable-aio,libaio,"
PACKAGECONFIG[mysql] = "--with-mysql \
                        --with-mysql-includes=${STAGING_INCDIR}/mysql \
                        --with-mysql-libs=${STAGING_LIBDIR}, \
                        --without-mysql,mysql5"

do_configure:prepend() {
    touch ${S}/NEWS ${S}/AUTHORS
}
