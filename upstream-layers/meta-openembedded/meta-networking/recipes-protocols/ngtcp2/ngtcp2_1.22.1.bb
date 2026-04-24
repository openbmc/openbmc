SUMMARY = "ngtcp2 project is an effort to implement IETF QUIC protocol"
HOMEPAGE = "https://nghttp2.org/ngtcp2"
BUGTRACKER = "https://github.com/ngtcp2/ngtcp2/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de0966c8ff4f62661a3da92967a75434"

SRC_URI = "gitsm://github.com/ngtcp2/ngtcp2;protocol=https;branch=release-1.22;tag=v${PV}"
SRCREV = "716e64b05f4a3709dfc0b0522cf9fd4456d055e5"

DEPENDS = "brotli libev nghttp3"

inherit cmake

PACKAGECONFIG ?= "shared gnutls"

PACKAGECONFIG[static] = "-DENABLE_STATIC_LIB=ON, -DENABLE_STATIC_LIB=OFF"
PACKAGECONFIG[shared] = "-DENABLE_SHARED_LIB=ON, -DENABLE_SHARED_LIB=OFF"
PACKAGECONFIG[build-lib-only] = "-DENABLE_LIB_ONLY=ON, -DENABLE_LIB_ONLY=OFF"
PACKAGECONFIG[openssl] = "-DENABLE_OPENSSL=ON, -DENABLE_OPENSSL=OFF, openssl"
PACKAGECONFIG[gnutls] = "-DENABLE_GNUTLS=ON, -DENABLE_GNUTLS=OFF, gnutls"

CVE_STATUS[CVE-2026-40170] = "fixed-version: fixed in 1.22.1"
