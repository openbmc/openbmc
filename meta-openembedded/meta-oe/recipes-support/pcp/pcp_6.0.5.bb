require pcp.inc
#inherit perlnative

# NOTE: the following prog dependencies are unknown, ignoring: gtar gzip pkgmk xmlto lzma qshape md5sum pod2man publican git makedepend qmake-qt4 xconfirm true gmake xz dblatex hdiutil rpm bzip2 which mkinstallp dtrace seinfo qmake-qt5 gawk dlltool rpmbuild dpkg makepkg qmake echo
# NOTE: unable to map the following pkg-config dependencies: libmicrohttpd libsystemd-journal
#       (this is based on recipes that have previously been built and packaged)
# NOTE: the following library dependencies are unknown, ignoring: nspr gen ibumad regex sasl2 pfm nss papi ibmad
#       (this is based on recipes that have previously been built and packaged)
DEPENDS += "perl-native bison-native flex-native python3-native python3-setuptools python3 \
	pcp-native cairo zlib ncurses readline libx11 avahi openssl"


SRC_URI += "file://0001-Remove-unsuitble-part-for-cross-compile.patch \
            file://pass-options-to-AR.patch \
            file://fix_parallel_make.patch \
           "

export PCP_DIR="${RECIPE_SYSROOT_NATIVE}"
#export PCP_RUN_DIR="${RECIPE_SYSROOT_NATIVE}"
EXTRA_OEMAKE = "CC="${CC}" LD="${LD}""
inherit useradd systemd features_check python3targetconfig

# Needs libx11
REQUIRED_DISTRO_FEATURES = "x11"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"
SYSTEMD_SERVICE:${PN} = "\
                        pmie_farm_check.service \
                        pmie_farm.service \
                        pmfind.service \
                        pmlogger_farm_check.service \
                        pmcd.service \
                        pmie.service \
                        pmlogger_daily.service \
                        pmlogger.service \
                        pmlogger_farm.service \
                        pmie_check.service \
                        pmproxy.service \
                        pmlogger_check.service \
                        pmie_daily.service"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home ${localstatedir}/lib/pcp --no-create-home \
                       --user-group pcp"

USERADD_PACKAGES += "${PN}-testsuite"
USERADD_PARAM:${PN}-testsuite = "--system --home ${localstatedir}/lib/pcp/testsuite --no-create-home \
                       --user-group pcpqa"

RDEPENDS:${PN} += "perl"
RDEPENDS:${PN}-testsuite += "${PN} bash perl"
RDEPENDS:python3-${PN} += "${PN} python3"

do_configure:prepend () {
    cp ${WORKDIR}/config.linux ${B}
    rm -rf ${S}/include/pcp/configsz.h
    rm -rf ${S}/include/pcp/platformsz.h
    export SED=${TMPDIR}/hosttools/sed
    export PYTHON=python3
}

do_compile:prepend() {
	sed -i -e "s,#undef HAVE_64BIT_LONG,,g" \
		-e "s,#undef HAVE_64BIT_PTR,,g" \
		-e "s,#undef PM_SIZEOF_SUSECONDS_T,,g" \
		-e "s,#undef PM_SIZEOF_TIME_T,,g" \
		${S}/src/include/pcp/config.h.in
	sed -i -e "s,HAVE_PYTHON_ORDEREDDICT = false,HAVE_PYTHON_ORDEREDDICT = true,g" \
		${S}/src/include/builddefs
	sed -i -e "s,TOPDIR)/python3-pcp.list,TOPDIR)/python3-pcp.list --install-lib=${PYTHON_SITEPACKAGES_DIR},g" ${S}/src/python/GNUmakefile
	export PYTHON=python3
	#export PYTHON3=${STAGING_BINDIR_NATIVE}/python3-native/python3
}

do_compile() {
        oe_runmake default_pcp 
}

do_install () {
	export NO_CHOWN=true
	oe_runmake install DIST_ROOT=${D}\
	install_pcp

	rm -rf ${D}${localstatedir}/log
	rm -rf ${D}${localstatedir}/lib/pcp/pmcd
	rm -rf ${D}${localstatedir}/lib/pcp/tmp
	rm -rf ${D}${localstatedir}/run
	mv ${D}${docdir}/C* ${D}${docdir}/pcp-doc/
	mv ${D}${docdir}/I* ${D}${docdir}/pcp-doc/
	mv ${D}${docdir}/R* ${D}${docdir}/pcp-doc/
	mv ${D}${docdir}/V* ${D}${docdir}/pcp-doc/
	sed -i "s#PCP_AWK_PROG=.*#PCP_AWK_PROG=awk#" ${D}/${sysconfdir}/pcp.conf
	sed -i "s#PCP_SORT_PROG=.*#PCP_SORT_PROG=sort#" ${D}/${sysconfdir}/pcp.conf
	sed -i "s#PCP_ECHO_PROG=.*#PCP_ECHO_PROG=echo#" ${D}/${sysconfdir}/pcp.conf
	sed -i "s#PCP_WHICH_PROG=.*#PCP_WHICH_PROG=which#" ${D}/${sysconfdir}/pcp.conf
}

PACKAGES += " ${PN}-export-zabbix-agent ${PN}-testsuite \
	libpcp-gui2  libpcp-gui2-dev \
	libpcp-import1 libpcp-archive1 \
	libpcp-mmv1 libpcp-mmv1-dev \
	libpcp-pmda3 libpcp-pmda3-dev \
	libpcp-trace2 libpcp-trace2-dev \
	libpcp-web1 libpcp-web1-dev \
	libpcp3 libpcp3-dev python3-${PN}\
"
FILES:libpcp-gui2 = "${libdir}/libpcp_gui.so.2 \
"	
FILES:libpcp-archive1 = "${libdir}/libpcp_archive.so.1 \
"	
FILES:libpcp-gui2-dev = " \
	${libdir}/libpcp_gui.so \
	${libdir}/libpcp_gui.a \
	${includedir}/pmafm.h \
	${includedir}/pmtime.h \
"
FILES:libpcp-mmv1 = " \
	${libdir}/libpcp_mmv.so.1 \
"
FILES:libpcp-mmv1-dev = " \
	${libdir}/libpcp_mmv.a \
	${libdir}/libpcp_mmv.so \
	${libdir}/libpcp_mmv.so \
	${includedir}/mmv_stats.h \
	${includedir}/mmv_dev.h \
	${datadir}/man/man3/mmv_* \
	${datadir}/man/man5/mmv.5.gz \
"
FILES:libpcp-import1 = " \
	${libdir}/libpcp_import.so.1 \
"
FILES:libpcp-pmda3 = " \
	${libdir}/libpcp_pmda.so.3 \
"
FILES:libpcp-pmda3-dev = " \
	${includedir}/pmda.h \
	${includedir}/pmdaroot.h \
	${libdir}/libpcp_pmda.a \
	${libdir}/libpcp_pmda.so \
	${libdir}/pkgconfig/libpcp_pmda.pc \
	${datadir}/man/man3/PMDA.3.gz \
	${datadir}/man/man3/pmda* \
"
FILES:libpcp-trace2 = " \
	${libdir}/libpcp_trace.so.2 \
"
FILES:libpcp-trace2-dev = " \
	${includedir}/trace.h \
	${includedir}/trace_dev.h \
	${libdir}/libpcp_trace.a \
	${libdir}/libpcp_trace.so \
	${datadir}/man/man3/pmtrace* \
"
FILES:libpcp-web1 = " \
	${libdir}/libpcp_web.so.1 \
"
FILES:libpcp-web1-dev = " \
	${includedir}/pmhttp.h \
	${includedir}/pmjson.h \
	${libdir}/libpcp_web.a \
	${libdir}/libpcp_web.so \
	${datadir}/man/man3/pmhttp* \
	${datadir}/man/man3/pmjson* \
"
FILES:libpcp3 = " \
	${libdir}/libpcp.so.3 \
"

FILES:${PN} = " \
	${sysconfdir}/pcp \
	${sysconfdir}/cron.d \
	${sysconfdir}/init.d \
	${libexecdir} \
	${bindir} \
	${datadir}/bash-completion \
	${datadir}/pcp-gui \
	${datadir}/zsh \
	${systemd_system_unitdir}/ \
	${libdir}/pcp/ \
	${libdir}/sysusers.d/pcp.conf \
	${datadir}/pcp \
	${libdir}/*.sh \
	${datadir}/man \
	${libdir}/rc-proc.sh.minimal \
	${sysconfdir}/p* \
	${sysconfdir}/s* \
	${localstatedir}/lib/pcp/config \
	${localstatedir}/lib/pcp/pmdas/ \
	${localstatedir}/lib/pcp/pmns \
	${libdir}/libpcp_fault.so.3 \
"

FILES:${PN}-export-zabbix-agent += " \
	${libdir}/zabbix \
	${sysconfdir}/zabbix \
	${mandir}/man3/zbxpcp.3.gz \
	${libdir}/zabbix \
"
FILES:${PN}-testsuite = "${localstatedir}/lib/pcp/testsuite/ ${libdir}/sysusers.d/pcp-testsuite.conf"
FILES:python3-${PN} = "${PYTHON_SITEPACKAGES_DIR}"
FILES:${PN}-dev += " \
        ${includedir}/pcp \
        ${libdir}/libpcp.a \
        ${libdir}/libpcp.so \
        ${localstatedir}/lib/pcp/pmdas/*/*.so \
        ${libexecdir}/pcp/bin/install-sh \
        ${libdir}/pkgconfig/libpcp.pc \
        ${libdir}/zabbix/modules/*.so \
        ${datadir}/man/man3/LOGIMPORT.3.gz \
        ${datadir}/man/man3/P* \
        ${datadir}/man/man3/Q* \
        ${datadir}/man/man3/__pm* \
        ${datadir}/man/man3/pmA* \
        ${datadir}/man/man3/pmC* \
        ${datadir}/man/man3/pmD* \
        ${datadir}/man/man3/pmE* \
        ${datadir}/man/man3/pmF* \
        ${datadir}/man/man3/pmG* \
        ${datadir}/man/man3/pmH* \
        ${datadir}/man/man3/pmI* \
        ${datadir}/man/man3/pmL* \
        ${datadir}/man/man3/pmM* \
        ${datadir}/man/man3/pmN* \
        ${datadir}/man/man3/pmO* \
        ${datadir}/man/man3/pmP* \
        ${datadir}/man/man3/pmR* \
        ${datadir}/man/man3/pmS* \
        ${datadir}/man/man3/pmT* \
        ${datadir}/man/man3/pmU* \
        ${datadir}/man/man3/pmW* \
        ${datadir}/man/man3/pmf* \
        ${datadir}/man/man3/pmg* \
        ${datadir}/man/man3/pmi* \
        ${datadir}/man/man3/pms* \
        ${datadir}/man/man3/pmt* \
"
