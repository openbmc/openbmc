SUMMARY = "ACCEL-PPP is a high performance VPN server application for linux"
HOMEPAGE = "http://sourceforge.net/apps/trac/accel-ppp/wiki"
SECTION = "net"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

DEPENDS = "openssl libpcre"

inherit cmake

PR = "r1"
PV = "1.7.3+git"

SRCREV = "4acfa46c321a344b9a6ce4128e72d1e02828d8a0"
SRC_URI = "git://accel-ppp.git.sourceforge.net/gitroot/accel-ppp/accel-ppp;branch=1.7"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = " \
                 -DBUILD_DRIVER=FALSE \
                 -DCMAKE_INSTALL_PREFIX=${prefix} \
                 -DCMAKE_BUILD_TYPE=Release \
                 -DLOG_PGSQL=FALSE \
                 -DRADIUS=FALSE \
                 -DNETSNMP=FALSE \
"
FILES_${PN}-dbg += "/usr/lib/${BPN}/.debug/*"

PACKAGES =+ "${PN}-libs"
FILES_${PN}-libs = "${libdir}/${BPN}/*.so /usr/lib/${BPN}/*.so"
INSANE_SKIP_${PN}-libs = "dev-so"
RDEPENDS_${PN} += "${PN}-libs"

do_install_prepend() {
    cmlist=`find ${S} -name CMakeLists.txt`
    for file in $cmlist; do
        sed -i -e "s:LIBRARY DESTINATION lib/accel-ppp:LIBRARY DESTINATION ${baselib}/accel-ppp:g" \
               -e "s:\${CMAKE_INSTALL_PREFIX}/lib/accel-ppp:\${CMAKE_INSTALL_PREFIX}/${baselib}/accel-ppp:g" \
               $cmlist
    done
}
