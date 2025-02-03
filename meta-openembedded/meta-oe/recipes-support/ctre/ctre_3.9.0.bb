DESCRIPTION = "Fast compile-time regular expressions with support for matching/searching/capturing."
HOMEPAGE = "https://github.com/hanickadot/compile-time-regular-expressions"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e982d844baa4df1c80de75470e0c5cb"

SRC_URI = "git://github.com/hanickadot/compile-time-regular-expressions.git;protocol=https;branch=main"
SRCREV = "eb9577aae3515d14e6c5564f9aeb046d2e7c1124"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= ""
PACKAGECONFIG[module] = "-DCTRE_MODULE=ON,-DCTRE_MODULE=OFF"
PACKAGECONFIG[tests] = "-DCTRE_BUILD_TESTS=ON,-DCTRE_BUILD_TESTS=OFF"

EXTRA_OECMAKE:append = " \
-DCTRE_BUILD_PACKAGE=OFF \
-DCTRE_BUILD_PACKAGE_DEB=OFF \
-DCTRE_BUILD_PACKAGE_RPM=OFF \
"
