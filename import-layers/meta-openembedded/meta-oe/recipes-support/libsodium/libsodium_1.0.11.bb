SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c9f00492f01f5610253fde01c3d2e866"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "b58928d035064b2a46fb564937b83540"
SRC_URI[sha256sum] = "a14549db3c49f6ae2170cbbf4664bd48ace50681045e8dbea7c8d9fb96f9c765"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
