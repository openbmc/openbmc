SUMMARY = "Canonical libwebsockets.org websocket library"
HOMEPAGE = "https://libwebsockets.org/"
LICENSE = "MIT & Zlib & BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8bea43a2eb5d713c338819a0be07797"

DEPENDS = "zlib"

S = "${WORKDIR}/git"
SRCREV = "8d605f0649ed1ab6d27a443c7688598ea21fdb75"
SRC_URI = "git://github.com/warmcat/libwebsockets.git;protocol=https;branch=v4.2-stable"

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

FILES:${PN}-testapps += "${datadir}/libwebsockets-test-server/* ${bindir}/libwebsockets-test-*"
FILES:${PN}-evlib-event += "${libdir}/libwebsockets-evlib_event.so"
FILES:${PN}-evlib-uv += "${libdir}/libwebsockets-evlib_uv.so"
FILES:${PN}-evlib-ev += "${libdir}/libwebsockets-evlib_ev.so"

RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libevent', '${PN}-evlib-event', '', d)}"
RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libuv', '${PN}-evlib-uv', '', d)}"
RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libev', '${PN}-evlib-ev', '', d)}"

RDEPENDS:${PN}-dev += " ${@bb.utils.contains('PACKAGECONFIG', 'static', '${PN}-staticdev', '', d)}"

# Avoid absolute paths to end up in the sysroot.
SSTATE_SCAN_FILES += "*.cmake"
