SUMMARY = "Microsoft project for cloud-based client-server communication in native code using a modern asynchronous C++ API design."
SECTION = "libs/network"
HOMEPAGE = "https://github.com/Microsoft/cpprestsdk/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/license.txt;md5=a2e15b954769218ff912468eecd6a02f"
DEPENDS = "openssl websocketpp zlib boost brotli"

EXTRA_OECMAKE = "-DCPPREST_EXPORT_DIR=cmake -DCPPREST_EXCLUDE_BROTLI=OFF"

SRC_URI = "git://github.com/Microsoft/cpprestsdk.git;protocol=https;branch=master \
           file://revert-9b670e5b33dfdbd501d618cd7e7498148ffbd559.patch \
           file://revert-f10d9f8e214516d2c19aa6ef831ee874a58c0479.patch \
           "

# tag 2.10.12
SRCREV= "d4fb1cf7f7d22c12e2e442ba5a5e98d09b0a28ab"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
