SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=49ce3b426e6a002e23a1387248e6dbe9"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz \
           file://0001-fix-aarch64-Move-target-pragma-after-arm_neon.h-incl.patch"
SRC_URI[sha256sum] = "018d79fe0a045cca07331d37bd0cb57b2e838c51bc48fd837a1472e50068bbea"

inherit autotools

S = "${WORKDIR}/libsodium-stable"
BBCLASSEXTEND = "native nativesdk"
