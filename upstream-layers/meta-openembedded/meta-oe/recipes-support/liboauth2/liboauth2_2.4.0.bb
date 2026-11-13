SUMMARY = "OAuth 2.x and OpenID Connect C library"
HOMEPAGE = "https://github.com/OpenIDC/liboauth2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = " \
    git://github.com/OpenIDC/liboauth2;protocol=https;branch=master;tag=v${PV}"

SRCREV = "85da4346435aec77f2ff11b529f2c1e8e5033c44"

DEPENDS = "libpcre2 jansson curl openssl cjose"

inherit pkgconfig autotools-brokensep

PACKAGECONFIG ??= ""
PACKAGECONFIG[memcache] = "--with-memcache,--without-memcache,libmemcached"
PACKAGECONFIG[redis] = "--with-redis,--without-redis,hiredis"
PACKAGECONFIG[jq] = "--with-jq,--without-jq,jq"
PACKAGECONFIG[apache] = "--with-apache,--without-apache,apache2"
PACKAGECONFIG[ccov] = "--enable-code-coverage,--disable-code-coverage,"
