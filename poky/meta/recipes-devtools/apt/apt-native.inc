require apt.inc
inherit native

DEPENDS += "dpkg-native gettext-native db-native curl-native xz-native"
PACKAGES = ""
USE_NLS = "yes"

SRC_URI += "file://db_linking_hack.patch \
            file://0001-Revert-always-run-dpkg-configure-a-at-the-end-of-our.patch \
"

python do_install () {
    bb.build.exec_func('do_install_base', d)
    bb.build.exec_func('do_install_config', d)
}

python do_install_config () {
    indir = os.path.dirname(d.getVar('FILE'))
    infile = open(oe.path.join(indir, 'files', 'apt.conf'), 'r')
    data = infile.read()
    infile.close()

    data = d.expand(data)

    outdir = oe.path.join(d.getVar('D'), d.getVar('sysconfdir'), 'apt')
    if not os.path.exists(outdir):
        os.makedirs(outdir)

    outpath = oe.path.join(outdir, 'apt.conf.sample')
    if not os.path.exists(outpath):
        outfile = open(outpath, 'w')
        outfile.write(data)
        outfile.close()
}

do_install_base () {
	install -d ${D}${bindir}
	install -m 0755 bin/apt-cdrom ${D}${bindir}/
	install -m 0755 bin/apt-get ${D}${bindir}/
	install -m 0755 bin/apt-config ${D}${bindir}/
	install -m 0755 bin/apt-cache ${D}${bindir}/
	install -m 0755 bin/apt-sortpkgs ${D}${bindir}/
	install -m 0755 bin/apt-extracttemplates ${D}${bindir}/
	install -m 0755 bin/apt-ftparchive ${D}${bindir}/

	oe_libinstall -so -C bin libapt-private ${D}${libdir}/

	oe_libinstall -so -C bin libapt-pkg$GLIBC_VER$LIBSTDCPP_VER ${D}${libdir}/
	oe_libinstall -so -C bin libapt-inst$GLIBC_VER$LIBSTDCPP_VER ${D}${libdir}/

	install -d ${D}${libdir}/apt/methods
	install -m 0755 bin/methods/* ${D}${libdir}/apt/methods/

	install -d ${D}${libdir}/dpkg/methods/apt
	install -m 0644 ${S}/dselect/desc.apt ${D}${libdir}/dpkg/methods/apt/ 
	install -m 0644 ${S}/dselect/names ${D}${libdir}/dpkg/methods/apt/ 
	install -m 0755 ${S}/dselect/install ${D}${libdir}/dpkg/methods/apt/ 
	install -m 0755 ${S}/dselect/setup ${D}${libdir}/dpkg/methods/apt/ 
	install -m 0755 ${S}/dselect/update ${D}${libdir}/dpkg/methods/apt/ 

	install -d ${D}${sysconfdir}/apt
	install -d ${D}${sysconfdir}/apt/apt.conf.d
	install -d ${D}${sysconfdir}/apt/preferences.d
	install -d ${D}${localstatedir}/lib/apt/lists/partial
	install -d ${D}${localstatedir}/cache/apt/archives/partial

	install -d ${D}${localstatedir}/log/apt/

	install -d ${D}${includedir}/apt-pkg
	for h in `find ${S}/apt-pkg ${S}/apt-inst -name '*.h'`
	do
		install -m 0644 $h ${D}${includedir}/apt-pkg
	done
}
