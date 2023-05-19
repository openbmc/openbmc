SUMMARY = "Cross-platform library for interfacing with rs-232 serial like ports"
HOMEPAGE = "http://wjwwood.io/serial/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;beginline=53;endline=62;md5=049c68d559533f90250404746e6a1045"

SRC_URI = " \
    git://github.com/wjwwood/${BPN}.git;protocol=https;branch=main \
    file://Findcatkin.cmake \
"
SRCREV = "10ac4e1c25c2cda1dc0a32a8e12b87fd89f3bb4f"
SRC_URI[sha256sum] = "c8cd235dda2ef7d977ba06dfcb35c35e42f45cfd9149ba3ad257756123d8ff96"

S = "${WORKDIR}/git"

inherit cmake

# Work-around for https://github.com/wjwwood/serial/issues/135
EXTRA_OECMAKE = " \
    -DCMAKE_MODULE_PATH=${WORKDIR} \
    -DCATKIN_PACKAGE_LIB_DESTINATION=${libdir} \
    -DCATKIN_PACKAGE_BIN_DESTINATION=${bindir} \
    -DCATKIN_GLOBAL_INCLUDE_DESTINATION=${includedir} \
    -DCATKIN_ENABLE_TESTING=OFF \
"

# Do not depend on the main package since it will be empty
RDEPENDS:${PN}-dev = ""

