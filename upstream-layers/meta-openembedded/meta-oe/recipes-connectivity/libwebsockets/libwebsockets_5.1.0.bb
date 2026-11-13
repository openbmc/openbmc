SUMMARY = "Canonical libwebsockets.org websocket library"
HOMEPAGE = "https://libwebsockets.org/"
LICENSE = "Apache-2.0 AND BSD-3-Clause AND MIT AND OFL-1.1 AND Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d1d99ab4e0f1be7992a891906e47add2"

DEPENDS = "zlib"
DEPENDS:append:class-native = " libcap-native"

SRCREV = "5f39aa483715b813f4d85a692fcb361b5d8d0c1b"
#SRCBRANCH = "v${@oe.utils.trim_version("${PV}", 2)}-stable"
SRCBRANCH = "main"
SRC_URI = " \
    git://github.com/warmcat/libwebsockets.git;protocol=https;branch=${SRCBRANCH};tag=v${PV} \
    file://0001-cmake-remove-source-dir-from-installed-lws_config.h.patch \
"

UPSTREAM_CHECK_URI = "https://github.com/warmcat/${BPN}/releases"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

PACKAGECONFIG ?= "libuv client server http2 ssl ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[client] = "-DLWS_WITHOUT_CLIENT=OFF,-DLWS_WITHOUT_CLIENT=ON,"
PACKAGECONFIG[http2] = "-DLWS_WITH_HTTP2=ON,-DLWS_WITH_HTTP2=OFF,"
PACKAGECONFIG[http3] = "-DLWS_WITH_HTTP3=ON -DLWS_ROLE_QUIC=ON,-DLWS_WITH_HTTP3=OFF -DLWS_ROLE_QUIC=OFF,gnutls"
PACKAGECONFIG[ipv6] = "-DLWS_IPV6=ON,-DLWS_IPV6=OFF,"
PACKAGECONFIG[libevent] = "-DLWS_WITH_LIBEVENT=ON,-DLWS_WITH_LIBEVENT=OFF,libevent"
PACKAGECONFIG[libev] = "-DLWS_WITH_LIBEV=ON,-DLWS_WITH_LIBEV=OFF,libev"
PACKAGECONFIG[libuv] = "-DLWS_WITH_LIBUV=ON,-DLWS_WITH_LIBUV=OFF,libuv"
PACKAGECONFIG[server] = "-DLWS_WITHOUT_SERVER=OFF,-DLWS_WITHOUT_SERVER=ON,"
PACKAGECONFIG[ssl] = "-DLWS_WITH_SSL=ON,-DLWS_WITH_SSL=OFF,${SSL_DEPENDENCY} libcap"
PACKAGECONFIG[gnutls] = "-DLWS_WITH_GNUTLS=ON,-DLWS_WITH_GNUTLS=OFF,gnutls"
PACKAGECONFIG[static] = "-DLWS_WITH_STATIC=ON,-DLWS_WITH_STATIC=OFF -DLWS_LINK_TESTAPPS_DYNAMIC=ON,"
PACKAGECONFIG[systemd] = "-DLWS_WITH_SDEVENT=ON,-DLWS_WITH_SDEVENT=OFF,systemd"
PACKAGECONFIG[examples] = "-DLWS_WITH_MINIMAL_EXAMPLES=ON,-DLWS_WITH_MINIMAL_EXAMPLES=OFF"

SSL_DEPENDENCY = "${@bb.utils.contains('PACKAGECONFIG', 'gnutls', '', 'openssl', d)}"

python __anonymous() {
  if bb.utils.contains('PACKAGECONFIG', 'systemd', True, False, d) and not bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d):
    bb.fatal("PACKAGECONFIG contains systemd but DISTRO_FEATURES doesn't")
}

EXTRA_OECMAKE += " \
    -DLWS_WITHOUT_TESTAPPS=ON \
    -DLWS_HAVE_HMAC_CTX_new=ON \
    -DDISABLE_WERROR=ON \
"

do_compile:prepend() {
    sed -i -e 's|/etc/ssl|${RECIPE_SYSROOT_NATIVE}/etc/ssl|g' ${S}/lib/tls/CMakeLists.txt
}

do_install:append() {
    sed -i -e 's|${STAGING_BASELIBDIR}/libcap.so|cap|g' ${D}${libdir}/cmake/libwebsockets/libwebsockets-config.cmake
    sed -i -e 's|${STAGING_BASELIBDIR}/libcap.so|cap|g' ${D}${libdir}/cmake/libwebsockets/LibwebsocketsTargets.cmake
    sed -i -e 's|${STAGING_LIBDIR}/libcrypto.so|crypto|g' ${D}${libdir}/cmake/libwebsockets/LibwebsocketsTargets.cmake
    sed -i -e 's|${STAGING_LIBDIR}/libssl.so|ssl|g' ${D}${libdir}/cmake/libwebsockets/LibwebsocketsTargets.cmake
    sed -i -e 's|${STAGING_LIBDIR}/libuv.so|uv|g' ${D}${libdir}/cmake/libwebsockets/LibwebsocketsTargets.cmake
    sed -i -e 's|${STAGING_INCDIR}||g' ${D}${libdir}/cmake/libwebsockets/LibwebsocketsTargets.cmake \
                                       ${D}${libdir}/cmake/libwebsockets/libwebsockets-config.cmake
    sed -i -e 's|${STAGING_LIBDIR}/||g' ${D}${libdir}/cmake/libwebsockets/LibwebsocketsTargets.cmake \
                                        ${D}${libdir}/cmake/libwebsockets/libwebsockets-config.cmake
}

PACKAGES =+ "${PN}-testapps ${PN}-evlib-event ${PN}-evlib-uv ${PN}-evlib-ev ${PN}-evlib-sd"

FILES:${PN}-testapps += "${datadir}/libwebsockets-test-server/* ${bindir}/libwebsockets-test-*"
FILES:${PN}-evlib-event += "${libdir}/libwebsockets-evlib_event.so"
FILES:${PN}-evlib-uv += "${libdir}/libwebsockets-evlib_uv.so"
FILES:${PN}-evlib-ev += "${libdir}/libwebsockets-evlib_ev.so"
FILES:${PN}-evlib-sd += "${libdir}/libwebsockets-evlib_sd.so"

RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libevent', '${PN}-evlib-event', '', d)}"
RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libuv', '${PN}-evlib-uv', '', d)}"
RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'libev', '${PN}-evlib-ev', '', d)}"
RDEPENDS:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'systemd', '${PN}-evlib-sd', '', d)}"

RDEPENDS:${PN}-dev += " ${@bb.utils.contains('PACKAGECONFIG', 'static', '${PN}-staticdev', '', d)}"

# Avoid absolute paths to end up in the sysroot.
SSTATE_SCAN_FILES += "*.cmake"

BBCLASSEXTEND = "native"

CVE_STATUS[CVE-2025-1866] = "fixed-version: fixed since 4.3.4"
