SUMMARY = "A dead simple tool to sign files and verify digital signatures."
DESCRIPTION = "Minisign is a dead simple tool to sign files and verify signatures. It is portable, lightweight, and uses the highly secure Ed25519 public-key signature system. Signature written by minisign can be verified using OpenBSD's signify tool: public key files and signature files are compatible."
HOMEPAGE = "https://jedisct1.github.io/minisign/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ae5258ce978a2a6df50571a88687b53"

DEPENDS = "libsodium"

SRC_URI = "git://github.com/jedisct1/minisign.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "b85e15d45ac9eab34e44596fd309f5b07db9545c"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_STRIP=''"

BBCLASSEXTEND = "native nativesdk"
