SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=092a09b78c3be486fac807435bf17b7a"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "0a66b86fd3aab3fe4c858edcd2772760"
SRC_URI[sha256sum] = "c0f191d2527852641e0a996b7b106d2e04cbc76ea50731b2d0babd3409301926"

inherit autotools
