SUMMARY = "System-level performance monitoring and performance management"
HOMEPAGE = "http://www.pcp.io"
SECTION =  "Applications/System"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=37ab75b580d5aad4ada04260efa3702f \
                    "
COMPATIBLE_HOST:libc-musl = "null"

SRC_URI = "git://github.com/performancecopilot/pcp;branch=stable;protocol=https \
           file://0001-configure-Limit-the-header-search-to-sysroot.patch \
           file://0001-htop-Change-dependency-order-of-header-and-sourcefil.patch \
           file://config.linux \
	   "

SRCREV = "d8d97b79ee47d9dfb076a3242564ed3cae04eba9"


inherit setuptools3-base pkgconfig autotools-brokensep

# Specify any options you want to pass to the configure script using EXTRA_OECONF:
CACHED_CONFIGUREVARS = "PACKAGE_DISTRIBUTION=arch"
EXTRA_OECONF:append = "\
                    --with-dstat-symlink=yes \
                    --with-infiniband=no \
                    --with-python=no \
                    --with-perl=no \
                    --with-python3=yes \
                    --with-pmdajson=no  \
                    --with-perfevent=no \
                    --with-pmdabcc=yes \
                    --with-rundir=${localstatedir}/run \
                    --with-sysconfigdir=${sysconfdir} \
                    --with-logdir=${localstatedir}/log \
                    --with-docdir=${docdir} \
                    "

PACKAGECONFIG[qt5] = "--with-qt=yes --with-qt3d=yes,--with-qt=no --with-qt3d=no,qtbase qt3d"

PACKAGECONFIG ?= ""
