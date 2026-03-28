SUMMARY = "ngtcp2 project is an effort to implement IETF QUIC protocol"
HOMEPAGE = "https://nghttp2.org/ngtcp2"
BUGTRACKER = "https://github.com/ngtcp2/ngtcp2/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de0966c8ff4f62661a3da92967a75434"

SRC_URI = "gitsm://github.com/ngtcp2/ngtcp2;protocol=https;branch=main;tag=v${PV};name=ngtcp2"
SRCREV = "9e32add590fc74707b62f61aa5e0aa5c6f8a2c60"

DEPENDS = "brotli libev nghttp3"

inherit cmake

PACKAGECONFIG ?= "shared gnutls"

PACKAGECONFIG[static] = "-DENABLE_STATIC_LIB=ON, -DENABLE_STATIC_LIB=OFF"
PACKAGECONFIG[shared] = "-DENABLE_SHARED_LIB=ON, -DENABLE_SHARED_LIB=OFF"
PACKAGECONFIG[build-lib-only] = "-DENABLE_LIB_ONLY=ON, -DENABLE_LIB_ONLY=OFF"
PACKAGECONFIG[openssl] = "-DENABLE_OPENSSL=ON, -DENABLE_OPENSSL=OFF, openssl"
PACKAGECONFIG[gnutls] = "-DENABLE_GNUTLS=ON, -DENABLE_GNUTLS=OFF, gnutls"
