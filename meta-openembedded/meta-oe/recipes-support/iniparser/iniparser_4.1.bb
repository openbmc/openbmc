SUMMARY = "The iniParser library is a simple C library offering INI file parsing services (both reading and writing)."
SECTION = "libs"
HOMEPAGE = "https://github.com/ndevilla/iniparser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e02baf71c76e0650e667d7da133379ac"

DEPENDS = "doxygen-native"

PV .= "+git"

SRC_URI = "git://github.com/ndevilla/iniparser.git;protocol=https;branch=master \
           file://0001-iniparser.pc-Make-libpath-a-variable.patch \
	   file://Add-CMake-support.patch \
           file://CVE-2023-33461.patch \
"

SRCREV= "deb85ad4936d4ca32cc2260ce43323d47936410d"

S = "${WORKDIR}/git"

inherit cmake

do_install:append() {
    install -Dm 0644 ${S}/iniparser.pc ${D}${libdir}/pkgconfig/iniparser.pc
    sed -i -e 's,@baselib@,${baselib},g' ${D}${libdir}/pkgconfig/iniparser.pc
}

BBCLASSEXTEND += "native"
