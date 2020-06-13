SUMMARY = "C++ framework for implementing distributed and networked applications"
DESCRIPTION = "C++ network programming framework that implements many core \
patterns for concurrent communication software"
LICENSE = "ACE-TAO-CIAO"
HOMEPAGE = "http://www.dre.vanderbilt.edu/~schmidt/ACE.html"
LIC_FILES_CHKSUM = "file://COPYING;md5=96ef88a5529594698e8ceabdd47df92c"

DEPENDS += "openssl gperf-native"

SRC_URI = "https://github.com/DOCGroup/ACE_TAO/releases/download/ACE%2BTAO-6_5_8/ACE-${PV}.tar.bz2 \
           file://ace_config.patch \
          "

SRC_URI[md5sum] = "a6ba6a944612fe0696c90cbb5c3078ee"
SRC_URI[sha256sum] = "cda2a960dbb9970a907663627711b2e2b14b3484a2859ae936370bcad0b16923"

COMPATIBLE_HOST_libc-musl = "null"

S = "${WORKDIR}/ACE_wrappers"
B = "${WORKDIR}/ACE_wrappers/ace"
export ACE_ROOT="${WORKDIR}/ACE_wrappers"

inherit pkgconfig

CXXFLAGS_append = " -fpermissive -Wnodeprecated-declarations"

do_install() {
    export D="${D}"
    oe_runmake install

    for i in $(find ${D} -name "*.pc") ; do
        sed -i -e s:${D}::g \
               -e s:/${TARGET_SYS}::g \
                  $i
    done

    rm -r ${D}/usr/share
}

UPSTREAM_CHECK_URI = "https://github.com/DOCGroup/ACE_TAO/releases"
