require systemd.inc

PROVIDES = "udev"

PE = "1"

DEPENDS = "kmod intltool-native gperf-native acl readline libcap libcgroup util-linux"

SECTION = "base/shell"

inherit useradd pkgconfig autotools perlnative update-rc.d update-alternatives qemu systemd ptest gettext bash-completion manpages

SRC_URI = "git://github.com/systemd/systemd.git;protocol=git \
           file://touchscreen.rules \
           file://00-create-volatile.conf \
           file://init \
           file://run-ptest \
           file://0004-Use-getenv-when-secure-versions-are-not-available.patch \
           file://0005-binfmt-Don-t-install-dependency-links-at-install-tim.patch \
           file://0007-use-lnr-wrapper-instead-of-looking-for-relative-opti.patch \
           file://0010-implment-systemd-sysv-install-for-OE.patch \
           file://0011-nss-mymachines-Build-conditionally-when-HAVE_MYHOSTN.patch \
           file://0012-rules-whitelist-hd-devices.patch \
           file://0013-Make-root-s-home-directory-configurable.patch \
           file://0014-Revert-rules-remove-firmware-loading-rules.patch \
           file://0015-Revert-udev-remove-userspace-firmware-loading-suppor.patch \
           file://0017-remove-duplicate-include-uchar.h.patch \
           file://0018-check-for-uchar.h-in-configure.patch \
           file://0019-socket-util-don-t-fail-if-libc-doesn-t-support-IDN.patch \
           file://0001-add-fallback-parse_printf_format-implementation.patch \
           file://0002-src-basic-missing.h-check-for-missing-strndupa.patch \
           file://0003-don-t-fail-if-GLOB_BRACE-and-GLOB_ALTDIRFUNC-is-not-.patch \
           file://0004-src-basic-missing.h-check-for-missing-__compar_fn_t-.patch \
           file://0006-Include-netinet-if_ether.h.patch \
           file://0007-check-for-missing-canonicalize_file_name.patch \
           file://0008-Do-not-enable-nss-tests.patch \
           file://0009-test-hexdecoct.c-Include-missing.h-form-strndupa.patch \
           file://0010-test-sizeof.c-Disable-tests-for-missing-typedefs-in-.patch \
           file://0011-don-t-use-glibc-specific-qsort_r.patch \
           file://0012-don-t-pass-AT_SYMLINK_NOFOLLOW-flag-to-faccessat.patch \
           file://0013-comparison_fn_t-is-glibc-specific-use-raw-signature-.patch \
           file://0001-Define-_PATH_WTMPX-and-_PATH_UTMPX-if-not-defined.patch \
           file://0001-Use-uintmax_t-for-handling-rlim_t.patch \
           file://0001-core-evaluate-presets-after-generators-have-run-6526.patch \
           file://0001-main-skip-many-initialization-steps-when-running-in-.patch \
           "
SRC_URI_append_qemuall = " file://0001-core-device.c-Change-the-default-device-timeout-to-2.patch"

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

PACKAGECONFIG[audit] = "--enable-audit,--disable-audit,audit"
PACKAGECONFIG[backlight] = "--enable-backlight,--disable-backlight"
PACKAGECONFIG[binfmt] = "--enable-binfmt,--disable-binfmt"
PACKAGECONFIG[bzip2] = "--enable-bzip2,--disable-bzip2,bzip2"
PACKAGECONFIG[coredump] = "--enable-coredump,--disable-coredump"
PACKAGECONFIG[cryptsetup] = "--enable-libcryptsetup,--disable-libcryptsetup,cryptsetup"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[efi] = "--enable-efi,--disable-efi"
PACKAGECONFIG[elfutils] = "--enable-elfutils,--disable-elfutils,elfutils"
PACKAGECONFIG[firstboot] = "--enable-firstboot,--disable-firstboot"
# Sign the journal for anti-tampering
PACKAGECONFIG[gcrypt] = "--enable-gcrypt,--disable-gcrypt,libgcrypt"
PACKAGECONFIG[hibernate] = "--enable-hibernate,--disable-hibernate"
PACKAGECONFIG[hostnamed] = "--enable-hostnamed,--disable-hostnamed"
PACKAGECONFIG[ima] = "--enable-ima,--disable-ima"
# importd requires curl/xz/zlib/bzip2/gcrypt
PACKAGECONFIG[importd] = "--enable-importd,--disable-importd"
# Update NAT firewall rules
PACKAGECONFIG[iptc] = "--enable-libiptc,--disable-libiptc,iptables"
PACKAGECONFIG[journal-upload] = "--enable-libcurl,--disable-libcurl,curl"
PACKAGECONFIG[ldconfig] = "--enable-ldconfig,--disable-ldconfig"
PACKAGECONFIG[libidn] = "--enable-libidn,--disable-libidn,libidn"
PACKAGECONFIG[localed] = "--enable-localed,--disable-localed"
PACKAGECONFIG[logind] = "--enable-logind,--disable-logind"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"
PACKAGECONFIG[machined] = "--enable-machined,--disable-machined"
PACKAGECONFIG[manpages] = "--enable-manpages,--disable-manpages,libxslt-native xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[microhttpd] = "--enable-microhttpd,--disable-microhttpd,libmicrohttpd"
PACKAGECONFIG[myhostname] = "--enable-myhostname,--disable-myhostname"
PACKAGECONFIG[networkd] = "--enable-networkd,--disable-networkd"
PACKAGECONFIG[nss] = "--enable-nss-systemd,--disable-nss-systemd"
PACKAGECONFIG[pam] = "--enable-pam,--disable-pam,libpam,${PAM_PLUGINS}"
PACKAGECONFIG[polkit] = "--enable-polkit,--disable-polkit"
PACKAGECONFIG[qrencode] = "--enable-qrencode,--disable-qrencode,qrencode"
PACKAGECONFIG[quotacheck] = "--enable-quotacheck,--disable-quotacheck"
PACKAGECONFIG[randomseed] = "--enable-randomseed,--disable-randomseed"
PACKAGECONFIG[resolved] = "--enable-resolved,--disable-resolved"
PACKAGECONFIG[rfkill] = "--enable-rfkill,--disable-rfkill"
# libseccomp is found in meta-security
PACKAGECONFIG[seccomp] = "--enable-seccomp,--disable-seccomp,libseccomp"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,initscripts-sushell"
PACKAGECONFIG[smack] = "--enable-smack,--disable-smack"
PACKAGECONFIG[sysusers] = "--enable-sysusers,--disable-sysusers"
PACKAGECONFIG[timedated] = "--enable-timedated,--disable-timedated"
PACKAGECONFIG[timesyncd] = "--enable-timesyncd,--disable-timesyncd"
PACKAGECONFIG[usrmerge] = "--disable-split-usr,--enable-split-usr"
PACKAGECONFIG[utmp] = "--enable-utmp,--disable-utmp"
PACKAGECONFIG[valgrind] = "ac_cv_header_valgrind_memcheck_h=yes ac_cv_header_valgrind_valgrind_h=yes,ac_cv_header_valgrind_memcheck_h=no ac_cv_header_valgrind_valgrind_h=no,valgrind"
PACKAGECONFIG[vconsole] = "--enable-vconsole,--disable-vconsole,,${PN}-vconsole-setup"
# Verify keymaps on locale change
PACKAGECONFIG[xkbcommon] = "--enable-xkbcommon,--disable-xkbcommon,libxkbcommon"
PACKAGECONFIG[xz] = "--enable-xz,--disable-xz,xz"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib"

# Hardcode target binary paths to avoid AC_PROG_PATH in the systemd
# configure script detecting and setting paths from sysroot or host.
CACHED_CONFIGUREVARS_class-target = " \
    ac_cv_path_KEXEC=${sbindir}/kexec \
    ac_cv_path_KILL=${base_bindir}/kill \
    ac_cv_path_KMOD=${base_bindir}/kmod \
    ac_cv_path_MOUNT_PATH=${base_bindir}/mount \
    ac_cv_path_QUOTACHECK=${sbindir}/quotacheck \
    ac_cv_path_QUOTAON=${sbindir}/quotaon \
    ac_cv_path_SULOGIN=${base_sbindir}/sulogin \
    ac_cv_path_UMOUNT_PATH=${base_bindir}/umount \
"

# Helper variables to clarify locations.  This mirrors the logic in systemd's
# build system.
rootprefix ?= "${root_prefix}"
rootlibdir ?= "${base_libdir}"
rootlibexecdir = "${rootprefix}/lib"

EXTRA_OECONF = " \
    --without-python \
    --with-roothomedir=${ROOT_HOME} \
    --with-rootlibdir=${rootlibdir} \
    --with-rootprefix=${rootprefix} \
    --with-sysvrcnd-path=${sysconfdir} \
    --with-firmware-path=${nonarch_base_libdir}/firmware \
"

# per the systemd README, define VALGRIND=1 to run under valgrind
CFLAGS .= "${@bb.utils.contains('PACKAGECONFIG', 'valgrind', ' -DVALGRIND=1', '', d)}"

# disable problematic GCC 5.2 optimizations [YOCTO #8291]
FULL_OPTIMIZATION_append_arm = " -fno-schedule-insns -fno-schedule-insns2"

COMPILER_NM ?= "${HOST_PREFIX}gcc-nm"
COMPILER_AR ?= "${HOST_PREFIX}gcc-ar"
COMPILER_RANLIB ?= "${HOST_PREFIX}gcc-ranlib"

do_configure_prepend() {
	export NM="${COMPILER_NM}"
	export AR="${COMPILER_AR}"
	export RANLIB="${COMPILER_RANLIB}"
	export KMOD="${base_bindir}/kmod"
	if [ -d ${S}/units.pre_sed ] ; then
		cp -r ${S}/units.pre_sed ${S}/units
	else
		cp -r ${S}/units ${S}/units.pre_sed
	fi
	sed -i -e 's:-DTEST_DIR=\\\".*\\\":-DTEST_DIR=\\\"${PTEST_PATH}/tests/test\\\":' ${S}/Makefile.am
	sed -i -e 's:-DCATALOG_DIR=\\\".*\\\":-DCATALOG_DIR=\\\"${PTEST_PATH}/tests/catalog\\\":' ${S}/Makefile.am
}

do_install() {
	autotools_do_install
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

do_install_ptest () {
	# install data files needed for tests
	install -d ${D}${PTEST_PATH}/tests/test
	cp -rfL ${S}/test/* ${D}${PTEST_PATH}/tests/test
	# python is disabled for systemd, thus removing these python testing scripts
	rm ${D}${PTEST_PATH}/tests/test/*.py
	sed -i 's/"tree"/"ls"/' ${D}${PTEST_PATH}/tests/test/udev-test.pl

	install -d ${D}${PTEST_PATH}/tests/catalog
	install ${S}/catalog/* ${D}${PTEST_PATH}/tests/catalog/

	install -D ${S}/build-aux/test-driver ${D}${PTEST_PATH}/tests/build-aux/test-driver

	install -d ${D}${PTEST_PATH}/tests/rules
	install ${B}/rules/* ${D}${PTEST_PATH}/tests/rules/

	# This directory needs to be there for udev-test.pl to work.
	install -d ${D}${libdir}/udev/rules.d

	# install actual test binaries
	install -m 0755 ${B}/test-* ${D}${PTEST_PATH}/tests/
	install -m 0755 ${B}/.libs/test-* ${D}${PTEST_PATH}/tests/

	install ${B}/Makefile ${D}${PTEST_PATH}/tests/
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
GROUPADD_PARAM_${PN} = "-r lock; -r systemd-journal"
USERADD_PARAM_${PN}-extra-utils += "--system -d / -M --shell /bin/nologin systemd-bus-proxy;"

FILES_${PN}-analyze = "${bindir}/systemd-analyze"

FILES_${PN}-initramfs = "/init"
RDEPENDS_${PN}-initramfs = "${PN}"

RDEPENDS_${PN}-ptest += "gawk make perl bash xz \
                         tzdata tzdata-americas tzdata-asia \
                         tzdata-europe tzdata-africa tzdata-antarctica \
                         tzdata-arctic tzdata-atlantic tzdata-australia \
                         tzdata-pacific tzdata-posix"

FILES_${PN}-ptest += "${libdir}/udev/rules.d"

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

# As this recipe builds udev, respect systemd being in DISTRO_FEATURES so
# that we don't build both udev and systemd in world builds.
python () {
    if not bb.utils.contains ('DISTRO_FEATURES', 'systemd', True, False, d):
        raise bb.parse.SkipPackage("'systemd' not in DISTRO_FEATURES")
}
