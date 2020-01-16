SUMMARY = "Linux DVB API applications and utilities"
HOMEPAGE = "http://www.linuxtv.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRCREV = "3d43b280298c39a67d1d889e01e173f52c12da35"

SRC_URI = "hg://linuxtv.org/hg;module=dvb-apps;protocol=http \
          file://dvb-scan-table \
          file://0001-Fix-generate-keynames.patch \
          file://0003-handle-static-shared-only-build.patch \
          file://0004-Makefile-remove-test.patch \
          file://0005-libucsi-optimization-removal.patch \
          file://0006-CA_SET_PID.patch \
          file://0001-dvbdate-Remove-Obsoleted-stime-API-calls.patch \
          "

S = "${WORKDIR}/${BPN}"

inherit perlnative

export enable_static="no"

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

RDEPENDS_dvbdate =+ "libdvbapi libucsi"
RDEPENDS_dvbtraffic =+ "libdvbapi"
RDEPENDS_dvb-scan =+ "libdvbapi libdvbcfg libdvbsec"
RDEPENDS_dvb-apps =+ "libdvbapi libdvbcfg libdvbsec libdvben50221 libucsi"
RDEPENDS_dvb-femon =+ "libdvbapi"
RDEPENDS_dvbnet =+ "libdvbapi"

RCONFLICTS_dvb-evtest = "evtest"

FILES_${PN} = "${bindir} ${datadir}/dvb"
FILES_${PN}-doc = ""
FILES_${PN}-dev = "${includedir}"
FILES_dvb-evtest = "${bindir}/evtest"
FILES_dvb-evtest-dbg = "${bindir}/.debug/evtest"
FILES_dvbapp-tests = "${bindir}/*test* "
FILES_dvbapp-tests-dbg = "${bindir}/.debug/*test*"
FILES_dvbdate = "${bindir}/dvbdate"
FILES_dvbdate-dbg = "${bindir}/.debug/dvbdate"
FILES_dvbtraffic = "${bindir}/dvbtraffic"
FILES_dvbtraffic-dbg = "${bindir}/.debug/dvbtraffic"
FILES_dvbnet = "${bindir}/dvbnet"
FILES_dvbnet-dbg = "${bindir}/.debug/dvbnet"
FILES_dvb-scan = "${bindir}/*scan "
FILES_dvb-scan-dbg = "${bindir}/.debug/*scan"
FILES_dvb-scan-data = "${docdir}/dvb-apps/scan"
FILES_dvb-azap = "${bindir}/azap"
FILES_dvb-azap-dbg = "${bindir}/.debug/azap"
FILES_dvb-czap = "${bindir}/czap"
FILES_dvb-czap-dbg = "${bindir}/.debug/czap"
FILES_dvb-szap = "${bindir}/szap"
FILES_dvb-szap-dbg = "${bindir}/.debug/szap"
FILES_dvb-tzap = "${bindir}/tzap"
FILES_dvb-tzap-dbg = "${bindir}/.debug/tzap"
FILES_dvb-femon = "${bindir}/femon"
FILES_dvb-femon-dbg = "${bindir}/.debug/femon"
FILES_dvb-zap-data = "${docdir}/dvb-apps/szap"

python populate_packages_prepend () {
    dvb_libdir = bb.data.expand('${libdir}', d)
    do_split_packages(d, dvb_libdir, '^lib(.*)\.so$', 'lib%s', 'DVB %s package', extra_depends='', allow_links=True)
    do_split_packages(d, dvb_libdir, '^lib(.*)\.la$', 'lib%s-dev', 'DVB %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, dvb_libdir, '^lib(.*)\.a$', 'lib%s-dev', 'DVB %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, dvb_libdir, '^lib(.*)\.so\.*', 'lib%s', 'DVB %s library', extra_depends='', allow_links=True)
}

INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-dev = "ldflags"

TARGET_CC_ARCH += "${LDFLAGS}"
