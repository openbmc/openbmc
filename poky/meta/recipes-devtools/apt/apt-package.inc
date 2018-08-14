apt-manpages="doc/apt-cache.8 \
	      doc/apt-cdrom.8 \
	      doc/apt-config.8 \
	      doc/apt-get.8 \
	      doc/apt.8 \
	      doc/apt.conf.5 \
	      doc/apt_preferences.5 \
	      doc/sources.list.5"
apt-utils-manpages="doc/apt-extracttemplates.1 \
		    doc/apt-sortpkgs.1"

def get_files_apt_doc(d, bb, manpages):
    import re
    manpages = re.sub(r'\bdoc/(\S+)/(\S+)\.\1\.(.)\b', r'${mandir}/\1/man\3/\2.\3', manpages)
    manpages = re.sub(r'\bdoc/(\S+)\.(.)\b', r'${mandir}/man\2/\1.\2', manpages)
    return manpages

def get_commands_apt_doc(d, bb, manpages):
    s = list()
    __dir_cache__ = list()
    for m in manpages.split():
        dest = get_files_apt_doc(d, bb, m)
        dir = os.path.dirname(dest)
        if not dir in __dir_cache__:
            s.append("install -d ${D}/%s" % dir)
            __dir_cache__.append(dir)
        s.append("install -m 0644 %s ${D}/%s" % (m, dest))
    return "\n".join(s)

PACKAGES += "${PN}-utils ${PN}-utils-doc"
FILES_${PN} = "${bindir}/apt-cdrom ${bindir}/apt-get \
	       ${bindir}/apt-config ${bindir}/apt-cache \
	       ${libdir}/apt ${libdir}/libapt*.so.* \
	       ${localstatedir} ${sysconfdir} \
	       ${libdir}/dpkg \
	       ${systemd_unitdir}/system \
           "
FILES_${PN}-utils = "${bindir}/apt-sortpkgs ${bindir}/apt-extracttemplates"
FILES_${PN}-doc = "${@get_files_apt_doc(d, bb, d.getVar('apt-manpages'))} \
		   ${docdir}/apt"
FILES_${PN}-utils-doc = "${@get_files_apt_doc(d, bb, d.getVar('apt-utils-manpages'))}"
FILES_${PN}-dev = "${libdir}/libapt*.so ${includedir}"

inherit systemd

SYSTEMD_SERVICE_${PN} = "apt-daily.timer"

do_install () {
	set -x
	install -d ${D}${bindir}
	install -m 0755 bin/apt-key ${D}${bindir}/
	install -m 0755 bin/apt-cdrom ${D}${bindir}/
	install -m 0755 bin/apt-get ${D}${bindir}/
	install -m 0755 bin/apt-config ${D}${bindir}/
	install -m 0755 bin/apt-cache ${D}${bindir}/

	install -m 0755 bin/apt-sortpkgs ${D}${bindir}/
	install -m 0755 bin/apt-extracttemplates ${D}${bindir}/

	oe_libinstall -so -C bin libapt-pkg ${D}${libdir}
	oe_libinstall -so -C bin libapt-inst ${D}${libdir}

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
	install -d ${D}${sysconfdir}/apt/sources.list.d
	install -d ${D}${sysconfdir}/apt/preferences.d
	install -d ${D}${localstatedir}/lib/apt/lists/partial
	install -d ${D}${localstatedir}/cache/apt/archives/partial
	install -d ${D}${docdir}/apt/examples
	install -m 0644 ${S}/doc/examples/* ${D}${docdir}/apt/examples/

	install -d ${D}${includedir}/apt-pkg/
	install -m 0644 include/apt-pkg/*.h ${D}${includedir}/apt-pkg/

	install -d ${D}${systemd_unitdir}/system/
	install -m 0644 ${S}/debian/apt.systemd.daily ${D}${libdir}/apt/
	install -m 0644 ${S}/debian/apt-daily.service ${D}${systemd_unitdir}/system/
	sed -i 's#/usr/lib/apt/#${libdir}/apt/#g' ${D}${systemd_unitdir}/system/apt-daily.service
	install -m 0644 ${S}/debian/apt-daily.timer ${D}${systemd_unitdir}/system/
	install -d ${D}${sysconfdir}/cron.daily/
	install -m 0755 ${S}/debian/apt.apt-compat.cron.daily ${D}${sysconfdir}/cron.daily/
	sed -i 's#/usr/lib/apt/#${libdir}/apt/#g' ${D}${sysconfdir}/cron.daily/apt.apt-compat.cron.daily
}
