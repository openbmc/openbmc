DESCRIPTION = "open source C/C++ client library and tools for the memcached server"
DEPENDS = "libevent util-linux"
SECTION = "libdevel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=865490941c91ba790f0ea78dec93bd60"

SRC_URI = "http://launchpad.net/libmemcached/1.0/${PV}/+download/libmemcached-${PV}.tar.gz"

UPSTREAM_CHECK_URI = "https://launchpad.net/libmemcached"

TARGET_LDFLAGS += "-luuid"
TARGET_CFLAGS += "-D__USE_GNU -D_GNU_SOURCE"

PACKAGECONFIG ??= ""
PACKAGECONFIG[sasl] = "--enable-sasl,--disable-sasl,cyrus-sasl"

inherit autotools gettext pkgconfig
