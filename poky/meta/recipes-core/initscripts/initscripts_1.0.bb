SUMMARY = "SysV init scripts"
HOMEPAGE = "https://github.com/fedora-sysv/initscripts"
DESCRIPTION = "Initscripts provide the basic system startup initialization scripts for the system.  These scripts include actions such as filesystem mounting, fsck, RTC manipulation and other actions routinely performed at system startup.  In addition, the scripts are also used during system shutdown to reverse the actions performed at startup."
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://functions;beginline=7;endline=7;md5=829e563511c9a1d6d41f17a7a4989d6a"

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
           file://dmesg.sh \
           file://logrotate-dmesg.conf \
           ${@bb.utils.contains('DISTRO_FEATURES','selinux','file://sushell','',d)} \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI:append:arm = " file://alignment.sh"
SRC_URI:append:armeb = " file://alignment.sh"

KERNEL_VERSION = ""

DEPENDS:append = " update-rc.d-native"
PACKAGE_WRITE_DEPS:append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"

PACKAGES =+ "${PN}-functions ${PN}-sushell"
RDEPENDS:${PN} = "initd-functions \
                  ${@bb.utils.contains('DISTRO_FEATURES','selinux','${PN}-sushell','',d)} \
                  init-system-helpers-service \
		 "
# Recommend pn-functions so that it will be a preferred default provider for initd-functions
RRECOMMENDS:${PN} = "${PN}-functions"
RPROVIDES:${PN}-functions = "initd-functions"
RCONFLICTS:${PN}-functions = "lsbinitscripts"
FILES:${PN}-functions = "${sysconfdir}/init.d/functions*"
FILES:${PN}-sushell = "${base_sbindir}/sushell"

HALTARGS ?= "-d -f"
VARLIBMOUNTARGS ?= ""

do_configure() {
	sed -i -e "s:SED_HALTARGS:${HALTARGS}:g" ${S}/halt
	sed -i -e "s:SED_HALTARGS:${HALTARGS}:g" ${S}/reboot
	sed -i -e "s:SED_VARLIBMOUNTARGS:${VARLIBMOUNTARGS}:g" ${S}/read-only-rootfs-hook.sh
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

	install -m 0644    ${S}/functions		${D}${sysconfdir}/init.d
	install -m 0755    ${S}/bootmisc.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/checkroot.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/halt		${D}${sysconfdir}/init.d
	install -m 0755    ${S}/hostname.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/mountall.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/mountnfs.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/reboot		${D}${sysconfdir}/init.d
	install -m 0755    ${S}/rmnologin.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/sendsigs		${D}${sysconfdir}/init.d
	install -m 0755    ${S}/single		${D}${sysconfdir}/init.d
	install -m 0755    ${S}/umountnfs.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/urandom		${D}${sysconfdir}/init.d
	sed -i ${D}${sysconfdir}/init.d/urandom -e 's,/var/,${localstatedir}/,g;s,/etc/,${sysconfdir}/,g'
	install -m 0755    ${S}/devpts.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${S}/devpts		${D}${sysconfdir}/default
	install -m 0755    ${S}/sysfs.sh		${D}${sysconfdir}/init.d
	install -m 0755    ${S}/populate-volatile.sh ${D}${sysconfdir}/init.d
	install -m 0755    ${S}/read-only-rootfs-hook.sh ${D}${sysconfdir}/init.d
	install -m 0755    ${S}/save-rtc.sh	${D}${sysconfdir}/init.d
	install -m 0644    ${S}/volatiles		${D}${sysconfdir}/default/volatiles/00_core
	if [ ${@ oe.types.boolean('${VOLATILE_LOG_DIR}') } = True ]; then
		sed -i -e '\@^d root root 0755 /var/volatile/log none$@ a\l root root 0755 /var/log /var/volatile/log' \
			${D}${sysconfdir}/default/volatiles/00_core
	fi
	if [ "${VOLATILE_TMP_DIR}" != "yes" ]; then
		sed -i -e "/\<tmp\>/d" ${D}${sysconfdir}/default/volatiles/00_core
	fi
	install -m 0755    ${S}/dmesg.sh		${D}${sysconfdir}/init.d
	install -m 0644    ${S}/logrotate-dmesg.conf ${D}${sysconfdir}/

	if [ "${TARGET_ARCH}" = "arm" ]; then
		install -m 0755 ${S}/alignment.sh	${D}${sysconfdir}/init.d
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES','selinux','true','false',d)}; then
		install -d ${D}/${base_sbindir}
		install -m 0755 ${S}/sushell ${D}/${base_sbindir}
	fi
#
# Install device dependent scripts
#
	install -m 0755 ${S}/banner.sh	${D}${sysconfdir}/init.d/banner.sh
	install -m 0755 ${S}/umountfs	${D}${sysconfdir}/init.d/umountfs
#
# Create runlevel links
#
	update-rc.d -r ${D} rmnologin.sh start 99 2 3 4 5 .
	update-rc.d -r ${D} sendsigs start 20 0 6 .
	update-rc.d -r ${D} urandom start 38 S 0 6 .
	update-rc.d -r ${D} umountnfs.sh stop 31 0 1 6 .
	update-rc.d -r ${D} umountfs start 40 0 6 .
	update-rc.d -r ${D} reboot start 90 6 .
	update-rc.d -r ${D} halt start 90 0 .
	update-rc.d -r ${D} save-rtc.sh start 25 0 6 .
	update-rc.d -r ${D} banner.sh start 02 S .
	update-rc.d -r ${D} checkroot.sh start 05 S .
	update-rc.d -r ${D} mountall.sh start 03 S .
	update-rc.d -r ${D} hostname.sh start 39 S .
	update-rc.d -r ${D} mountnfs.sh start 15 2 3 4 5 .
	update-rc.d -r ${D} bootmisc.sh start 36 S .
	update-rc.d -r ${D} sysfs.sh start 02 S .
	update-rc.d -r ${D} populate-volatile.sh start 37 S .
	update-rc.d -r ${D} read-only-rootfs-hook.sh start 29 S .
	update-rc.d -r ${D} devpts.sh start 06 S .
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

pkg_postinst:${PN} () {
	if type systemctl >/dev/null 2>/dev/null; then
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

CONFFILES:${PN} += "${sysconfdir}/init.d/checkroot.sh"
