SUMMARY = "Linux DVB API applications and utilities"
HOMEPAGE = "http://www.linuxtv.org"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://www.linuxtv.org/hg/dvb-apps/archive/3d43b280298c.tar.bz2;downloadfilename=${BPN}-3d43b280298c.tar.bz2 \
          file://dvb-scan-table \
          file://0001-Fix-generate-keynames.patch \
          file://0003-handle-static-shared-only-build.patch \
          file://0004-Makefile-remove-test.patch \
          file://0005-libucsi-optimization-removal.patch \
          file://0006-CA_SET_PID.patch \
          file://0001-dvbdate-Remove-Obsoleted-stime-API-calls.patch \
          "
SRC_URI[sha256sum] = "f39e2f0ebed7e32bce83522062ad4d414f67fccd5df1b647618524497e15e057"
S = "${WORKDIR}/${BPN}-3d43b280298c"

inherit perlnative

export enable_static="no"

export PERL_USE_UNSAFE_INC = "1"

do_configure() {
    sed -i -e s:/usr/include:${STAGING_INCDIR}:g util/av7110_loadkeys/generate-keynames.sh
}
do_install() {
    make DESTDIR=${D} install
    install -d ${D}/${bindir}
    install -d ${D}/${docdir}/dvb-apps
    install -d ${D}/${docdir}/dvb-apps/scan
    install -d ${D}/${docdir}/dvb-apps/szap
    chmod a+rx ${D}/${libdir}/*.so*
    cp -R --no-dereference --preserve=mode,links ${S}/util/szap/channels-conf* ${D}/${docdir}/dvb-apps/szap/
    cp -R --no-dereference --preserve=mode,links ${S}/util/szap/README   ${D}/${docdir}/dvb-apps/szap/
    cp -R --no-dereference --preserve=mode,links ${WORKDIR}/dvb-scan-table/* ${D}/usr/share/dvb
}

PACKAGES =+ "dvb-evtest dvb-evtest-dbg \
             dvbapp-tests dvbapp-tests-dbg \
             dvbdate dvbdate-dbg \
             dvbtraffic dvbtraffic-dbg \
             dvbnet dvbnet-dbg \
             dvb-scan dvb-scan-dbg dvb-scan-data \
             dvb-azap dvb-azap-dbg \
             dvb-czap dvb-czap-dbg \
             dvb-szap dvb-szap-dbg \
             dvb-tzap dvb-tzap-dbg \
             dvb-femon dvb-femon-dbg \
             dvb-zap-data"
PACKAGES =+ "libdvbapi libdvbcfg libdvben50221 \
            libesg libucsi libdvbsec"

RDEPENDS:dvbdate =+ "libdvbapi libucsi"
RDEPENDS:dvbtraffic =+ "libdvbapi"
RDEPENDS:dvb-scan =+ "libdvbapi libdvbcfg libdvbsec"
RDEPENDS:dvb-apps =+ "libdvbapi libdvbcfg libdvbsec libdvben50221 libucsi"
RDEPENDS:dvb-femon =+ "libdvbapi"
RDEPENDS:dvbnet =+ "libdvbapi"

RCONFLICTS:dvb-evtest = "evtest"

FILES:${PN} = "${bindir} ${datadir}/dvb"
FILES:${PN}-doc = ""
FILES:${PN}-dev = "${includedir}"
FILES:dvb-evtest = "${bindir}/evtest"
FILES:dvb-evtest-dbg = "${bindir}/.debug/evtest"
FILES:dvbapp-tests = "${bindir}/*test* "
FILES:dvbapp-tests-dbg = "${bindir}/.debug/*test*"
FILES:dvbdate = "${bindir}/dvbdate"
FILES:dvbdate-dbg = "${bindir}/.debug/dvbdate"
FILES:dvbtraffic = "${bindir}/dvbtraffic"
FILES:dvbtraffic-dbg = "${bindir}/.debug/dvbtraffic"
FILES:dvbnet = "${bindir}/dvbnet"
FILES:dvbnet-dbg = "${bindir}/.debug/dvbnet"
FILES:dvb-scan = "${bindir}/*scan "
FILES:dvb-scan-dbg = "${bindir}/.debug/*scan"
FILES:dvb-scan-data = "${docdir}/dvb-apps/scan"
FILES:dvb-azap = "${bindir}/azap"
FILES:dvb-azap-dbg = "${bindir}/.debug/azap"
FILES:dvb-czap = "${bindir}/czap"
FILES:dvb-czap-dbg = "${bindir}/.debug/czap"
FILES:dvb-szap = "${bindir}/szap"
FILES:dvb-szap-dbg = "${bindir}/.debug/szap"
FILES:dvb-tzap = "${bindir}/tzap"
FILES:dvb-tzap-dbg = "${bindir}/.debug/tzap"
FILES:dvb-femon = "${bindir}/femon"
FILES:dvb-femon-dbg = "${bindir}/.debug/femon"
FILES:dvb-zap-data = "${docdir}/dvb-apps/szap"

python populate_packages:prepend () {
    dvb_libdir = bb.data.expand('${libdir}', d)
    do_split_packages(d, dvb_libdir, r'^lib(.*)\.so$', 'lib%s', 'DVB %s package', extra_depends='', allow_links=True)
    do_split_packages(d, dvb_libdir, r'^lib(.*)\.la$', 'lib%s-dev', 'DVB %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, dvb_libdir, r'^lib(.*)\.a$', 'lib%s-dev', 'DVB %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, dvb_libdir, r'^lib(.*)\.so\.*', 'lib%s', 'DVB %s library', extra_depends='', allow_links=True)
}

TARGET_CC_ARCH += "${LDFLAGS}"
