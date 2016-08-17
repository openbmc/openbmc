SUMMARY = "SysV init scripts"
DESCRIPTION = "Initscripts provide the basic system startup initialization scripts for the system.  These scripts include actions such as filesystem mounting, fsck, RTC manipulation and other actions routinely performed at system startup.  In addition, the scripts are also used during system shutdown to reverse the actions performed at startup."
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
PR = "r155"

INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://functions \
           file://halt \
           file://umountfs \
           file://devpts.sh \
           file://devpts \
           file://hostname.sh \
           file://mountall.sh \
           file://banner.sh \
           file://bootmisc.sh \
           file://mountnfs.sh \
           file://reboot \
           file://checkfs.sh \
           file://single \
           file://sendsigs \
           file://urandom \
           file://rmnologin.sh \
           file://checkroot.sh \
           file://umountnfs.sh \
           file://sysfs.sh \
           file://populate-volatile.sh \
           file://read-only-rootfs-hook.sh \
           file://volatiles \
           file://save-rtc.sh \
           file://GPLv2.patch \
           file://dmesg.sh \
           file://logrotate-dmesg.conf \
           ${@bb.utils.contains('DISTRO_FEATURES','selinux','file://sushell','',d)} \
"

S = "${WORKDIR}"

SRC_URI_append_arm = " file://alignment.sh"

KERNEL_VERSION = ""

inherit update-alternatives
DEPENDS_append = " update-rc.d-native"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"

PACKAGES =+ "${PN}-functions"
RDEPENDS_${PN} = "${PN}-functions \
                  ${@bb.utils.contains('DISTRO_FEATURES','selinux','bash','',d)} \
		 "
FILES_${PN}-functions = "${sysconfdir}/init.d/functions*"

ALTERNATIVE_PRIORITY_${PN}-functions = "90"
ALTERNATIVE_${PN}-functions = "functions"
ALTERNATIVE_LINK_NAME[functions] = "${sysconfdir}/init.d/functions"

HALTARGS ?= "-d -f"

do_configure() {
	sed -i -e "s:SED_HALTARGS:${HALTARGS}:g" ${WORKDIR}/halt
	sed -i -e "s:SED_HALTARGS:${HALTARGS}:g" ${WORKDIR}/reboot
}

do_install () {
#
# Create directories and install device independent scripts
#
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/rcS.d
	install -d ${D}${sysconfdir}/rc0.d
	install -d ${D}${sysconfdir}/rc1.d
	install -d ${D}${sysconfdir}/rc2.d
	install -d ${D}${sysconfdir}/rc3.d
	install -d ${D}${sysconfdir}/rc4.d
	install -d ${D}${sysconfdir}/rc5.d
	install -d ${D}${sysconfdir}/rc6.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/default/volatiles
	# Holds state information pertaining to urandom
	install -d ${D}${localstatedir}/lib/urandom

	install -m 0644    ${WORKDIR}/functions		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/bootmisc.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/checkroot.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/halt		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/hostname.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/mountall.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/mountnfs.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/reboot		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/rmnologin.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/sendsigs		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/single		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/umountnfs.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/urandom		${D}${sysconfdir}/init.d
	sed -i ${D}${sysconfdir}/init.d/urandom -e 's,/var/,${localstatedir}/,g;s,/etc/,${sysconfdir}/,g'
	install -m 0755    ${WORKDIR}/devpts.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/devpts		${D}${sysconfdir}/default
	install -m 0755    ${WORKDIR}/sysfs.sh		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/populate-volatile.sh ${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/read-only-rootfs-hook.sh ${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/save-rtc.sh	${D}${sysconfdir}/init.d
	install -m 0644    ${WORKDIR}/volatiles		${D}${sysconfdir}/default/volatiles/00_core
	install -m 0755    ${WORKDIR}/dmesg.sh		${D}${sysconfdir}/init.d
	install -m 0644    ${WORKDIR}/logrotate-dmesg.conf ${D}${sysconfdir}/

	if [ "${TARGET_ARCH}" = "arm" ]; then
		install -m 0755 ${WORKDIR}/alignment.sh	${D}${sysconfdir}/init.d
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES','selinux','true','false',d)}; then
		install -d ${D}/${base_sbindir}
		install -m 0755 ${WORKDIR}/sushell ${D}/${base_sbindir}
	fi
#
# Install device dependent scripts
#
	install -m 0755 ${WORKDIR}/banner.sh	${D}${sysconfdir}/init.d/banner.sh
	install -m 0755 ${WORKDIR}/umountfs	${D}${sysconfdir}/init.d/umountfs
#
# Create runlevel links
#
	update-rc.d -r ${D} rmnologin.sh start 99 2 3 4 5 .
	update-rc.d -r ${D} sendsigs start 20 0 6 .
	update-rc.d -r ${D} urandom start 38 S 0 6 .
	update-rc.d -r ${D} umountnfs.sh start 31 0 1 6 .
	update-rc.d -r ${D} umountfs start 40 0 6 .
	update-rc.d -r ${D} reboot start 90 6 .
	update-rc.d -r ${D} halt start 90 0 .
	update-rc.d -r ${D} save-rtc.sh start 25 0 6 .
	update-rc.d -r ${D} banner.sh start 02 S .
	update-rc.d -r ${D} checkroot.sh start 06 S .
	update-rc.d -r ${D} mountall.sh start 03 S .
	update-rc.d -r ${D} hostname.sh start 39 S .
	update-rc.d -r ${D} mountnfs.sh start 15 2 3 4 5 .
	update-rc.d -r ${D} bootmisc.sh start 55 S .
	update-rc.d -r ${D} sysfs.sh start 02 S .
	update-rc.d -r ${D} populate-volatile.sh start 37 S .
	update-rc.d -r ${D} read-only-rootfs-hook.sh start 29 S .
	update-rc.d -r ${D} devpts.sh start 38 S .
	if [ "${TARGET_ARCH}" = "arm" ]; then
	        update-rc.d -r ${D} alignment.sh start 06 S .
	fi
	# We wish to have /var/log ready at this stage so execute this after
	# populate-volatile.sh
	update-rc.d -r ${D} dmesg.sh start 38 S .
}

MASKED_SCRIPTS = " \
  banner \
  bootmisc \
  checkfs \
  checkroot \
  devpts \
  dmesg \
  hostname \
  mountall \
  mountnfs \
  populate-volatile \
  read-only-rootfs-hook \
  rmnologin \
  sysfs \
  urandom"

pkg_postinst_${PN} () {
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		for SERVICE in ${MASKED_SCRIPTS}; do
			systemctl $OPTS mask $SERVICE.service
		done
	fi

    # Delete any old volatile cache script, as directories may have moved
    if [ -z "$D" ]; then
        rm -f "/etc/volatile.cache"
    fi
}

CONFFILES_${PN} += "${sysconfdir}/init.d/checkroot.sh"
