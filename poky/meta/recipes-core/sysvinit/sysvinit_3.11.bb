SUMMARY = "System-V like init"
DESCRIPTION = "This package is required to boot in most configurations.  It provides the /sbin/init program.  This is the first process started on boot, and the last process terminated before the system halts."
HOMEPAGE = "http://savannah.nongnu.org/projects/sysvinit/"
SECTION = "base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYRIGHT;endline=15;md5=a1d3b3526501d3546d530bbe6ab6cdbe \
                    "

RDEPENDS:${PN} = "${PN}-inittab"

GITHUB_BASE_URI = "https://github.com/slicer69/${BPN}/releases/"
SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BP}.tar.xz \
           file://install.patch \
           file://crypt-lib.patch \
           file://pidof-add-m-option.patch \
           file://realpath.patch \
           file://0001-include-sys-sysmacros.h-for-major-minor-defines-in-g.patch \
           file://rcS-default \
           file://rc \
           file://rcS \
           file://bootlogd.init \
           file://01_bootlogd \
           "
SRC_URI[sha256sum] = "04f1b7d67413af1c09c41acc1e3ce946f2d0f7a1e47c9978744bc542864e13d4"

S = "${WORKDIR}/sysvinit-${PV}"

inherit update-alternatives features_check github-releases
DEPENDS:append = " update-rc.d-native base-passwd virtual/crypt"
do_package_setscene[depends] = "${MLPREFIX}base-passwd:do_populate_sysroot"

PACKAGECONFIG[psplash-text-updates] = ",,"

REQUIRED_DISTRO_FEATURES = "sysvinit"

ALTERNATIVE:${PN} = "init mountpoint halt reboot runlevel shutdown poweroff last lastb mesg utmpdump wall"

ALTERNATIVE_PRIORITY = "200"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] = "50"

ALTERNATIVE_LINK_NAME[mountpoint] = "${base_bindir}/mountpoint"
ALTERNATIVE_PRIORITY[mountpoint] = "20"

ALTERNATIVE_LINK_NAME[halt] = "${base_sbindir}/halt"
ALTERNATIVE_LINK_NAME[reboot] = "${base_sbindir}/reboot"
ALTERNATIVE_LINK_NAME[runlevel] = "${base_sbindir}/runlevel"
ALTERNATIVE_LINK_NAME[shutdown] = "${base_sbindir}/shutdown"
ALTERNATIVE_LINK_NAME[poweroff] = "${base_sbindir}/poweroff"

ALTERNATIVE:${PN}-pidof = "pidof"
ALTERNATIVE_LINK_NAME[pidof] = "${base_bindir}/pidof"

ALTERNATIVE:${PN}-sulogin = "sulogin"
ALTERNATIVE_LINK_NAME[sulogin] = "${base_sbindir}/sulogin"

ALTERNATIVE:${PN}-doc = "mountpoint.1 last.1 lastb.1 mesg.1 wall.1 sulogin.8 utmpdump.1"

ALTERNATIVE_LINK_NAME[last.1] = "${mandir}/man1/last.1"
ALTERNATIVE_LINK_NAME[lastb.1] = "${mandir}/man1/lastb.1"
ALTERNATIVE_LINK_NAME[mesg.1] = "${mandir}/man1/mesg.1"
ALTERNATIVE_LINK_NAME[mountpoint.1] = "${mandir}/man1/mountpoint.1"
ALTERNATIVE_LINK_NAME[sulogin.8] = "${mandir}/man8/sulogin.8"
ALTERNATIVE_LINK_NAME[utmpdump.1] = "${mandir}/man1/utmpdump.1"
ALTERNATIVE_LINK_NAME[wall.1] = "${mandir}/man1/wall.1"

PACKAGES =+ "sysvinit-pidof sysvinit-sulogin"
FILES:${PN} += "${base_sbindir}/* ${base_bindir}/*"
FILES:sysvinit-pidof = "${base_bindir}/pidof.sysvinit ${base_sbindir}/killall5"
FILES:sysvinit-sulogin = "${base_sbindir}/sulogin.sysvinit"

RDEPENDS:${PN} += "sysvinit-pidof initd-functions base-passwd"

CFLAGS:prepend = "-D_GNU_SOURCE "
export LCRYPT = "-lcrypt"
EXTRA_OEMAKE += "'base_bindir=${base_bindir}' \
		 'base_sbindir=${base_sbindir}' \
		 'bindir=${bindir}' \
		 'sbindir=${sbindir}' \
		 'sysconfdir=${sysconfdir}' \
		 'includedir=${includedir}' \
		 'mandir=${mandir}' \
                 MNTPOINT=yes"

do_install () {
	oe_runmake 'ROOT=${D}' install

	install -d ${D}${sysconfdir} \
		   ${D}${sysconfdir}/default \
		   ${D}${sysconfdir}/init.d
	for level in S 0 1 2 3 4 5 6; do
		install -d ${D}${sysconfdir}/rc$level.d
	done

	sed -e \
		's:#PSPLASH_TEXT#:${@bb.utils.contains("PACKAGECONFIG","psplash-text-updates","yes","no", d)}:g' \
		${UNPACKDIR}/rcS-default > ${D}${sysconfdir}/default/rcS
	chmod 0644 ${D}${sysconfdir}/default/rcS
	install -m 0755    ${UNPACKDIR}/rc		${D}${sysconfdir}/init.d
	install -m 0755    ${UNPACKDIR}/rcS		${D}${sysconfdir}/init.d
	install -m 0755    ${UNPACKDIR}/bootlogd.init     ${D}${sysconfdir}/init.d/bootlogd
	ln -sf bootlogd ${D}${sysconfdir}/init.d/stop-bootlogd

	update-rc.d -r ${D} bootlogd start 07 S .
	update-rc.d -r ${D} stop-bootlogd start 99 2 3 4 5 .

	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${UNPACKDIR}/01_bootlogd ${D}${sysconfdir}/default/volatiles

	chown root:shutdown ${D}${base_sbindir}/halt ${D}${base_sbindir}/shutdown
	chmod o-x,u+s ${D}${base_sbindir}/halt ${D}${base_sbindir}/shutdown

        # Already provided by e2fsprogs; sysvinit's version is a copy from there
        rm ${D}${base_sbindir}/logsave
        rm ${D}${mandir}/man8/logsave.8
}
