do_install() {
	oe_runmake install_root=${D} install
	for r in ${rpcsvc}; do
		h=`echo $r|sed -e's,\.x$,.h,'`
		install -m 0644 ${S}/sunrpc/rpcsvc/$h ${D}/${includedir}/rpcsvc/
	done
	install -d ${D}/${sysconfdir}/ 
	install -m 0644 ${WORKDIR}/etc/ld.so.conf ${D}/${sysconfdir}/
	install -d ${D}${localedir}
	make -f ${WORKDIR}/generate-supported.mk IN="${S}/localedata/SUPPORTED" OUT="${WORKDIR}/SUPPORTED"
	# get rid of some broken files...
	for i in ${GLIBC_BROKEN_LOCALES}; do
		grep -v $i ${WORKDIR}/SUPPORTED > ${WORKDIR}/SUPPORTED.tmp
		mv ${WORKDIR}/SUPPORTED.tmp ${WORKDIR}/SUPPORTED
	done
	rm -f ${D}${sysconfdir}/rpc
	rm -rf ${D}${datadir}/zoneinfo
	rm -rf ${D}${libexecdir}/getconf
}

def get_libc_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU', True) in [ 'soft', 'ppc-efd' ]:
        return "--without-fp"
    return ""

python populate_packages_prepend () {
    if d.getVar('DEBIAN_NAMES', True):
        pkgs = d.getVar('PACKAGES', True).split()
        bpn = d.getVar('BPN', True)
        prefix = d.getVar('MLPREFIX', True) or ""
        # Set the base package...
        d.setVar('PKG_' + prefix + bpn, prefix + 'libc6')
        libcprefix = prefix + bpn + '-'
        for p in pkgs:
            # And all the subpackages.
            if p.startswith(libcprefix):
                renamed = p.replace(bpn, 'libc6', 1)
                d.setVar('PKG_' + p, renamed)
        # For backward compatibility with old -dbg package
        d.appendVar('RPROVIDES_' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
        d.appendVar('RCONFLICTS_' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
        d.appendVar('RREPLACES_' + libcprefix + 'dbg', ' ' + prefix + 'libc-dbg')
}
