SUMMARY = "Microsoft project for cloud-based client-server communication in native code using a modern asynchronous C++ API design."
SECTION = "libs/network"
HOMEPAGE = "https://github.com/Microsoft/cpprestsdk/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/license.txt;md5=a2e15b954769218ff912468eecd6a02f"
DEPENDS = "openssl websocketpp zlib boost brotli"

EXTRA_OECMAKE = "-DCPPREST_EXPORT_DIR=cmake -DCPPREST_EXCLUDE_BROTLI=OFF"

SRC_URI = "git://github.com/Microsoft/cpprestsdk.git;protocol=https;branch=master \
           file://disable-outside-tests.patch \
           file://disable-test-timeouts.patch \
           file://disable-float-tests.patch \
           file://950-fix.patch \
           file://system-brotli.patch \
           "

# tag 2.10.7
SRCREV= "c4cef129e880a3f9c23a480e8c983793963173bb"

S = "${WORKDIR}/git"

inherit cmake
