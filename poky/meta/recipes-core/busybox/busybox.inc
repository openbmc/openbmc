SUMMARY = "Tiny versions of many common UNIX utilities in a single small executable"
DESCRIPTION = "BusyBox combines tiny versions of many common UNIX utilities into a single small executable. It provides minimalist replacements for most of the utilities you usually find in GNU fileutils, shellutils, etc. The utilities in BusyBox generally have fewer options than their full-featured GNU cousins; however, the options that are included provide the expected functionality and behave very much like their GNU counterparts. BusyBox provides a fairly complete POSIX environment for any small or embedded system."
HOMEPAGE = "http://www.busybox.net"
BUGTRACKER = "https://bugs.busybox.net/"

DEPENDS += "kern-tools-native virtual/crypt"

# bzip2 applet in busybox is based on lightly-modified bzip2 source
# the GPL is version 2 only
LICENSE = "GPLv2 & bzip2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de10de48642ab74318e893a61105afbb"

SECTION = "base"

# Whether to split the suid apps into a seperate binary
BUSYBOX_SPLIT_SUID ?= "1"

export EXTRA_CFLAGS = "${CFLAGS}"
export EXTRA_LDFLAGS = "${LDFLAGS}"

EXTRA_OEMAKE = "CC='${CC}' LD='${CCLD}' V=1 ARCH=${TARGET_ARCH} CROSS_COMPILE=${TARGET_PREFIX} SKIP_STRIP=y HOSTCC='${BUILD_CC}' HOSTCPP='${BUILD_CPP}'"

PACKAGES =+ "${PN}-httpd ${PN}-udhcpd ${PN}-udhcpc ${PN}-syslog ${PN}-mdev ${PN}-hwclock"

FILES_${PN}-httpd = "${sysconfdir}/init.d/busybox-httpd /srv/www"
FILES_${PN}-syslog = "${sysconfdir}/init.d/syslog* ${sysconfdir}/syslog-startup.conf* ${sysconfdir}/syslog.conf* ${systemd_unitdir}/system/syslog.service ${sysconfdir}/default/busybox-syslog"
FILES_${PN}-mdev = "${sysconfdir}/init.d/mdev ${sysconfdir}/mdev.conf ${sysconfdir}/mdev/*"
FILES_${PN}-udhcpd = "${sysconfdir}/init.d/busybox-udhcpd"
FILES_${PN}-udhcpc = "${sysconfdir}/udhcpc.d ${datadir}/udhcpc"
FILES_${PN}-hwclock = "${sysconfdir}/init.d/hwclock.sh"

INITSCRIPT_PACKAGES = "${PN}-httpd ${PN}-syslog ${PN}-udhcpd ${PN}-mdev ${PN}-hwclock"

INITSCRIPT_NAME_${PN}-httpd = "busybox-httpd"
INITSCRIPT_NAME_${PN}-hwclock = "hwclock.sh"
INITSCRIPT_NAME_${PN}-mdev = "mdev"
INITSCRIPT_PARAMS_${PN}-mdev = "start 04 S ."
INITSCRIPT_NAME_${PN}-syslog = "syslog"
INITSCRIPT_NAME_${PN}-udhcpd = "busybox-udhcpd"

SYSTEMD_PACKAGES = "${PN}-syslog"
SYSTEMD_SERVICE_${PN}-syslog = "${@bb.utils.contains('SRC_URI', 'file://syslog.cfg', 'busybox-syslog.service', '', d)}"

RDEPENDS_${PN}-syslog = "busybox"
CONFFILES_${PN}-syslog = "${sysconfdir}/syslog-startup.conf"
RCONFLICTS_${PN}-syslog = "rsyslog sysklogd syslog-ng"

CONFFILES_${PN}-mdev = "${sysconfdir}/mdev.conf"

RRECOMMENDS_${PN} = "${PN}-udhcpc"

RDEPENDS_${PN} = "${@["", "busybox-inittab"][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'busybox')]}"

inherit cml1 systemd update-rc.d ptest

# busybox's unzip test case needs zip command, which busybox itself does not provide
RDEPENDS_${PN}-ptest = "zip"

# internal helper
def busybox_cfg(feature, tokens, cnf, rem):
    if type(tokens) == type(""):
        tokens = [tokens]
    rem.extend(['/^[# ]*' + token + '[ =]/d' for token in tokens])
    if feature:
        cnf.extend([token + '=y' for token in tokens])
    else:
        cnf.extend(['# ' + token + ' is not set' for token in tokens])

# Map distro features to config settings
def features_to_busybox_settings(d):
    cnf, rem = ([], [])
    busybox_cfg(bb.utils.contains('DISTRO_FEATURES', 'ipv6', True, False, d), 'CONFIG_FEATURE_IPV6', cnf, rem)
    busybox_cfg(True, 'CONFIG_LFS', cnf, rem)
    busybox_cfg(True, 'CONFIG_FDISK_SUPPORT_LARGE_DISKS', cnf, rem)
    busybox_cfg(bb.utils.contains('DISTRO_FEATURES', 'nls', True, False, d), 'CONFIG_LOCALE_SUPPORT', cnf, rem)
    busybox_cfg(bb.utils.contains('DISTRO_FEATURES', 'ipv4', True, False, d), 'CONFIG_FEATURE_IFUPDOWN_IPV4', cnf, rem)
    busybox_cfg(bb.utils.contains('DISTRO_FEATURES', 'ipv6', True, False, d), 'CONFIG_FEATURE_IFUPDOWN_IPV6', cnf, rem)
    busybox_cfg(bb.utils.contains_any('DISTRO_FEATURES', 'bluetooth wifi', True, False, d), 'CONFIG_RFKILL', cnf, rem)
    return "\n".join(cnf), "\n".join(rem)

# X, Y = ${@features_to_busybox_settings(d)}
# unfortunately doesn't seem to work with bitbake, workaround:
def features_to_busybox_conf(d):
    cnf, rem = features_to_busybox_settings(d)
    return cnf
def features_to_busybox_del(d):
    cnf, rem = features_to_busybox_settings(d)
    return rem

configmangle = '/CONFIG_EXTRA_CFLAGS/d; \
		'
OE_FEATURES := "${@features_to_busybox_conf(d)}"
OE_DEL      := "${@features_to_busybox_del(d)}"
DO_IPv4 := "${@bb.utils.contains('DISTRO_FEATURES', 'ipv4', 1, 0, d)}"
DO_IPv6 := "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 1, 0, d)}"

python () {
  if "${OE_DEL}":
    d.setVar('configmangle_append', "${OE_DEL}" + "\n")
  if "${OE_FEATURES}":
    d.setVar('configmangle_append',
                   "/^### DISTRO FEATURES$/a\\\n%s\n\n" %
                   ("\\n".join((d.expand("${OE_FEATURES}").split("\n")))))
  d.setVar('configmangle_append',
                 "/^### CROSS$/a\\\n%s\n" %
                  ("\\n".join(["CONFIG_EXTRA_CFLAGS=\"${CFLAGS} ${HOST_CC_ARCH}\""
                        ])
                  ))
}

do_prepare_config () {
	if [ "${BUILD_REPRODUCIBLE_BINARIES}" = "1" ]; then
		export KCONFIG_NOTIMESTAMP=1
	fi
	sed -e '/CONFIG_STATIC/d' \
		< ${WORKDIR}/defconfig > ${S}/.config
	echo "# CONFIG_STATIC is not set" >> .config
	for i in 'CROSS' 'DISTRO FEATURES'; do echo "### $i"; done >> \
		${S}/.config
	sed -i -e '${configmangle}' ${S}/.config
	if test ${DO_IPv4} -eq 0 && test ${DO_IPv6} -eq 0; then
		# disable networking applets
		mv ${S}/.config ${S}/.config.oe-tmp
		awk 'BEGIN{net=0}
		/^# Networking Utilities/{net=1}
		/^#$/{if(net){net=net+1}}
		{if(net==2&&$0 !~ /^#/&&$1){print("# "$1" is not set")}else{print}}' \
		${S}/.config.oe-tmp > ${S}/.config
	fi
	sed -i 's/CONFIG_IFUPDOWN_UDHCPC_CMD_OPTIONS="-R -n"/CONFIG_IFUPDOWN_UDHCPC_CMD_OPTIONS="-R -b"/' ${S}/.config
	sed -i 's|${DEBUG_PREFIX_MAP}||g' ${S}/.config
}

# returns all the elements from the src uri that are .cfg files
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return sources_list

do_configure () {
	do_prepare_config
	merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
	cml1_do_configure
}

do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	if [ "${BUILD_REPRODUCIBLE_BINARIES}" = "1" ]; then
		export KCONFIG_NOTIMESTAMP=1
	fi
	if [ "${BUSYBOX_SPLIT_SUID}" = "1" -a x`grep "CONFIG_FEATURE_INDIVIDUAL=y" .config` = x ]; then
		# split the .config into two parts, and make two busybox binaries
		if [ -e .config.orig ]; then
			# Need to guard again an interrupted do_compile - restore any backup
			cp .config.orig .config
		fi
		cp .config .config.orig
		oe_runmake busybox.cfg.suid
		oe_runmake busybox.cfg.nosuid

		# workaround for suid bug 10346
		if ! grep -q "CONFIG_SH_IS_NONE" busybox.cfg.nosuid; then
			echo "CONFIG_SH_IS_NONE" >> busybox.cfg.suid
		fi

		for i in `cat busybox.cfg.suid busybox.cfg.nosuid`; do
			echo "# $i is not set" >> .config.disable.apps
		done
		merge_config.sh -m .config.orig .config.disable.apps
		cp .config .config.nonapps
		for s in suid nosuid; do
			cat busybox.cfg.$s | while read item; do
				grep -w "$item" .config.orig
			done > .config.app.$s

			# workaround for suid bug 10346
			if [ "$s" = "suid" ] ; then
				sed "s/.*CONFIG_SH_IS_NONE.*$/CONFIG_SH_IS_NONE=y/" -i .config.app.suid
			fi

			merge_config.sh -m .config.nonapps .config.app.$s
			oe_runmake busybox_unstripped
			mv busybox_unstripped busybox.$s
			oe_runmake busybox.links
			sort busybox.links > busybox.links.$s
			rm busybox.links
		done

		# hard fail if sh is being linked to the suid busybox (detects bug 10346)
		if grep -q -x "/bin/sh" busybox.links.suid; then
			bbfatal "busybox suid binary incorrectly provides /bin/sh"
		fi

		# copy .config.orig back to .config, because the install process may check this file
		cp .config.orig .config
		# cleanup
		rm .config.orig .config.app.suid .config.app.nosuid .config.disable.apps .config.nonapps
	else
		oe_runmake busybox_unstripped
		cp busybox_unstripped busybox
		oe_runmake busybox.links
	fi
}

do_install () {
	sed -i "s:^/bin/:BASE_BINDIR/:" busybox.links*
	sed -i "s:^/sbin/:BASE_SBINDIR/:" busybox.links*
	sed -i "s:^/usr/bin/:BINDIR/:" busybox.links*
	sed -i "s:^/usr/sbin/:SBINDIR/:" busybox.links*

	# Move arch/link to BINDIR to match coreutils
	sed -i "s:^BASE_BINDIR/arch:BINDIR/arch:" busybox.links*
	sed -i "s:^BASE_BINDIR/link:BINDIR/link:" busybox.links*

	sed -i "s:^BASE_BINDIR/:${base_bindir}/:" busybox.links*
	sed -i "s:^BASE_SBINDIR/:${base_sbindir}/:" busybox.links*
	sed -i "s:^BINDIR/:${bindir}/:" busybox.links*
	sed -i "s:^SBINDIR/:${sbindir}/:" busybox.links*

	install -d ${D}${sysconfdir}/init.d

	if ! grep -q "CONFIG_FEATURE_INDIVIDUAL=y" ${B}/.config; then
		# Install ${base_bindir}/busybox, and the ${base_bindir}/sh link so the postinst script
		# can run. Let update-alternatives handle the rest.
		install -d ${D}${base_bindir}
		if [ "${BUSYBOX_SPLIT_SUID}" = "1" ]; then
			install -m 4755 ${B}/busybox.suid ${D}${base_bindir}
			install -m 0755 ${B}/busybox.nosuid ${D}${base_bindir}
			install -m 0644 ${S}/busybox.links.suid ${D}${sysconfdir}
			install -m 0644 ${S}/busybox.links.nosuid ${D}${sysconfdir}
			if grep -q "CONFIG_SH_IS_ASH=y" ${B}/.config; then
				ln -sf busybox.nosuid ${D}${base_bindir}/sh
			fi
			# Keep a default busybox for people who want to invoke busybox directly.
			# This is also useful for the on device upgrade. Because we want
			# to use the busybox command in postinst.
			ln -sf busybox.nosuid ${D}${base_bindir}/busybox
		else
			if grep -q "CONFIG_FEATURE_SUID=y" ${B}/.config; then
				install -m 4755 ${B}/busybox ${D}${base_bindir}
			else
				install -m 0755 ${B}/busybox ${D}${base_bindir}
			fi
			install -m 0644 ${S}/busybox.links ${D}${sysconfdir}
			if grep -q "CONFIG_SH_IS_ASH=y" ${B}/.config; then
				ln -sf busybox ${D}${base_bindir}/sh
			fi
			# We make this symlink here to eliminate the error when upgrading together
			# with busybox-syslog. Without this symlink, the opkg may think of the
			# busybox.nosuid as obsolete and remove it, resulting in dead links like
			# ${base_bindir}/sed -> ${base_bindir}/busybox.nosuid. This will make upgrading busybox-syslog fail.
			# This symlink will be safely deleted in postinst, thus no negative effect.
			ln -sf busybox ${D}${base_bindir}/busybox.nosuid
		fi
	else
		install -d ${D}${base_bindir} ${D}${bindir} ${D}${libdir}
		cat busybox.links | while read FILE; do
			NAME=`basename "$FILE"`
			install -m 0755 "0_lib/$NAME" "${D}$FILE.${BPN}"
		done
		# add suid bit where needed
		for i in `grep -E "APPLET.*BB_SUID_((MAYBE|REQUIRE))" include/applets.h | grep -v _BB_SUID_DROP | cut -f 3 -d '(' | cut -f 1 -d ','`; do
			find ${D} -name $i.${BPN} -exec chmod a+s {} \;
		done
		install -m 0755 0_lib/libbusybox.so.${PV} ${D}${libdir}/libbusybox.so.${PV}
		ln -sf sh.${BPN} ${D}${base_bindir}/sh
		ln -sf ln.${BPN} ${D}${base_bindir}/ln
		ln -sf test.${BPN} ${D}${bindir}/test
		if [ -f ${D}/linuxrc.${BPN} ]; then
			mv ${D}/linuxrc.${BPN} ${D}/linuxrc
		fi
		install -m 0644 ${S}/busybox.links ${D}${sysconfdir}
	fi

	if grep -q "CONFIG_SYSLOGD=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/syslog ${D}${sysconfdir}/init.d/syslog
		install -m 644 ${WORKDIR}/syslog-startup.conf ${D}${sysconfdir}/syslog-startup.conf
		install -m 644 ${WORKDIR}/syslog.conf ${D}${sysconfdir}/syslog.conf
	fi
	if grep -q "CONFIG_CROND=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/busybox-cron ${D}${sysconfdir}/init.d/
	fi
	if grep -q "CONFIG_HTTPD=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/busybox-httpd ${D}${sysconfdir}/init.d/
		install -d ${D}/srv/www
	fi
	if grep -q "CONFIG_UDHCPD=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/busybox-udhcpd ${D}${sysconfdir}/init.d/
	fi
	if grep -q "CONFIG_HWCLOCK=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/hwclock.sh ${D}${sysconfdir}/init.d/
	fi
	if grep -q "CONFIG_UDHCPC=y" ${B}/.config; then
		install -d ${D}${sysconfdir}/udhcpc.d
		install -d ${D}${datadir}/udhcpc
		install -m 0755 ${WORKDIR}/simple.script ${D}${sysconfdir}/udhcpc.d/50default
		sed -i "s:/SBIN_DIR/:${base_sbindir}/:" ${D}${sysconfdir}/udhcpc.d/50default
		install -m 0755 ${WORKDIR}/default.script ${D}${datadir}/udhcpc/default.script
	fi
	if grep -q "CONFIG_INETD=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/inetd ${D}${sysconfdir}/init.d/inetd.${BPN}
		sed -i "s:/usr/sbin/:${sbindir}/:" ${D}${sysconfdir}/init.d/inetd.${BPN}
		install -m 0644 ${WORKDIR}/inetd.conf ${D}${sysconfdir}/
	fi
	if grep -q "CONFIG_MDEV=y" ${B}/.config; then
		install -m 0755 ${WORKDIR}/mdev ${D}${sysconfdir}/init.d/mdev
		if grep "CONFIG_FEATURE_MDEV_CONF=y" ${B}/.config; then
			install -m 644 ${WORKDIR}/mdev.conf ${D}${sysconfdir}/mdev.conf
			install -d ${D}${sysconfdir}/mdev
			install -m 0755 ${WORKDIR}/find-touchscreen.sh ${D}${sysconfdir}/mdev
			install -m 0755 ${WORKDIR}/mdev-mount.sh ${D}${sysconfdir}/mdev
		fi
	fi
	if grep -q "CONFIG_INIT=y" ${B}/.config; then
		install -D -m 0777 ${WORKDIR}/rcS ${D}${sysconfdir}/init.d/rcS
		install -D -m 0777 ${WORKDIR}/rcK ${D}${sysconfdir}/init.d/rcK
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		if grep -q "CONFIG_KLOGD=y" ${B}/.config; then
			install -d ${D}${systemd_unitdir}/system
			sed 's,@base_sbindir@,${base_sbindir},g' < ${WORKDIR}/busybox-klogd.service.in \
			> ${D}${systemd_unitdir}/system/busybox-klogd.service
		fi

		if grep -q "CONFIG_SYSLOGD=y" ${B}/.config; then
			install -d ${D}${systemd_unitdir}/system
			sed 's,@base_sbindir@,${base_sbindir},g' < ${WORKDIR}/busybox-syslog.service.in \
			> ${D}${systemd_unitdir}/system/busybox-syslog.service
			if  [ ! -e ${D}${systemd_unitdir}/system/busybox-klogd.service ] ; then
				sed -i '/klog/d' ${D}${systemd_unitdir}/system/busybox-syslog.service
			fi
			if [ -f ${WORKDIR}/busybox-syslog.default ] ; then
				install -d ${D}${sysconfdir}/default
				install -m 0644 ${WORKDIR}/busybox-syslog.default ${D}${sysconfdir}/default/busybox-syslog
			fi
		fi
	fi

	# Remove the sysvinit specific configuration file for systemd systems to avoid confusion
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'false', 'true', d)}; then
		rm -f ${D}${sysconfdir}/syslog-startup.conf
	fi
}

PTEST_BINDIR = "1"

do_install_ptest () {
	cp -r ${B}/testsuite ${D}${PTEST_PATH}/
	cp ${B}/.config      ${D}${PTEST_PATH}/
	ln -s /bin/busybox   ${D}${PTEST_PATH}/busybox
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "50"

python do_package_prepend () {
    # We need to load the full set of busybox provides from the /etc/busybox.links
    # Use this to see the update-alternatives with the right information

    dvar = d.getVar('D')
    pn = d.getVar('PN')
    def set_alternative_vars(links, target):
        links = d.expand(links)
        target = d.expand(target)
        f = open('%s%s' % (dvar, links), 'r')
        for alt_link_name in f:
            alt_link_name = alt_link_name.strip()
            alt_name = os.path.basename(alt_link_name)
            # Match coreutils
            if alt_name == '[':
                alt_name = 'lbracket'
            if alt_name == 'klogd' or alt_name == 'syslogd':
                d.appendVar('ALTERNATIVE_%s-syslog' % (pn), ' ' + alt_name)
            else:
                d.appendVar('ALTERNATIVE_%s' % (pn), ' ' + alt_name)
            d.setVarFlag('ALTERNATIVE_LINK_NAME', alt_name, alt_link_name)
            if os.path.exists('%s%s' % (dvar, target)):
                d.setVarFlag('ALTERNATIVE_TARGET', alt_name, target)
        f.close()
        return

    if os.path.exists('%s/etc/busybox.links' % (dvar)):
        set_alternative_vars("${sysconfdir}/busybox.links", "${base_bindir}/busybox")
    else:
        set_alternative_vars("${sysconfdir}/busybox.links.nosuid", "${base_bindir}/busybox.nosuid")
        set_alternative_vars("${sysconfdir}/busybox.links.suid", "${base_bindir}/busybox.suid")
}

# This part of code is dedicated to the on target upgrade problem.  It's known
# that if we don't make appropriate symlinks before update-alternatives calls,
# there will be errors indicating missing commands such as 'sed'.
# These symlinks will later be updated by update-alternatives calls.
# The update-alternatives.bbclass' postinst script runs firstly before other
# postinst, but this part of code needs run firstly, so add this funtion.
python populate_packages_updatealternatives_append() {
    postinst = """
test -n 2 > /dev/null || alias test='busybox test'
if test "x$D" = "x"; then
    # Remove busybox.nosuid if it's a symlink, because this situation indicates
    # that we're installing or upgrading to a one-binary busybox.
    if test -h ${base_bindir}/busybox.nosuid; then
        rm -f ${base_bindir}/busybox.nosuid
    fi
    for suffix in "" ".nosuid" ".suid"; do
        if test -e ${sysconfdir}/busybox.links$suffix; then
            while read link; do
                if test ! -e "$link"; then
                    # we can use busybox here because even if we are using splitted busybox
                    # we've made a symlink from /bin/busybox to /bin/busybox.nosuid.
                    busybox rm -f $link
                    busybox ln -s "${base_bindir}/busybox$suffix" $link
                fi
            done < ${sysconfdir}/busybox.links$suffix
        fi
    done
fi
if grep -q "^${base_bindir}/bash$" $D${sysconfdir}/busybox.links*; then
    grep -q "^${base_bindir}/bash$" $D${sysconfdir}/shells || echo ${base_bindir}/bash >> $D${sysconfdir}/shells
fi

"""
    d.prependVar('pkg_postinst_%s' % pkg, postinst)
}

pkg_prerm_${PN} () {
	# This is so you can make busybox commit suicide - removing busybox with no other packages
	# providing its files, this will make update-alternatives work, but the update-rc.d part
	# for syslog, httpd and/or udhcpd will fail if there is no other package providing sh
	tmpdir=`mktemp -d /tmp/busyboxrm-XXXXXX`
	ln -s ${base_bindir}/busybox $tmpdir/[
	ln -s ${base_bindir}/busybox $tmpdir/test
	ln -s ${base_bindir}/busybox $tmpdir/head
	ln -s ${base_bindir}/busybox $tmpdir/sh
	ln -s ${base_bindir}/busybox $tmpdir/basename
	ln -s ${base_bindir}/busybox $tmpdir/echo
	ln -s ${base_bindir}/busybox $tmpdir/mv
	ln -s ${base_bindir}/busybox $tmpdir/ln
	ln -s ${base_bindir}/busybox $tmpdir/dirname
	ln -s ${base_bindir}/busybox $tmpdir/rm
	ln -s ${base_bindir}/busybox $tmpdir/sed
	ln -s ${base_bindir}/busybox $tmpdir/sort
	ln -s ${base_bindir}/busybox $tmpdir/grep
	ln -s ${base_bindir}/busybox $tmpdir/tail
	export PATH=$PATH:$tmpdir
}

pkg_postrm_${PN} () {
	if grep -q "^${base_bindir}/bash$" $D${sysconfdir}/busybox.links* && [ ! -e $D${base_bindir}/bash ]; then
		printf "$(grep -v "^${base_bindir}/bash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
	fi
}

pkg_prerm_${PN}-syslog () {
	# remove syslog
	if test "x$D" = "x"; then
		if test "$1" = "upgrade" -o "$1" = "remove"; then
			${sysconfdir}/init.d/syslog stop || :
		fi
	fi
}

RPROVIDES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/sh /bin/ash', '', d)}"
