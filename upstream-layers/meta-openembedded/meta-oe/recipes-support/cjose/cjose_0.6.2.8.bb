SUMMARY = "C library implementing the Javascript Object Signing and Encryption (JOSE)"
HOMEPAGE = "https://github.com/OpenIDC/cjose"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7249e2f9437adfb8c88d870438042f0e"

SRC_URI = "git://github.com/OpenIDC/cjose;protocol=https;branch=version-0.6.2.x;tag=v${PV}"

SRCREV = "e787ace6a203d483038a793fd58f958a2f9b48c1"

DEPENDS = "openssl libcheck jansson"

inherit pkgconfig autotools

