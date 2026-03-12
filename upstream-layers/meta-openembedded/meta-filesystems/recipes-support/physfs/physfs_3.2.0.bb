SUMMARY = "PhysicsFS is a library to provide abstract access to various archives"
HOMEPAGE = "http://icculus.org/physfs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e64c08153665a18b56559d0683a64909"
DEPENDS = "readline zlib"

inherit cmake

PE = "1"
PV .= "+git"

SRC_URI = "git://github.com/icculus/physfs.git;protocol=https;branch=main"
SRCREV = "d70c3fcf06814f8608c8327d3e8136063ee0133d"

do_install:append(){
    sed -i 's|${RECIPE_SYSROOT}|\$\{CMAKE_SYSROOT\}|g' ${D}${libdir}/cmake/PhysFS/PhysFS-static-targets.cmake
}

FILES:${PN} += "${datadir}/licenses/PhysicsFS3"
