SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f5ecba1fa793fc1f3c8f32d6cb5a37b"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "37b18839e57e7a62834231395c8e962b"
SRC_URI[sha256sum] = "eeadc7e1e1bcef09680fb4837d448fbdf57224978f865ac1c16745868fbd0533"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
