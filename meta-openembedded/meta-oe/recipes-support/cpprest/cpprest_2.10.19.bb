SUMMARY = "Microsoft project for cloud-based client-server communication in native code using a modern asynchronous C++ API design."
SECTION = "libs/network"
HOMEPAGE = "https://github.com/Microsoft/cpprestsdk/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/license.txt;md5=a2e15b954769218ff912468eecd6a02f"
DEPENDS = "openssl websocketpp zlib boost brotli"

EXTRA_OECMAKE = "-DCPPREST_EXPORT_DIR=cmake/cpprestsdk -DCPPREST_EXCLUDE_BROTLI=OFF -DWERROR=OFF"

SRC_URI = "git://github.com/Microsoft/cpprestsdk.git;protocol=https;branch=master \
           file://disable-float-tests.patch \
           file://disable-outside-tests.patch "

# tag 2.10.19
SRCREV= "411a109150b270f23c8c97fa4ec9a0a4a98cdecf"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

SKIP_RECIPE[cpprest] ?= "Needs websocketpp which does not yet work with boost >= 1.87"
