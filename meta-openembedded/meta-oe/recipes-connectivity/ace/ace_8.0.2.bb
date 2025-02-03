SUMMARY = "C++ framework for implementing distributed and networked applications"
DESCRIPTION = "C++ network programming framework that implements many core \
patterns for concurrent communication software"
LICENSE = "ACE-TAO-CIAO"
HOMEPAGE = "http://www.dre.vanderbilt.edu/~schmidt/ACE.html"
LIC_FILES_CHKSUM = "file://COPYING;md5=d2c090e9c730fd91677782d8e2091d77"

DEPENDS += "openssl gperf-native"

SRC_URI = "https://github.com/DOCGroup/ACE_TAO/releases/download/ACE%2BTAO-8_0_2/ACE-${PV}.tar.bz2 \
           file://ace_config.patch \
           file://no_sysctl.patch \
          "
SRC_URI[sha256sum] = "dba38a905858ec4a44c04b4bbaef42b891adf061e8c0bbdaa1dce2c04fcccb7f"

UPSTREAM_CHECK_URI = "https://github.com/DOCGroup/ACE_TAO/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

CVE_STATUS[CVE-2009-1147] = "cpe-incorrect: this CVE is for vmware ace"

COMPATIBLE_HOST:libc-musl = "null"

S = "${WORKDIR}/ACE_wrappers"
B = "${WORKDIR}/ACE_wrappers/ace"
export ACE_ROOT="${WORKDIR}/ACE_wrappers"

inherit pkgconfig

CXXFLAGS:append = " -fpermissive -Wnodeprecated-declarations"
CXX:append = " -ffile-prefix-map=${WORKDIR}= -fdebug-prefix-map=${WORKDIR}= "
EXTRA_OEMAKE += "INSTALL_LIB=${baselib} install_rpath=0"

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
