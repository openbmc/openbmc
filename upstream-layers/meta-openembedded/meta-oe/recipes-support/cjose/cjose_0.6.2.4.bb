SUMMARY = "C library implementing the Javascript Object Signing and Encryption (JOSE)"
HOMEPAGE = "https://github.com/OpenIDC/cjose"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7249e2f9437adfb8c88d870438042f0e"

SRC_URI = "git://github.com/OpenIDC/cjose;protocol=https;branch=version-0.6.2.x;tag=v${PV}"

PV = "0.6.2.4"
SRCREV = "8d94c3ad3237ab6a83d2e92fa541542b1b92c023"

DEPENDS = "openssl libcheck jansson"

inherit pkgconfig autotools

