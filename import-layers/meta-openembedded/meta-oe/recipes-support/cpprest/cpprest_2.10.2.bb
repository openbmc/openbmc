SUMMARY = "Microsoft project for cloud-based client-server communication in native code using a modern asynchronous C++ API design."
SECTION = "libs/network"
HOMEPAGE = "https://github.com/Microsoft/cpprestsdk/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/../license.txt;md5=a2e15b954769218ff912468eecd6a02f"
DEPENDS = "openssl websocketpp zlib boost"

SRC_URI = "git://github.com/Microsoft/cpprestsdk.git;protocol=https;branch=master"

SRC_URI += "file://fix-cmake-install.patch"

# tag 2.10.2
SRCREV= "fea848e2a77563cf2a6f28f8eab396fd6e787fbf"

S = "${WORKDIR}/git/Release"

inherit cmake
