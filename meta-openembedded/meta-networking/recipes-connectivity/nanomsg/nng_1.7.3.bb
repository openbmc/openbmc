SUMMARY = "nanomsg-next-generation -- light-weight brokerless messaging"
DESCRIPTION = "NNG, like its predecessors nanomsg (and to some extent ZeroMQ), is a lightweight, broker-less library, offering a simple API to solve common recurring messaging problems, such as publish/subscribe, RPC-style request/reply, or service discovery."
HOMEPAGE = "https://github.com/nanomsg/nng"
SECTION = "libs/networking"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a41e579bb4326c21c774f8e51e41d8a3"

SRC_URI = "git://github.com/nanomsg/nng.git;branch=master;protocol=https"
SRCREV = "85fbe7f9e4642b554d0d97f2e3ff2aa12978691a"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON -DNNG_ENABLE_NNGCAT=ON"

PACKAGECONFIG ??= ""

PACKAGECONFIG[mbedtls] = "-DNNG_ENABLE_TLS=ON,-DNNG_ENABLE_TLS=OFF,mbedtls"

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}/*"
