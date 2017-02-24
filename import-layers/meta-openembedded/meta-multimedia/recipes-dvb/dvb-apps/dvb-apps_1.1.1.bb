HOMEPAGE = "http://www.linuxtv.org"
SUMMARY = "Linux DVB API applications and utilities"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "v4l-utils-native"

SRC_URI = " \
            hg://linuxtv.org/hg;module=dvb-apps;protocol=http;name=apps \
            git://linuxtv.org/git/dtv-scan-tables.git;protocol=http;destsuffix=dvb-apps/initial-scan-tables;name=scantables \
          "
SRCREV_apps = "3d43b280298c"
SRCREV_scantables = "ceb11833b35f05813b1f0397a60e0f3b99430aab"
SRCREV_FORMAT = "apps_scantables"

S = "${WORKDIR}/${BPN}"

TARGET_CC_ARCH += "${LDFLAGS}"
EXTRA_OEMAKE = "VERSION_FILE='${STAGING_INCDIR}/linux/dvb/version.h'"

do_configure() {
    sed -i -e s:/usr/include:${STAGING_INCDIR}:g util/av7110_loadkeys/generate-keynames.sh
}

do_compile_append() {
# dvb-apps only support DVBAPI v3, so generate them from the DVBAPI v5 downloaded files
    make -C initial-scan-tables clean
    make -C initial-scan-tables dvbv3
}

do_install() {
    make DESTDIR=${D} install
# dvb-apps only support DVBAPI v3, so only install the generated DVBAPI v3 files
    make -C initial-scan-tables DATADIR=${D}/${datadir} DVBV3DIR=dvb install_v3

    install -d ${D}/${bindir}
    install -d ${D}/${docdir}/dvb-apps
    install -d ${D}/${docdir}/dvb-apps/szap
    chmod a+rx ${D}/${libdir}/*.so*

    # Install tests
    install -m 0755 ${S}/test/setvoltage      ${D}${bindir}/test_setvoltage
    install -m 0755 ${S}/test/set22k          ${D}${bindir}/test_set22k
    install -m 0755 ${S}/test/sendburst       ${D}${bindir}/test_sendburst
    install -m 0755 ${S}/test/diseqc          ${D}${bindir}/test_diseqc
    install -m 0755 ${S}/test/test_sections   ${D}${bindir}/
    install -m 0755 ${S}/test/test_av_play    ${D}${bindir}/
    install -m 0755 ${S}/test/test_stillimage ${D}${bindir}/
    install -m 0755 ${S}/test/test_dvr_play   ${D}${bindir}/
    install -m 0755 ${S}/test/test_tt         ${D}${bindir}/
    install -m 0755 ${S}/test/test_sec_ne     ${D}${bindir}/
    install -m 0755 ${S}/test/test_stc        ${D}${bindir}/
    install -m 0755 ${S}/test/test_av         ${D}${bindir}/
    install -m 0755 ${S}/test/test_vevent     ${D}${bindir}/
    install -m 0755 ${S}/test/test_pes        ${D}${bindir}/
    install -m 0755 ${S}/test/test_dvr        ${D}${bindir}/

    cp -pPR ${S}/util/szap/channels-conf* ${D}/${docdir}/dvb-apps/szap/
    cp -pPR ${S}/util/szap/README   ${D}/${docdir}/dvb-apps/szap/
}

python populate_packages_prepend () {
    dvb_libdir = bb.data.expand('${libdir}', d)
    do_split_packages(d, dvb_libdir, '^lib(.*)\.so$', 'lib%s', 'DVB %s package', extra_depends='', allow_links=True)
    do_split_packages(d, dvb_libdir, '^lib(.*)\.la$', 'lib%s-dev', 'DVB %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, dvb_libdir, '^lib(.*)\.a$', 'lib%s-dev', 'DVB %s development package', extra_depends='${PN}-dev')
    do_split_packages(d, dvb_libdir, '^lib(.*)\.so\.*', 'lib%s', 'DVB %s library', extra_depends='', allow_links=True)
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

# Expose the packages from the above spitting
PACKAGES =+ "libdvbapi libdvbcfg libdvben50221 \
             libesg libucsi libdvbsec"

FILES_${PN} = "${bindir} ${datadir}/dvb"
FILES_${PN}-doc = ""
FILES_${PN}-dev = "${includedir}"

FILES_dvb-evtest = "${bindir}/evtest"
FILES_dvb-evtest-dbg = "${bindir}/.debug/evtest"
RCONFLICTS_dvb-evtest = "evtest"

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
FILES_dvb-scan-data = "${datadir}/dvb"

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

RDEPENDS_dvbdate =+ "libdvbapi libucsi"
RDEPENDS_dvbtraffic =+ "libdvbapi"
RDEPENDS_dvb-scan =+ "libdvbapi libdvbcfg libdvbsec"
RDEPENDS_dvb-apps =+ "libdvbapi libdvbcfg libdvbsec libdvben50221 libucsi"
RDEPENDS_dvb-femon =+ "libdvbapi"
RDEPENDS_dvbnet =+ "libdvbapi"
