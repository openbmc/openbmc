#The functionality of Bastille that is actually available is restricted. Please
#consult the README file for the meta-security layer for additional information.
SUMMARY = "Linux hardening tool"
DESCRIPTION = "Bastille Linux is a Hardening and Reporting/Auditing Program which enhances the security of a Linux box, by configuring daemons, system settings and firewalling."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"
# Bash is needed for set +o privileged (check busybox), might also need ncurses
DEPENDS = "virtual/kernel"
RDEPENDS_${PN} = "perl bash tcl perl-module-getopt-long perl-module-text-wrap lib-perl perl-module-file-path perl-module-mime-base64 perl-module-file-find perl-module-errno perl-module-file-glob perl-module-tie-hash-namedcapture perl-module-file-copy perl-module-english perl-module-exporter perl-module-cwd libcurses-perl coreutils"
FILES_${PN} += "/run/lock/subsys/bastille"

SRC_URI = "http://sourceforge.net/projects/bastille-linux/files/bastille-linux/3.2.1/Bastille-3.2.1.tar.bz2 \
           file://AccountPermission.pm \
           file://FileContent.pm \
           file://HPSpecific.pm \
           file://Miscellaneous.pm \
           file://ServiceAdmin.pm \
           file://config \
           file://fix_version_parse.patch \
           file://fixed_defined_warnings.patch \
           file://call_output_config.patch \
           file://fix_missing_use_directives.patch \
           file://fix_number_of_modules.patch \
           file://remove_questions_text_file_references.patch \
           file://simplify_B_place.patch \
           file://find_existing_config.patch \
           file://upgrade_options_processing.patch \
           file://accept_os_flag_in_backend.patch \
           file://allow_os_with_assess.patch \
           file://edit_usage_message.patch \
           file://organize_distro_discovery.patch \
           file://do_not_apply_config.patch \
           "

SRC_URI[md5sum] = "df803f7e38085aa5da79f85d0539f91b"
SRC_URI[sha256sum] = "0ea25191b1dc1c8f91e1b6f8cb5436a3aa1e57418809ef902293448efed5021a"

S = "${WORKDIR}/Bastille"

do_install () {
	install -d ${D}${sbindir}
	install -d ${D}${libdir}/perl5/site_perl/Curses

	install -d ${D}${libdir}/Bastille
	install -d ${D}${libdir}/Bastille/API
	install -d ${D}${datadir}/Bastille
	install -d ${D}${datadir}/Bastille/OSMap
	install -d ${D}${datadir}/Bastille/OSMap/Modules
	install -d ${D}${datadir}/Bastille/Questions
	install -d ${D}${datadir}/Bastille/FKL/configs/
	install -d ${D}${localstatedir}/log/Bastille
	install -d ${D}${sysconfdir}/Bastille
	install -m 0755 AutomatedBastille  ${D}${sbindir}
	install -m 0755 BastilleBackEnd    ${D}${sbindir}
	install -m 0755 InteractiveBastille    ${D}${sbindir}
	install -m 0644 Modules.txt    ${D}${datadir}/Bastille
	# New Weights file(s).
	install -m 0644 Weights.txt    ${D}${datadir}/Bastille
	# Castle graphic
	install -m 0644 bastille.jpg    ${D}${datadir}/Bastille/
	# Javascript file
	install -m 0644 wz_tooltip.js    ${D}${datadir}/Bastille/
	install -m 0644 Credits    ${D}${datadir}/Bastille
	install -m 0644 FKL/configs/fkl_config_redhat.cfg    ${D}${datadir}/Bastille/FKL/configs/
	install -m 0755 RevertBastille    ${D}${sbindir}
	install -m 0755 bin/bastille    ${D}${sbindir}
	install -m 0644 bastille-firewall    ${D}${datadir}/Bastille
	install -m 0644 bastille-firewall-reset    ${D}${datadir}/Bastille
	install -m 0644 bastille-firewall-schedule    ${D}${datadir}/Bastille
	install -m 0644 bastille-tmpdir-defense.sh    ${D}${datadir}/Bastille
	install -m 0644 bastille-tmpdir.csh    ${D}${datadir}/Bastille
	install -m 0644 bastille-tmpdir.sh    ${D}${datadir}/Bastille
	install -m 0644 bastille-firewall.cfg    ${D}${datadir}/Bastille
	install -m 0644 bastille-ipchains    ${D}${datadir}/Bastille
	install -m 0644 bastille-netfilter    ${D}${datadir}/Bastille
	install -m 0644 bastille-firewall-early.sh    ${D}${datadir}/Bastille
	install -m 0644 bastille-firewall-pre-audit.sh    ${D}${datadir}/Bastille
	install -m 0644 complete.xbm    ${D}${datadir}/Bastille
	install -m 0644 incomplete.xbm    ${D}${datadir}/Bastille
	install -m 0644 disabled.xpm    ${D}${datadir}/Bastille
	install -m 0644 ifup-local    ${D}${datadir}/Bastille
	install -m 0644 hosts.allow    ${D}${datadir}/Bastille

	install -m 0644 Bastille/AccountSecurity.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/Apache.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/API.pm    ${D}${libdir}/Bastille
	install -m 0644 ${WORKDIR}/AccountPermission.pm    ${D}${libdir}/Bastille/API
	install -m 0644 ${WORKDIR}/FileContent.pm    ${D}${libdir}/Bastille/API
	install -m 0644 ${WORKDIR}/HPSpecific.pm    ${D}${libdir}/Bastille/API
	install -m 0644 ${WORKDIR}/ServiceAdmin.pm    ${D}${libdir}/Bastille/API
	install -m 0644 ${WORKDIR}/Miscellaneous.pm    ${D}${libdir}/Bastille/API
	install -m 0644 Bastille/BootSecurity.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/ConfigureMiscPAM.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/DisableUserTools.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/DNS.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/FilePermissions.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/FTP.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/Firewall.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/OSX_API.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/LogAPI.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/HP_UX.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/IOLoader.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/Patches.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/Logging.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/MiscellaneousDaemons.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/PatchDownload.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/Printing.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/PSAD.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/RemoteAccess.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/SecureInetd.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/Sendmail.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/TestDriver.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/TMPDIR.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_AccountSecurity.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_Apache.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_DNS.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_FTP.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_HP_UX.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_MiscellaneousDaemons.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_Patches.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_SecureInetd.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_Sendmail.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_BootSecurity.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_DisableUserTools.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_FilePermissions.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_Logging.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/test_Printing.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille/IPFilter.pm    ${D}${libdir}/Bastille
	install -m 0644 Bastille_Curses.pm    ${D}${libdir}/perl5/site_perl
	install -m 0644 Bastille_Tk.pm    ${D}${libdir}/perl5/site_perl
	install -m 0644 Curses/Widgets.pm    ${D}${libdir}/perl5/site_perl/Curses

	install -m 0644 OSMap/LINUX.bastille    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/LINUX.system    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/LINUX.service    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/HP-UX.bastille    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/HP-UX.system    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/HP-UX.service    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/OSX.bastille    ${D}${datadir}/Bastille/OSMap
	install -m 0644 OSMap/OSX.system    ${D}${datadir}/Bastille/OSMap

	install -m 0777 ${WORKDIR}/config ${D}${sysconfdir}/Bastille/config

	for file in `cat Modules.txt` ; do
		install -m 0644 Questions/$file.txt ${D}${datadir}/Bastille/Questions
	done

	${THISDIR}/files/set_required_questions.py ${D}${sysconfdir}/Bastille/config ${D}${datadir}/Bastille/Questions

	ln -s RevertBastille ${D}${sbindir}/UndoBastille
}

FILES_${PN} += "${datadir}/Bastille ${libdir}/Bastille ${libdir}/perl* ${sysconfdir}/*"
