require systemd.inc

PROVIDES = "udev"

PE = "1"

DEPENDS = "kmod intltool-native gperf-native acl readline libcap libcgroup util-linux"

SECTION = "base/shell"

inherit useradd pkgconfig meson perlnative update-rc.d update-alternatives qemu systemd gettext bash-completion manpages distro_features_check

# As this recipe builds udev, respect systemd being in DISTRO_FEATURES so
# that we don't build both udev and systemd in world builds.
REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI += "file://touchscreen.rules \
           file://00-create-volatile.conf \
           file://init \
           file://0001-Hide-__start_BUS_ERROR_MAP-and-__stop_BUS_ERROR_MAP.patch \
           file://0001-Use-getenv-when-secure-versions-are-not-available.patch \
           file://0002-binfmt-Don-t-install-dependency-links-at-install-tim.patch \
           file://0003-use-lnr-wrapper-instead-of-looking-for-relative-opti.patch \
           file://0004-implment-systemd-sysv-install-for-OE.patch \
           file://0005-rules-whitelist-hd-devices.patch \
           file://0006-Make-root-s-home-directory-configurable.patch \
           file://0007-Revert-rules-remove-firmware-loading-rules.patch \
           file://0008-Revert-udev-remove-userspace-firmware-loading-suppor.patch \
           file://0009-remove-duplicate-include-uchar.h.patch \
           file://0010-check-for-uchar.h-in-meson.build.patch \
           file://0011-socket-util-don-t-fail-if-libc-doesn-t-support-IDN.patch \
           file://0012-rules-watch-metadata-changes-in-ide-devices.patch \
           file://0013-add-fallback-parse_printf_format-implementation.patch \
           file://0014-src-basic-missing.h-check-for-missing-strndupa.patch \
           file://0015-don-t-fail-if-GLOB_BRACE-and-GLOB_ALTDIRFUNC-is-not-.patch \
           file://0016-src-basic-missing.h-check-for-missing-__compar_fn_t-.patch \
           file://0017-Include-netinet-if_ether.h.patch \
           file://0018-check-for-missing-canonicalize_file_name.patch \
           file://0019-Do-not-enable-nss-tests-if-nss-systemd-is-not-enable.patch \
           file://0020-test-hexdecoct.c-Include-missing.h-for-strndupa.patch \
           file://0021-test-sizeof.c-Disable-tests-for-missing-typedefs-in-.patch \
           file://0022-don-t-use-glibc-specific-qsort_r.patch \
           file://0023-don-t-pass-AT_SYMLINK_NOFOLLOW-flag-to-faccessat.patch \
           file://0024-comparison_fn_t-is-glibc-specific-use-raw-signature-.patch \
           file://0025-Define-_PATH_WTMPX-and-_PATH_UTMPX-if-not-defined.patch \
           file://0026-Use-uintmax_t-for-handling-rlim_t.patch \
           file://0027-remove-nobody-user-group-checking.patch \
           file://0028-add-missing-FTW_-macros-for-musl.patch \
           file://0029-nss-mymachines-Build-conditionally-when-ENABLE_MYHOS.patch \
           file://0030-fix-missing-of-__register_atfork-for-non-glibc-build.patch \
           file://0031-fix-missing-ULONG_LONG_MAX-definition-in-case-of-mus.patch \
           file://0032-memfd.patch \
           file://0033-basic-macros-rename-noreturn-into-_noreturn_-8456.patch \
           file://libmount.patch \
           file://0034-Fix-format-truncation-compile-failure-by-typecasting.patch \
           file://0035-Define-glibc-compatible-basename-for-non-glibc-syste.patch \
           "
SRC_URI_append_qemuall = " file://0001-core-device.c-Change-the-default-device-timeout-to-2.patch"

# Workaround undefined reference to `__stack_chk_fail_local' on qemux86 and qemuppc for musl
SRC_URI_append_libc-musl_qemux86 = " file://0001-Remove-fstack-protector-flags-to-workaround-musl-bui.patch"
SRC_URI_append_libc-musl_qemuppc = " file://0001-Remove-fstack-protector-flags-to-workaround-musl-bui.patch"

PAM_PLUGINS = " \
    pam-plugin-unix \
    pam-plugin-loginuid \
    pam-plugin-keyinit \
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'efi ldconfig pam selinux usrmerge', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'rfkill', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xkbcommon', '', d)} \
    backlight \
    binfmt \
    firstboot \
    hibernate \
    hostnamed \
    ima \
    localed \
    logind \
    machined \
    myhostname \
    networkd \
    nss \
    polkit \
    quotacheck \
    randomseed \
    resolved \
    smack \
    sysusers \
    timedated \
    timesyncd \
    utmp \
    vconsole \
    xz \
"

PACKAGECONFIG_remove_libc-musl = " \
    localed \
    myhostname \
    nss \
    resolved \
    selinux \
    smack \
    sysusers \
    utmp \
"

# Use the upstream systemd serial-getty@.service and rely on
# systemd-getty-generator instead of using the OE-core specific
# systemd-serialgetty.bb - not enabled by default.
PACKAGECONFIG[serial-getty-generator] = ""

PACKAGECONFIG[audit] = "-Daudit=true,-Daudit=false,audit"
PACKAGECONFIG[backlight] = "-Dbacklight=true,-Dbacklight=false"
PACKAGECONFIG[binfmt] = "-Dbinfmt=true,-Dbinfmt=false"
PACKAGECONFIG[bzip2] = "-Dbzip2=true,-Dbzip2=false,bzip2"
PACKAGECONFIG[coredump] = "-Dcoredump=true,-Dcoredump=false"
PACKAGECONFIG[cryptsetup] = "-Dlibcryptsetup=true,-Dlibcryptsetup=false,cryptsetup"
PACKAGECONFIG[dbus] = "-Ddbus=true,-Ddbus=false,dbus"
PACKAGECONFIG[efi] = "-Defi=true,-Defi=false"
PACKAGECONFIG[elfutils] = "-Delfutils=true,-Delfutils=false,elfutils"
PACKAGECONFIG[firstboot] = "-Dfirstboot=true,-Dfirstboot=false"
# Sign the journal for anti-tampering
PACKAGECONFIG[gcrypt] = "-Dgcrypt=true,-Dgcrypt=false,libgcrypt"
PACKAGECONFIG[hibernate] = "-Dhibernate=true,-Dhibernate=false"
PACKAGECONFIG[hostnamed] = "-Dhostnamed=true,-Dhostnamed=false"
PACKAGECONFIG[ima] = "-Dima=true,-Dima=false"
# importd requires curl/xz/zlib/bzip2/gcrypt
PACKAGECONFIG[importd] = "-Dimportd=true,-Dimportd=false"
# Update NAT firewall rules
PACKAGECONFIG[iptc] = "-Dlibiptc=true,-Dlibiptc=false,iptables"
PACKAGECONFIG[journal-upload] = "-Dlibcurl=true,-Dlibcurl=false,curl"
PACKAGECONFIG[ldconfig] = "-Dldconfig=true,-Dldconfig=false"
PACKAGECONFIG[libidn] = "-Dlibidn=true,-Dlibidn=false,libidn"
PACKAGECONFIG[localed] = "-Dlocaled=true,-Dlocaled=false"
PACKAGECONFIG[logind] = "-Dlogind=true,-Dlogind=false"
PACKAGECONFIG[lz4] = "-Dlz4=true,-Dlz4=false,lz4"
PACKAGECONFIG[machined] = "-Dmachined=true,-Dmachined=false"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[microhttpd] = "-Dmicrohttpd=true,-Dmicrohttpd=false,libmicrohttpd"
PACKAGECONFIG[myhostname] = "-Dmyhostname=true,-Dmyhostname=false"
PACKAGECONFIG[networkd] = "-Dnetworkd=true,-Dnetworkd=false"
PACKAGECONFIG[nss] = "-Dnss-systemd=true,-Dnss-systemd=false"
PACKAGECONFIG[pam] = "-Dpam=true,-Dpam=false,libpam,${PAM_PLUGINS}"
PACKAGECONFIG[polkit] = "-Dpolkit=true,-Dpolkit=false"
PACKAGECONFIG[qrencode] = "-Dqrencode=true,-Dqrencode=false,qrencode"
PACKAGECONFIG[quotacheck] = "-Dquotacheck=true,-Dquotacheck=false"
PACKAGECONFIG[randomseed] = "-Drandomseed=true,-Drandomseed=false"
PACKAGECONFIG[resolved] = "-Dresolve=true,-Dresolve=false"
PACKAGECONFIG[rfkill] = "-Drfkill=true,-Drfkill=false"
# libseccomp is found in meta-security
PACKAGECONFIG[seccomp] = "-Dseccomp=true,-Dseccomp=false,libseccomp"
PACKAGECONFIG[selinux] = "-Dselinux=true,-Dselinux=false,libselinux,initscripts-sushell"
PACKAGECONFIG[smack] = "-Dsmack=true,-Dsmack=false"
PACKAGECONFIG[sysusers] = "-Dsysusers=true,-Dsysusers=false"
PACKAGECONFIG[timedated] = "-Dtimedated=true,-Dtimedated=false"
PACKAGECONFIG[timesyncd] = "-Dtimesyncd=true,-Dtimesyncd=false"
PACKAGECONFIG[usrmerge] = "-Dsplit-usr=false,-Dsplit-usr=true"
PACKAGECONFIG[utmp] = "-Dutmp=true,-Dutmp=false"
PACKAGECONFIG[valgrind] = "-DVALGRIND=1,,valgrind"
PACKAGECONFIG[vconsole] = "-Dvconsole=true,-Dvconsole=false,,${PN}-vconsole-setup"
# Verify keymaps on locale change
PACKAGECONFIG[xkbcommon] = "-Dxkbcommon=true,-Dxkbcommon=false,libxkbcommon"
PACKAGECONFIG[xz] = "-Dxz=true,-Dxz=false,xz"
PACKAGECONFIG[zlib] = "-Dzlib=true,-Dzlib=false,zlib"

# Helper variables to clarify locations.  This mirrors the logic in systemd's
# build system.
rootprefix ?= "${root_prefix}"
rootlibdir ?= "${base_libdir}"
rootlibexecdir = "${rootprefix}/lib"

# This links udev statically with systemd helper library.
# Otherwise udev package would depend on systemd package (which has the needed shared library),
# and always pull it into images.
EXTRA_OEMESON += "-Dlink-udev-shared=false"

EXTRA_OEMESON += "-Dnobody-user=nobody \
                  -Dnobody-group=nobody \
                  -Droothomedir=${ROOTHOME} \
                  -Drootlibdir=${rootlibdir} \
                  -Drootprefix=${rootprefix} \
                  -Dsysvrcnd-path=${sysconfdir} \
                  -Dfirmware-path=${nonarch_base_libdir}/firmware \
                  "

# Hardcode target binary paths to avoid using paths from sysroot
EXTRA_OEMESON += "-Dkexec-path=${sbindir}/kexec \
                  -Dkill-path=${base_bindir}/kill \
                  -Dkmod-path=${base_bindir}/kmod \
                  -Dmount-path=${base_bindir}/mount \
                  -Dquotacheck-path=${sbindir}/quotacheck \
                  -Dquotaon-path=${sbindir}/quotaon \
                  -Dsulogin-path=${base_sbindir}/sulogin \
                  -Dumount-path=${base_bindir}/umount"

do_install() {
	meson_do_install
	install -d ${D}/${base_sbindir}
	if ${@bb.utils.contains('PACKAGECONFIG', 'serial-getty-generator', 'false', 'true', d)}; then
		# Provided by a separate recipe
		rm ${D}${systemd_unitdir}/system/serial-getty* -f
	fi

	# Provide support for initramfs
	[ ! -e ${D}/init ] && ln -s ${rootlibexecdir}/systemd/systemd ${D}/init
	[ ! -e ${D}/${base_sbindir}/udevd ] && ln -s ${rootlibexecdir}/systemd/systemd-udevd ${D}/${base_sbindir}/udevd

	# Create machine-id
	# 20:12 < mezcalero> koen: you have three options: a) run systemd-machine-id-setup at install time, b) have / read-only and an empty file there (for stateless) and c) boot with / writable
	touch ${D}${sysconfdir}/machine-id

	install -d ${D}${sysconfdir}/udev/rules.d/
	install -d ${D}${sysconfdir}/tmpfiles.d
	install -m 0644 ${WORKDIR}/*.rules ${D}${sysconfdir}/udev/rules.d/
	install -d ${D}${libdir}/pkgconfig
	install -m 0644 ${B}/src/udev/udev.pc ${D}${libdir}/pkgconfig/

	install -m 0644 ${WORKDIR}/00-create-volatile.conf ${D}${sysconfdir}/tmpfiles.d/

	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/systemd-udevd
		sed -i s%@UDEVD@%${rootlibexecdir}/systemd/systemd-udevd% ${D}${sysconfdir}/init.d/systemd-udevd
	fi

	chown root:systemd-journal ${D}/${localstatedir}/log/journal

	# Delete journal README, as log can be symlinked inside volatile.
	rm -f ${D}/${localstatedir}/log/README

	install -d ${D}${systemd_unitdir}/system/graphical.target.wants
	install -d ${D}${systemd_unitdir}/system/multi-user.target.wants
	install -d ${D}${systemd_unitdir}/system/poweroff.target.wants
	install -d ${D}${systemd_unitdir}/system/reboot.target.wants
	install -d ${D}${systemd_unitdir}/system/rescue.target.wants

	# Create symlinks for systemd-update-utmp-runlevel.service
	if ${@bb.utils.contains('PACKAGECONFIG', 'utmp', 'true', 'false', d)}; then
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_unitdir}/system/graphical.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_unitdir}/system/multi-user.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_unitdir}/system/poweroff.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_unitdir}/system/reboot.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_unitdir}/system/rescue.target.wants/systemd-update-utmp-runlevel.service
	fi

	# Enable journal to forward message to syslog daemon
	sed -i -e 's/.*ForwardToSyslog.*/ForwardToSyslog=yes/' ${D}${sysconfdir}/systemd/journald.conf
	# Set the maximium size of runtime journal to 64M as default
	sed -i -e 's/.*RuntimeMaxUse.*/RuntimeMaxUse=64M/' ${D}${sysconfdir}/systemd/journald.conf

	# this file is needed to exist if networkd is disabled but timesyncd is still in use since timesyncd checks it
	# for existence else it fails
	if [ -s ${D}${exec_prefix}/lib/tmpfiles.d/systemd.conf ]; then
		${@bb.utils.contains('PACKAGECONFIG', 'networkd', ':', 'sed -i -e "\$ad /run/systemd/netif/links 0755 root root -" ${D}${exec_prefix}/lib/tmpfiles.d/systemd.conf', d)}
	fi
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'resolved', 'true', 'false', d)}; then
		echo 'L! ${sysconfdir}/resolv.conf - - - - ../run/systemd/resolve/resolv.conf' >>${D}${exec_prefix}/lib/tmpfiles.d/etc.conf
		echo 'd /run/systemd/resolve 0755 root root -' >>${D}${exec_prefix}/lib/tmpfiles.d/systemd.conf
		echo 'f /run/systemd/resolve/resolv.conf 0644 root root' >>${D}${exec_prefix}/lib/tmpfiles.d/systemd.conf
		ln -s ../run/systemd/resolve/resolv.conf ${D}${sysconfdir}/resolv-conf.systemd
	else
		sed -i -e "s%^L! /etc/resolv.conf.*$%L! /etc/resolv.conf - - - - ../run/systemd/resolve/resolv.conf%g" ${D}${exec_prefix}/lib/tmpfiles.d/etc.conf
		ln -s ../run/systemd/resolve/resolv.conf ${D}${sysconfdir}/resolv-conf.systemd
	fi
	install -Dm 0755 ${S}/src/systemctl/systemd-sysv-install.SKELETON ${D}${systemd_unitdir}/systemd-sysv-install

	# If polkit is setup fixup permissions and ownership
	if ${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'true', 'false', d)}; then
		if [ -d ${D}${datadir}/polkit-1/rules.d ]; then
			chmod 700 ${D}${datadir}/polkit-1/rules.d
			chown polkitd:root ${D}${datadir}/polkit-1/rules.d
		fi
	fi
}


python populate_packages_prepend (){
    systemdlibdir = d.getVar("rootlibdir")
    do_split_packages(d, systemdlibdir, '^lib(.*)\.so\.*', 'lib%s', 'Systemd %s library', extra_depends='', allow_links=True)
}
PACKAGES_DYNAMIC += "^lib(udev|systemd|nss).*"

PACKAGES =+ "\
    ${PN}-gui \
    ${PN}-vconsole-setup \
    ${PN}-initramfs \
    ${PN}-analyze \
    ${PN}-kernel-install \
    ${PN}-rpm-macros \
    ${PN}-binfmt \
    ${PN}-zsh-completion \
    ${PN}-xorg-xinitrc \
    ${PN}-container \
    ${PN}-extra-utils \
"

SUMMARY_${PN}-container = "Tools for containers and VMs"
DESCRIPTION_${PN}-container = "Systemd tools to spawn and manage containers and virtual machines."

SYSTEMD_PACKAGES = "${@bb.utils.contains('PACKAGECONFIG', 'binfmt', '${PN}-binfmt', '', d)}"
SYSTEMD_SERVICE_${PN}-binfmt = "systemd-binfmt.service"

USERADD_PACKAGES = "${PN} ${PN}-extra-utils"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'microhttpd', '--system -d / -M --shell /bin/nologin systemd-journal-gateway;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'microhttpd', '--system -d / -M --shell /bin/nologin systemd-journal-remote;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'journal-upload', '--system -d / -M --shell /bin/nologin systemd-journal-upload;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'timesyncd', '--system -d / -M --shell /bin/nologin systemd-timesync;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'networkd', '--system -d / -M --shell /bin/nologin systemd-network;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'coredump', '--system -d / -M --shell /bin/nologin systemd-coredump;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'resolved', '--system -d / -M --shell /bin/nologin systemd-resolve;', '', d)}"
USERADD_PARAM_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'polkit', '--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 polkitd;', '', d)}"
GROUPADD_PARAM_${PN} = "-r systemd-journal"
USERADD_PARAM_${PN}-extra-utils += "--system -d / -M --shell /bin/nologin systemd-bus-proxy;"

FILES_${PN}-analyze = "${bindir}/systemd-analyze"

FILES_${PN}-initramfs = "/init"
RDEPENDS_${PN}-initramfs = "${PN}"

FILES_${PN}-gui = "${bindir}/systemadm"

FILES_${PN}-vconsole-setup = "${rootlibexecdir}/systemd/systemd-vconsole-setup \
                              ${systemd_unitdir}/system/systemd-vconsole-setup.service \
                              ${systemd_unitdir}/system/sysinit.target.wants/systemd-vconsole-setup.service"

RDEPENDS_${PN}-kernel-install += "bash"
FILES_${PN}-kernel-install = "${bindir}/kernel-install \
                              ${sysconfdir}/kernel/ \
                              ${exec_prefix}/lib/kernel \
                             "
FILES_${PN}-rpm-macros = "${exec_prefix}/lib/rpm \
                         "

FILES_${PN}-xorg-xinitrc = "${sysconfdir}/X11/xinit/xinitrc.d/*"

FILES_${PN}-zsh-completion = "${datadir}/zsh/site-functions"

FILES_${PN}-binfmt = "${sysconfdir}/binfmt.d/ \
                      ${exec_prefix}/lib/binfmt.d \
                      ${rootlibexecdir}/systemd/systemd-binfmt \
                      ${systemd_unitdir}/system/proc-sys-fs-binfmt_misc.* \
                      ${systemd_unitdir}/system/systemd-binfmt.service"
RRECOMMENDS_${PN}-binfmt = "kernel-module-binfmt-misc"

RRECOMMENDS_${PN}-vconsole-setup = "kbd kbd-consolefonts kbd-keymaps"

FILES_${PN}-container = "${sysconfdir}/dbus-1/system.d/org.freedesktop.import1.conf \
                         ${sysconfdir}/dbus-1/system.d/org.freedesktop.machine1.conf \
                         ${base_bindir}/machinectl \
                         ${bindir}/systemd-nspawn \
                         ${nonarch_libdir}/systemd/import-pubring.gpg \
                         ${systemd_system_unitdir}/busnames.target.wants/org.freedesktop.import1.busname \
                         ${systemd_system_unitdir}/busnames.target.wants/org.freedesktop.machine1.busname \
                         ${systemd_system_unitdir}/local-fs.target.wants/var-lib-machines.mount \
                         ${systemd_system_unitdir}/machine.slice \
                         ${systemd_system_unitdir}/machines.target \
                         ${systemd_system_unitdir}/org.freedesktop.import1.busname \
                         ${systemd_system_unitdir}/org.freedesktop.machine1.busname \
                         ${systemd_system_unitdir}/systemd-importd.service \
                         ${systemd_system_unitdir}/systemd-machined.service \
                         ${systemd_system_unitdir}/dbus-org.freedesktop.machine1.service \
                         ${systemd_system_unitdir}/var-lib-machines.mount \
                         ${rootlibexecdir}/systemd/systemd-import \
                         ${rootlibexecdir}/systemd/systemd-importd \
                         ${rootlibexecdir}/systemd/systemd-journal-gatewayd \
                         ${rootlibexecdir}/systemd/systemd-journal-remote \
                         ${rootlibexecdir}/systemd/systemd-journal-upload \
                         ${rootlibexecdir}/systemd/systemd-machined \
                         ${rootlibexecdir}/systemd/systemd-pull \
                         ${exec_prefix}/lib/tmpfiles.d/systemd-nspawn.conf \
                         ${systemd_system_unitdir}/systemd-nspawn@.service \
                         ${libdir}/libnss_mymachines.so.2 \
                         ${datadir}/dbus-1/system-services/org.freedesktop.import1.service \
                         ${datadir}/dbus-1/system-services/org.freedesktop.machine1.service \
                         ${datadir}/dbus-1/system.d/org.freedesktop.machine1.conf \
                         ${datadir}/polkit-1/actions/org.freedesktop.import1.policy \
                         ${datadir}/polkit-1/actions/org.freedesktop.machine1.policy \
                        "

FILES_${PN}-extra-utils = "\
                        ${base_bindir}/systemd-escape \
                        ${base_bindir}/systemd-inhibit \
                        ${bindir}/systemd-detect-virt \
                        ${bindir}/systemd-path \
                        ${bindir}/systemd-run \
                        ${bindir}/systemd-cat \
                        ${bindir}/systemd-delta \
                        ${bindir}/systemd-cgls \
                        ${bindir}/systemd-cgtop \
                        ${bindir}/systemd-stdio-bridge \
                        ${base_bindir}/systemd-ask-password \
                        ${base_bindir}/systemd-tty-ask-password-agent \
                        ${systemd_unitdir}/system/systemd-ask-password-console.path \
                        ${systemd_unitdir}/system/systemd-ask-password-console.service \
                        ${systemd_unitdir}/system/systemd-ask-password-wall.path \
                        ${systemd_unitdir}/system/systemd-ask-password-wall.service \
                        ${systemd_unitdir}/system/sysinit.target.wants/systemd-ask-password-console.path \
                        ${systemd_unitdir}/system/sysinit.target.wants/systemd-ask-password-wall.path \
                        ${systemd_unitdir}/system/multi-user.target.wants/systemd-ask-password-wall.path \
                        ${rootlibexecdir}/systemd/systemd-resolve-host \
                        ${rootlibexecdir}/systemd/systemd-ac-power \
                        ${rootlibexecdir}/systemd/systemd-activate \
                        ${rootlibexecdir}/systemd/systemd-bus-proxyd \
                        ${systemd_unitdir}/system/systemd-bus-proxyd.service \
                        ${systemd_unitdir}/system/systemd-bus-proxyd.socket \
                        ${rootlibexecdir}/systemd/systemd-socket-proxyd \
                        ${rootlibexecdir}/systemd/systemd-reply-password \
                        ${rootlibexecdir}/systemd/systemd-sleep \
                        ${rootlibexecdir}/systemd/system-sleep \
                        ${systemd_unitdir}/system/systemd-hibernate.service \
                        ${systemd_unitdir}/system/systemd-hybrid-sleep.service \
                        ${systemd_unitdir}/system/systemd-suspend.service \
                        ${systemd_unitdir}/system/sleep.target \
                        ${rootlibexecdir}/systemd/systemd-initctl \
                        ${systemd_unitdir}/system/systemd-initctl.service \
                        ${systemd_unitdir}/system/systemd-initctl.socket \
                        ${systemd_unitdir}/system/sockets.target.wants/systemd-initctl.socket \
                        ${rootlibexecdir}/systemd/system-generators/systemd-gpt-auto-generator \
                        ${rootlibexecdir}/systemd/systemd-cgroups-agent \
"

CONFFILES_${PN} = "${sysconfdir}/machine-id \
                ${sysconfdir}/systemd/coredump.conf \
                ${sysconfdir}/systemd/journald.conf \
                ${sysconfdir}/systemd/logind.conf \
                ${sysconfdir}/systemd/system.conf \
                ${sysconfdir}/systemd/user.conf"

FILES_${PN} = " ${base_bindir}/* \
                ${datadir}/dbus-1/services \
                ${datadir}/dbus-1/system-services \
                ${datadir}/polkit-1 \
                ${datadir}/${BPN} \
                ${datadir}/factory \
                ${sysconfdir}/dbus-1/ \
                ${sysconfdir}/machine-id \
                ${sysconfdir}/modules-load.d/ \
                ${sysconfdir}/pam.d/ \
                ${sysconfdir}/sysctl.d/ \
                ${sysconfdir}/systemd/ \
                ${sysconfdir}/tmpfiles.d/ \
                ${sysconfdir}/xdg/ \
                ${sysconfdir}/init.d/README \
                ${sysconfdir}/resolv-conf.systemd \
                ${rootlibexecdir}/systemd/* \
                ${systemd_unitdir}/* \
                ${base_libdir}/security/*.so \
                /cgroup \
                ${bindir}/systemd* \
                ${bindir}/busctl \
                ${bindir}/coredumpctl \
                ${bindir}/localectl \
                ${bindir}/hostnamectl \
                ${bindir}/timedatectl \
                ${bindir}/bootctl \
                ${bindir}/kernel-install \
                ${exec_prefix}/lib/tmpfiles.d/*.conf \
                ${exec_prefix}/lib/systemd \
                ${exec_prefix}/lib/modules-load.d \
                ${exec_prefix}/lib/sysctl.d \
                ${exec_prefix}/lib/sysusers.d \
                ${exec_prefix}/lib/environment.d \
                ${localstatedir} \
                ${nonarch_base_libdir}/udev/rules.d/70-uaccess.rules \
                ${nonarch_base_libdir}/udev/rules.d/71-seat.rules \
                ${nonarch_base_libdir}/udev/rules.d/73-seat-late.rules \
                ${nonarch_base_libdir}/udev/rules.d/99-systemd.rules \
                ${nonarch_base_libdir}/modprobe.d/systemd.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.timedate1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.locale1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.network1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.resolve1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.systemd1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.hostname1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.login1.conf \
               "

FILES_${PN}-dev += "${base_libdir}/security/*.la ${datadir}/dbus-1/interfaces/ ${sysconfdir}/rpm/macros.systemd"

RDEPENDS_${PN} += "kmod dbus util-linux-mount udev (= ${EXTENDPKGV}) util-linux-agetty"
RDEPENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'serial-getty-generator', '', 'systemd-serialgetty', d)}"
RDEPENDS_${PN} += "volatile-binds update-rc.d"

RRECOMMENDS_${PN} += "systemd-extra-utils \
                      systemd-compat-units udev-hwdb \
                      util-linux-fsck e2fsprogs-e2fsck \
                      kernel-module-autofs4 kernel-module-unix kernel-module-ipv6 \
                      os-release \
"

INSANE_SKIP_${PN} += "dev-so libdir"
INSANE_SKIP_${PN}-dbg += "libdir"
INSANE_SKIP_${PN}-doc += " libdir"

PACKAGES =+ "udev udev-hwdb"

RPROVIDES_udev = "hotplug"

RDEPENDS_udev-hwdb += "udev"

FILES_udev += "${base_sbindir}/udevd \
               ${rootlibexecdir}/systemd/systemd-udevd \
               ${rootlibexecdir}/udev/accelerometer \
               ${rootlibexecdir}/udev/ata_id \
               ${rootlibexecdir}/udev/cdrom_id \
               ${rootlibexecdir}/udev/collect \
               ${rootlibexecdir}/udev/findkeyboards \
               ${rootlibexecdir}/udev/keyboard-force-release.sh \
               ${rootlibexecdir}/udev/keymap \
               ${rootlibexecdir}/udev/mtd_probe \
               ${rootlibexecdir}/udev/scsi_id \
               ${rootlibexecdir}/udev/v4l_id \
               ${rootlibexecdir}/udev/keymaps \
               ${rootlibexecdir}/udev/rules.d/*.rules \
               ${sysconfdir}/udev \
               ${sysconfdir}/init.d/systemd-udevd \
               ${systemd_unitdir}/system/*udev* \
               ${systemd_unitdir}/system/*.wants/*udev* \
               ${base_bindir}/udevadm \
               ${datadir}/bash-completion/completions/udevadm \
              "

FILES_udev-hwdb = "${rootlibexecdir}/udev/hwdb.d"

INITSCRIPT_PACKAGES = "udev"
INITSCRIPT_NAME_udev = "systemd-udevd"
INITSCRIPT_PARAMS_udev = "start 03 S ."

python __anonymous() {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

# TODO:
# u-a for runlevel and telinit

ALTERNATIVE_${PN} = "init halt reboot shutdown poweroff runlevel resolv-conf"

ALTERNATIVE_TARGET[init] = "${rootlibexecdir}/systemd/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"

ALTERNATIVE_TARGET[halt] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[halt] = "${base_sbindir}/halt"
ALTERNATIVE_PRIORITY[halt] ?= "300"

ALTERNATIVE_TARGET[reboot] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[reboot] = "${base_sbindir}/reboot"
ALTERNATIVE_PRIORITY[reboot] ?= "300"

ALTERNATIVE_TARGET[shutdown] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[shutdown] = "${base_sbindir}/shutdown"
ALTERNATIVE_PRIORITY[shutdown] ?= "300"

ALTERNATIVE_TARGET[poweroff] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[poweroff] = "${base_sbindir}/poweroff"
ALTERNATIVE_PRIORITY[poweroff] ?= "300"

ALTERNATIVE_TARGET[runlevel] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[runlevel] = "${base_sbindir}/runlevel"
ALTERNATIVE_PRIORITY[runlevel] ?= "300"

ALTERNATIVE_TARGET[resolv-conf] = "${sysconfdir}/resolv-conf.systemd"
ALTERNATIVE_LINK_NAME[resolv-conf] = "${sysconfdir}/resolv.conf"
ALTERNATIVE_PRIORITY[resolv-conf] ?= "50"

pkg_postinst_${PN} () {
	sed -e '/^hosts:/s/\s*\<myhostname\>//' \
		-e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 myhostname \3\4\5/' \
		-i $D${sysconfdir}/nsswitch.conf
}

pkg_prerm_${PN} () {
	sed -e '/^hosts:/s/\s*\<myhostname\>//' \
		-e '/^hosts:/s/\s*myhostname//' \
		-i $D${sysconfdir}/nsswitch.conf
}

PACKAGE_WRITE_DEPS += "qemu-native"
pkg_postinst_udev-hwdb () {
	if test -n "$D"; then
		${@qemu_run_binary(d, '$D', '${base_bindir}/udevadm')} hwdb --update \
			--root $D
		chown root:root $D${sysconfdir}/udev/hwdb.bin
	else
		udevadm hwdb --update
	fi
}

pkg_prerm_udev-hwdb () {
	rm -f $D${sysconfdir}/udev/hwdb.bin
}
