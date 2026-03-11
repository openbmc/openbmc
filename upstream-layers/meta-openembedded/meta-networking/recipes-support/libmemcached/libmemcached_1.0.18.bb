DESCRIPTION = "open source C/C++ client library and tools for the memcached server"
DEPENDS = "libevent util-linux"
SECTION = "libdevel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=865490941c91ba790f0ea78dec93bd60"

SRC_URI = "\
           http://launchpad.net/libmemcached/1.0/${PV}/+download/libmemcached-${PV}.tar.gz \
           file://crosscompile.patch \
           file://0001-configure.ac-Do-not-configure-build-aux.patch \
           file://0001-Fix-comparison-types.patch \
           file://0002-POSIX_SPAWN_USEVFORK-is-not-linux-specific-but-glibc.patch \
           "
SRC_URI[md5sum] = "b3958716b4e53ddc5992e6c49d97e819"
SRC_URI[sha256sum] = "e22c0bb032fde08f53de9ffbc5a128233041d9f33b5de022c0978a2149885f82"

UPSTREAM_CHECK_URI = "https://launchpad.net/libmemcached"

CVE_STATUS[CVE-2023-27478] = "fixed-version: this problem was not yet introduced in 1.0.18"

TARGET_LDFLAGS += "-luuid"
TARGET_CFLAGS += "-D__USE_GNU -D_GNU_SOURCE"

PACKAGECONFIG ??= ""
PACKAGECONFIG[sasl] = "--enable-sasl,--disable-sasl,cyrus-sasl"

inherit autotools gettext pkgconfig
