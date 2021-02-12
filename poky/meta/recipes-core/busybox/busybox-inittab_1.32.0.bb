SUMMARY = "inittab configuration for BusyBox"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://inittab"

S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

do_compile() {
	:
}

do_install() {
	install -d ${D}${sysconfdir}
	install -D -m 0644 ${WORKDIR}/inittab ${D}${sysconfdir}/inittab
	tmp="${SERIAL_CONSOLES}"
	[ -n "$tmp" ] && echo >> ${D}${sysconfdir}/inittab
	for i in $tmp
	do
		j=`echo ${i} | sed s/\;/\ /g`
		id=`echo ${i} | sed -e 's/^.*;//' -e 's/;.*//'`
		echo "$id::respawn:${base_sbindir}/getty ${j}" >> ${D}${sysconfdir}/inittab
	done
}

# SERIAL_CONSOLES is generally defined by the MACHINE .conf.
# Set PACKAGE_ARCH appropriately.
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} = "${sysconfdir}/inittab"
CONFFILES_${PN} = "${sysconfdir}/inittab"

RCONFLICTS_${PN} = "sysvinit-inittab"
