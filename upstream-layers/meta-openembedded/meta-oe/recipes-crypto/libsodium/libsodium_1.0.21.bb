SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4942a8ebbbc7f2212bd68a47df264a4f"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz \
           file://0001-Fix-compilation-with-GCC-on-aarch64.patch \
           "
SRC_URI[sha256sum] = "9e4285c7a419e82dedb0be63a72eea357d6943bc3e28e6735bf600dd4883feaf"


inherit autotools

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2025-69277] = "fixed-version: fixed in 1.0.20"
