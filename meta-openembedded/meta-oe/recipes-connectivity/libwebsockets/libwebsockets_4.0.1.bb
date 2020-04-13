SUMMARY = "Canonical libwebsockets.org websocket library"
HOMEPAGE = "https://libwebsockets.org/"
LICENSE = "MIT & Zlib & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c47b078124308a4e1354e8d59f606b7"

DEPENDS = "zlib"

S = "${WORKDIR}/git"
SRCREV = "6eb39388f43f6e2a27f0efcbf8cb2391e38824e9"
SRC_URI = "git://github.com/warmcat/libwebsockets.git;protocol=https;branch=v4.0-stable"

UPSTREAM_CHECK_URI = "https://github.com/warmcat/${BPN}/releases"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

PACKAGECONFIG ?= "libuv client server http2 ssl"
PACKAGECONFIG[client] = "-DLWS_WITHOUT_CLIENT=OFF,-DLWS_WITHOUT_CLIENT=ON,"
PACKAGECONFIG[http2] = "-DLWS_WITH_HTTP2=ON,-DLWS_WITH_HTTP2=OFF,"
PACKAGECONFIG[ipv6] = "-DLWS_IPV6=ON,-DLWS_IPV6=OFF,"
PACKAGECONFIG[libev] = "-DLWS_WITH_LIBEV=ON,-DLWS_WITH_LIBEV=OFF,libev"
PACKAGECONFIG[libuv] = "-DLWS_WITH_LIBUV=ON,-DLWS_WITH_LIBUV=OFF,libuv"
PACKAGECONFIG[server] = "-DLWS_WITHOUT_SERVER=OFF,-DLWS_WITHOUT_SERVER=ON,"
PACKAGECONFIG[ssl] = "-DLWS_WITH_SSL=ON,-DLWS_WITH_SSL=OFF,openssl"
PACKAGECONFIG[testapps] = "-DLWS_WITHOUT_TESTAPPS=OFF,-DLWS_WITHOUT_TESTAPPS=ON,"

EXTRA_OECMAKE += " \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"

PACKAGES =+ "${PN}-testapps"

FILES_${PN}-testapps += "${datadir}/libwebsockets-test-server/*"

CFLAGS_append = " -Wno-error"
