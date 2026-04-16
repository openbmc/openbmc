SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4942a8ebbbc7f2212bd68a47df264a4f"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "adbdd8f16149e81ac6078a03aca6fc03b592b89ef7b5ed83841c086191be3349"


inherit autotools

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2025-69277] = "fixed-version: fixed in 1.0.20"
