SUMMARY = "Canonical libwebsockets.org websocket library"
HOMEPAGE = "https://libwebsockets.org/"
LICENSE = "MIT & Zlib & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c47b078124308a4e1354e8d59f606b7"

DEPENDS = "zlib"

S = "${WORKDIR}/git"
SRCREV = "736f0739ed8c75db0e29c7678b5a9194e957c347"
SRC_URI = "git://github.com/warmcat/libwebsockets.git;protocol=https;branch=v4.1-stable"

UPSTREAM_CHECK_URI = "https://github.com/warmcat/${BPN}/releases"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

PACKAGECONFIG ?= "libuv client server http2 ssl"
PACKAGECONFIG[client] = "-DLWS_WITHOUT_CLIENT=OFF,-DLWS_WITHOUT_CLIENT=ON,"
PACKAGECONFIG[http2] = "-DLWS_WITH_HTTP2=ON,-DLWS_WITH_HTTP2=OFF,"
PACKAGECONFIG[ipv6] = "-DLWS_IPV6=ON,-DLWS_IPV6=OFF,"
PACKAGECONFIG[libevent] = "-DLWS_WITH_LIBEVENT=ON,-DLWS_WITH_LIBEVENT=OFF,libevent"
PACKAGECONFIG[libev] = "-DLWS_WITH_LIBEV=ON,-DLWS_WITH_LIBEV=OFF,libev"
PACKAGECONFIG[libuv] = "-DLWS_WITH_LIBUV=ON,-DLWS_WITH_LIBUV=OFF,libuv"
PACKAGECONFIG[server] = "-DLWS_WITHOUT_SERVER=OFF,-DLWS_WITHOUT_SERVER=ON,"
PACKAGECONFIG[ssl] = "-DLWS_WITH_SSL=ON,-DLWS_WITH_SSL=OFF,openssl"
PACKAGECONFIG[static] = "-DLWS_WITH_STATIC=ON,-DLWS_WITH_STATIC=OFF -DLWS_LINK_TESTAPPS_DYNAMIC=ON,"

EXTRA_OECMAKE += " \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"

PACKAGES =+ "${PN}-testapps ${PN}-evlib-event ${PN}-evlib-uv ${PN}-evlib-ev"

FILES_${PN}-testapps += "${datadir}/libwebsockets-test-server/* ${bindir}/libwebsockets-test-*"
FILES_${PN}-evlib-event += "${libdir}/libwebsockets-evlib_event.so"
FILES_${PN}-evlib-uv += "${libdir}/libwebsockets-evlib_uv.so"
FILES_${PN}-evlib-ev += "${libdir}/libwebsockets-evlib_ev.so"

RDEPENDS_${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libevent', '${PN}-evlib-event', '', d)}"
RDEPENDS_${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libuv', '${PN}-evlib-uv', '', d)}"
RDEPENDS_${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libev', '${PN}-evlib-ev', '', d)}"

RDEPENDS_${PN}-dev += " ${@bb.utils.contains('PACKAGECONFIG', 'static', '${PN}-staticdev', '', d)}"
