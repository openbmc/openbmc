require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

DEPENDS += "bc-native dtc-native gnutls-native python3-pyelftools-native"

SRCREV = "e50b1e8715011def8aff1588081a2649a2c6cd47"
SRC_URI = "git://source.denx.de/u-boot/u-boot.git;protocol=https;branch=master"

# u-boot/lib/rsa/rsa-sign.c uses the OpenSSL engine API, but this has been removed
# from OpenSSL 4.  Upstream u-boot has been fixed but we can enable the stub engine
# API in OpenSSL until this recipe is removed.
BUILD_CFLAGS += "-DOPENSSL_ENGINE_STUBS"
