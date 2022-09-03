SUMMARY = "Security tool that is a wrapper for TCP daemons"
HOMEPAGE = "http://www.softpanorama.org/Net/Network_security/TCP_wrappers/"
DESCRIPTION = "Tools for monitoring and filtering incoming requests for tcp \
               services."
SECTION = "console/network"

LICENSE = "BSD-1-Clause"
LIC_FILES_CHKSUM = "file://DISCLAIMER;md5=071bd69cb78b18888ea5e3da5c3127fa"
PR ="r10"

DEPENDS += "libnsl2"

PACKAGES = "${PN}-dbg libwrap libwrap-doc libwrap-dev libwrap-staticdev ${PN} ${PN}-doc"
FILES:libwrap = "${base_libdir}/lib*${SOLIBS}"
FILES:libwrap-doc = "${mandir}/man3 ${mandir}/man5"
FILES:libwrap-dev = "${libdir}/lib*${SOLIBSDEV} ${includedir}"
FILES:libwrap-staticdev = "${libdir}/lib*.a"
FILES:${PN} = "${sbindir}"
FILES:${PN}-doc = "${mandir}/man8"

SRC_URI = "http://ftp.porcupine.org/pub/security/tcp_wrappers_${PV}.tar.gz \
           file://00_man_quoting.diff \
           file://01_man_portability.patch \
           file://05_wildcard_matching.patch \
           file://06_fix_gethostbyname.patch \
           file://10_usagi-ipv6.patch \
           file://11_tcpd_blacklist.patch \
           file://11_usagi_fix.patch \
           file://12_makefile_config.patch \
           file://13_shlib_weaksym.patch \
           file://14_cidr_support.patch \
           file://15_match_clarify.patch \
           file://expand_remote_port.patch \
           file://have_strerror.patch \
           file://man_fromhost.patch \
           file://restore_sigalarm.patch \
           file://rfc931.diff \
           file://safe_finger.patch \
           file://sig_fix.patch \
           file://siglongjmp.patch \
           file://socklen_t.patch \
           file://tcpdchk_libwrapped.patch \
           file://ldflags.patch \
           file://rename_strings_variable.patch \
           file://try-from.8 \
           file://safe_finger.8 \
           file://makefile-fix-parallel.patch \
           file://musl-decls.patch \
           file://0001-Fix-build-with-clang.patch \
           file://fix_warnings.patch \
           file://fix_warnings2.patch \
           file://0001-Remove-fgets-extern-declaration.patch \
           file://0001-Fix-implicit-function-declaration-warnings.patch \
           "

SRC_URI[md5sum] = "e6fa25f71226d090f34de3f6b122fb5a"
SRC_URI[sha256sum] = "9543d7adedf78a6de0b221ccbbd1952e08b5138717f4ade814039bb489a4315d"

S = "${WORKDIR}/tcp_wrappers_${PV}"

EXTRA_OEMAKE = "'CC=${CC}' \
                'AR=${AR}' \
                'RANLIB=${RANLIB}' \
                'REAL_DAEMON_DIR=${sbindir}' \
                'STYLE=-DPROCESS_OPTIONS' \
                'FACILITY=LOG_DAEMON' \
                'SEVERITY=LOG_INFO' \
                'BUGS=' \
                'VSYSLOG=' \
                'RFC931_TIMEOUT=10' \
                'ACCESS=-DHOSTS_ACCESS' \
                'KILL_OPT=-DKILL_IP_OPTIONS' \
                'UMASK=-DDAEMON_UMASK=022' \
                'NETGROUP=${EXTRA_OEMAKE_NETGROUP}' \
                'ARFLAGS=rv' \
                'AUX_OBJ=weak_symbols.o' \
                'TLI=' \
                'COPTS=' \
                'EXTRA_CFLAGS=${CFLAGS} -DSYS_ERRLIST_DEFINED -DHAVE_STRERROR -DHAVE_WEAKSYMS -D_REENTRANT -DINET6=1 -Dss_family=__ss_family -Dss_len=__ss_len'"

EXTRA_OEMAKE_NETGROUP = "-DNETGROUP -DUSE_GETDOMAIN"
EXTRA_OEMAKE_NETGROUP:libc-musl = "-DUSE_GETDOMAIN"

EXTRA_OEMAKE:append:libc-musl = " 'LIBS='"

do_compile () {
	oe_runmake 'TABLES=-DHOSTS_DENY=\"${sysconfdir}/hosts.deny\" -DHOSTS_ALLOW=\"${sysconfdir}/hosts.allow\"' \
		   all
}

BINS = "safe_finger tcpd tcpdchk try-from tcpdmatch"
MANS3 = "hosts_access"
MANS5 = "hosts_options"
MANS8 = "tcpd tcpdchk tcpdmatch"
do_install () {
	oe_libinstall -a libwrap ${D}${libdir}
	oe_libinstall -C shared -so libwrap ${D}${base_libdir}

	if [ "${libdir}" != "${base_libdir}" ] ; then
		rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
		libname=`readlink ${D}${base_libdir}/libwrap.so | xargs basename`
		ln -s ${rel_lib_prefix}${base_libdir}/${libname} ${D}${libdir}/libwrap.so
		rm -f ${D}${base_libdir}/libwrap.so
	fi

	install -d ${D}${sbindir}
	for b in ${BINS}; do
		install -m 0755 $b ${D}${sbindir}/ || exit 1
	done

	install -d ${D}${mandir}/man3
	for m in ${MANS3}; do
		install -m 0644 $m.3 ${D}${mandir}/man3/ || exit 1
	done

	install -d ${D}${mandir}/man5
	for m in ${MANS5}; do
		install -m 0644 $m.5 ${D}${mandir}/man5/ || exit 1
	done

	install -d ${D}${mandir}/man8
	for m in ${MANS8}; do
		install -m 0644 $m.8 ${D}${mandir}/man8/ || exit 1
	done

	install -m 0644 ${WORKDIR}/try-from.8 ${D}${mandir}/man8/
	install -m 0644 ${WORKDIR}/safe_finger.8 ${D}${mandir}/man8/

	install -d ${D}${includedir}
	install -m 0644 tcpd.h ${D}${includedir}/

	install -d ${D}${sysconfdir}
	touch ${D}${sysconfdir}/hosts.allow
	touch ${D}${sysconfdir}/hosts.deny
}

FILES:${PN} += "${sysconfdir}/hosts.allow ${sysconfdir}/hosts.deny"
