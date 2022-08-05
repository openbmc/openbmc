require systemd.inc

PROVIDES = "udev"

PE = "1"

DEPENDS = "intltool-native gperf-native libcap util-linux python3-jinja2-native"

SECTION = "base/shell"

inherit useradd pkgconfig meson perlnative update-rc.d update-alternatives qemu systemd gettext bash-completion manpages features_check

# As this recipe builds udev, respect systemd being in DISTRO_FEATURES so
# that we don't build both udev and systemd in world builds.
REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI += " \
           file://touchscreen.rules \
           file://00-create-volatile.conf \
           ${@bb.utils.contains('PACKAGECONFIG', 'polkit_hostnamed_fallback', 'file://org.freedesktop.hostname1_no_polkit.conf', '', d)} \
           ${@bb.utils.contains('PACKAGECONFIG', 'polkit_hostnamed_fallback', 'file://00-hostnamed-network-user.conf', '', d)} \
           file://init \
           file://99-default.preset \
           file://systemd-pager.sh \
           file://0001-binfmt-Don-t-install-dependency-links-at-install-tim.patch \
           file://0003-implment-systemd-sysv-install-for-OE.patch \
           file://0001-Move-sysusers.d-sysctl.d-binfmt.d-modules-load.d-to-.patch \
           file://0001-glibc-Remove-include-linux-fs.h-to-resolve-fsconfig_.patch \
           "

# patches needed by musl
SRC_URI:append:libc-musl = " ${SRC_URI_MUSL}"
SRC_URI_MUSL = "\
               file://0003-missing_type.h-add-comparison_fn_t.patch \
               file://0004-add-fallback-parse_printf_format-implementation.patch \
               file://0005-src-basic-missing.h-check-for-missing-strndupa.patch \
               file://0007-don-t-fail-if-GLOB_BRACE-and-GLOB_ALTDIRFUNC-is-not-.patch \
               file://0008-add-missing-FTW_-macros-for-musl.patch \
               file://0010-Use-uintmax_t-for-handling-rlim_t.patch \
               file://0011-test-sizeof.c-Disable-tests-for-missing-typedefs-in-.patch \
               file://0012-don-t-pass-AT_SYMLINK_NOFOLLOW-flag-to-faccessat.patch \
               file://0013-Define-glibc-compatible-basename-for-non-glibc-syste.patch \
               file://0014-Do-not-disable-buffering-when-writing-to-oom_score_a.patch \
               file://0015-distinguish-XSI-compliant-strerror_r-from-GNU-specif.patch \
               file://0018-avoid-redefinition-of-prctl_mm_map-structure.patch \
               file://0022-do-not-disable-buffer-in-writing-files.patch \
               file://0025-Handle-__cpu_mask-usage.patch \
               file://0026-Handle-missing-gshadow.patch \
               file://0028-missing_syscall.h-Define-MIPS-ABI-defines-for-musl.patch \
               file://0001-pass-correct-parameters-to-getdents64.patch \
               file://0002-Add-sys-stat.h-for-S_IFDIR.patch \
               file://0001-Adjust-for-musl-headers.patch \
               "

PAM_PLUGINS = " \
    pam-plugin-unix \
    pam-plugin-loginuid \
    pam-plugin-keyinit \
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl audit efi ldconfig pam selinux smack usrmerge polkit seccomp', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'rfkill', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xkbcommon', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '', 'link-udev-shared', d)} \
    backlight \
    binfmt \
    gshadow \
    hibernate \
    hostnamed \
    idn \
    ima \
    kmod \
    localed \
    logind \
    machined \
    myhostname \
    networkd \
    nss \
    nss-mymachines \
    nss-resolve \
    quotacheck \
    randomseed \
    resolved \
    set-time-epoch \
    sysusers \
    sysvinit \
    timedated \
    timesyncd \
    userdb \
    utmp \
    vconsole \
    wheel-group \
    zstd \
"

PACKAGECONFIG:remove:libc-musl = " \
    gshadow \
    idn \
    localed \
    myhostname \
    nss \
    nss-mymachines \
    nss-resolve \
    sysusers \
    userdb \
    utmp \
"

# https://github.com/seccomp/libseccomp/issues/347
PACKAGECONFIG:remove:mipsarch = "seccomp"

CFLAGS:append:libc-musl = " -D__UAPI_DEF_ETHHDR=0 "

# Some of the dependencies are weak-style recommends - if not available at runtime,
# systemd won't fail but the library-related feature will be skipped with a warning.

# Use the upstream systemd serial-getty@.service and rely on
# systemd-getty-generator instead of using the OE-core specific
# systemd-serialgetty.bb - not enabled by default.
PACKAGECONFIG[serial-getty-generator] = ""

PACKAGECONFIG[acl] = "-Dacl=true,-Dacl=false,acl"
PACKAGECONFIG[audit] = "-Daudit=true,-Daudit=false,audit"
PACKAGECONFIG[backlight] = "-Dbacklight=true,-Dbacklight=false"
PACKAGECONFIG[binfmt] = "-Dbinfmt=true,-Dbinfmt=false"
PACKAGECONFIG[bzip2] = "-Dbzip2=true,-Dbzip2=false,bzip2"
PACKAGECONFIG[cgroupv2] = "-Ddefault-hierarchy=unified,-Ddefault-hierarchy=hybrid"
PACKAGECONFIG[coredump] = "-Dcoredump=true,-Dcoredump=false"
PACKAGECONFIG[cryptsetup] = "-Dlibcryptsetup=true,-Dlibcryptsetup=false,cryptsetup,,cryptsetup"
PACKAGECONFIG[tpm2] = "-Dtpm2=true,-Dtpm2=false,tpm2-tss,tpm2-tss libtss2 libtss2-tcti-device"
PACKAGECONFIG[dbus] = "-Ddbus=true,-Ddbus=false,dbus"
PACKAGECONFIG[efi] = "-Defi=true,-Defi=false"
PACKAGECONFIG[gnu-efi] = "-Dgnu-efi=true -Defi-libdir=${STAGING_LIBDIR} -Defi-includedir=${STAGING_INCDIR}/efi,-Dgnu-efi=false,gnu-efi"
PACKAGECONFIG[elfutils] = "-Delfutils=true,-Delfutils=false,elfutils"
PACKAGECONFIG[firstboot] = "-Dfirstboot=true,-Dfirstboot=false"
PACKAGECONFIG[repart] = "-Drepart=true,-Drepart=false"
PACKAGECONFIG[homed] = "-Dhomed=true,-Dhomed=false"
# Sign the journal for anti-tampering
PACKAGECONFIG[gcrypt] = "-Dgcrypt=true,-Dgcrypt=false,libgcrypt"
PACKAGECONFIG[gnutls] = "-Dgnutls=true,-Dgnutls=false,gnutls"
PACKAGECONFIG[gshadow] = "-Dgshadow=true,-Dgshadow=false"
PACKAGECONFIG[hibernate] = "-Dhibernate=true,-Dhibernate=false"
PACKAGECONFIG[hostnamed] = "-Dhostnamed=true,-Dhostnamed=false"
PACKAGECONFIG[idn] = "-Didn=true,-Didn=false"
PACKAGECONFIG[ima] = "-Dima=true,-Dima=false"
# importd requires journal-upload/xz/zlib/bzip2/gcrypt
PACKAGECONFIG[importd] = "-Dimportd=true,-Dimportd=false"
# Update NAT firewall rules
PACKAGECONFIG[iptc] = "-Dlibiptc=true,-Dlibiptc=false,iptables"
PACKAGECONFIG[journal-upload] = "-Dlibcurl=true,-Dlibcurl=false,curl"
PACKAGECONFIG[kmod] = "-Dkmod=true,-Dkmod=false,kmod"
PACKAGECONFIG[ldconfig] = "-Dldconfig=true,-Dldconfig=false,,ldconfig"
PACKAGECONFIG[libidn] = "-Dlibidn=true,-Dlibidn=false,libidn,,libidn"
PACKAGECONFIG[libidn2] = "-Dlibidn2=true,-Dlibidn2=false,libidn2,,libidn2"
# Link udev shared with systemd helper library.
# If enabled the udev package depends on the systemd package (which has the needed shared library).
PACKAGECONFIG[link-udev-shared] = "-Dlink-udev-shared=true,-Dlink-udev-shared=false"
PACKAGECONFIG[localed] = "-Dlocaled=true,-Dlocaled=false"
PACKAGECONFIG[logind] = "-Dlogind=true,-Dlogind=false"
PACKAGECONFIG[lz4] = "-Dlz4=true,-Dlz4=false,lz4"
PACKAGECONFIG[machined] = "-Dmachined=true,-Dmachined=false"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[microhttpd] = "-Dmicrohttpd=true,-Dmicrohttpd=false,libmicrohttpd"
PACKAGECONFIG[myhostname] = "-Dnss-myhostname=true,-Dnss-myhostname=false,,libnss-myhostname"
PACKAGECONFIG[networkd] = "-Dnetworkd=true,-Dnetworkd=false"
PACKAGECONFIG[nss] = "-Dnss-systemd=true,-Dnss-systemd=false"
PACKAGECONFIG[nss-mymachines] = "-Dnss-mymachines=true,-Dnss-mymachines=false"
PACKAGECONFIG[nss-resolve] = "-Dnss-resolve=true,-Dnss-resolve=false"
PACKAGECONFIG[oomd] = "-Doomd=true,-Doomd=false"
PACKAGECONFIG[openssl] = "-Dopenssl=true,-Dopenssl=false,openssl"
PACKAGECONFIG[pam] = "-Dpam=true,-Dpam=false,libpam,${PAM_PLUGINS}"
PACKAGECONFIG[pcre2] = "-Dpcre2=true,-Dpcre2=false,libpcre2"
PACKAGECONFIG[polkit] = "-Dpolkit=true,-Dpolkit=false"
# If polkit is disabled and networkd+hostnamed are in use, enabling this option and
# using dbus-broker will allow networkd to be authorized to change the
# hostname without acquiring additional privileges
PACKAGECONFIG[polkit_hostnamed_fallback] = ",,,,dbus-broker,polkit"
PACKAGECONFIG[portabled] = "-Dportabled=true,-Dportabled=false"
PACKAGECONFIG[qrencode] = "-Dqrencode=true,-Dqrencode=false,qrencode,,qrencode"
PACKAGECONFIG[quotacheck] = "-Dquotacheck=true,-Dquotacheck=false"
PACKAGECONFIG[randomseed] = "-Drandomseed=true,-Drandomseed=false"
PACKAGECONFIG[resolved] = "-Dresolve=true,-Dresolve=false"
PACKAGECONFIG[rfkill] = "-Drfkill=true,-Drfkill=false"
PACKAGECONFIG[seccomp] = "-Dseccomp=true,-Dseccomp=false,libseccomp"
PACKAGECONFIG[selinux] = "-Dselinux=true,-Dselinux=false,libselinux,initscripts-sushell"
PACKAGECONFIG[smack] = "-Dsmack=true,-Dsmack=false"
PACKAGECONFIG[sysext] = "-Dsysext=true, -Dsysext=false"
PACKAGECONFIG[sysusers] = "-Dsysusers=true,-Dsysusers=false"
PACKAGECONFIG[sysvinit] = "-Dsysvinit-path=${sysconfdir}/init.d -Dsysvrcnd-path=${sysconfdir},-Dsysvinit-path= -Dsysvrcnd-path=,,systemd-compat-units update-rc.d"
# When enabled use reproducble build timestamp if set as time epoch,
# or build time if not. When disabled, time epoch is unset.
def build_epoch(d):
    epoch = d.getVar('SOURCE_DATE_EPOCH') or "-1"
    return '-Dtime-epoch=%d' % int(epoch)
PACKAGECONFIG[set-time-epoch] = "${@build_epoch(d)},-Dtime-epoch=0"
PACKAGECONFIG[timedated] = "-Dtimedated=true,-Dtimedated=false"
PACKAGECONFIG[timesyncd] = "-Dtimesyncd=true,-Dtimesyncd=false"
PACKAGECONFIG[usrmerge] = "-Dsplit-usr=false,-Dsplit-usr=true"
PACKAGECONFIG[sbinmerge] = "-Dsplit-bin=false,-Dsplit-bin=true"
PACKAGECONFIG[userdb] = "-Duserdb=true,-Duserdb=false"
PACKAGECONFIG[utmp] = "-Dutmp=true,-Dutmp=false"
PACKAGECONFIG[valgrind] = "-DVALGRIND=1,,valgrind"
PACKAGECONFIG[vconsole] = "-Dvconsole=true,-Dvconsole=false,,${PN}-vconsole-setup"
PACKAGECONFIG[wheel-group] = "-Dwheel-group=true, -Dwheel-group=false"
PACKAGECONFIG[xdg-autostart] = "-Dxdg-autostart=true,-Dxdg-autostart=false"
# Verify keymaps on locale change
PACKAGECONFIG[xkbcommon] = "-Dxkbcommon=true,-Dxkbcommon=false,libxkbcommon"
PACKAGECONFIG[xz] = "-Dxz=true,-Dxz=false,xz"
PACKAGECONFIG[zlib] = "-Dzlib=true,-Dzlib=false,zlib"
PACKAGECONFIG[zstd] = "-Dzstd=true,-Dzstd=false,zstd"

# Helper variables to clarify locations.  This mirrors the logic in systemd's
# build system.
rootprefix ?= "${root_prefix}"
rootlibdir ?= "${base_libdir}"
rootlibexecdir = "${rootprefix}/lib"

EXTRA_OEMESON += "-Dnobody-user=nobody \
                  -Dnobody-group=nobody \
                  -Drootlibdir=${rootlibdir} \
                  -Drootprefix=${rootprefix} \
                  -Ddefault-locale=C \
                  -Dmode=release \
                  -Dsystem-alloc-uid-min=101 \
                  -Dsystem-uid-max=999 \
                  -Dsystem-alloc-gid-min=101 \
                  -Dsystem-gid-max=999 \
                  "

# Hardcode target binary paths to avoid using paths from sysroot
EXTRA_OEMESON += "-Dkexec-path=${sbindir}/kexec \
                  -Dkmod-path=${base_bindir}/kmod \
                  -Dmount-path=${base_bindir}/mount \
                  -Dquotacheck-path=${sbindir}/quotacheck \
                  -Dquotaon-path=${sbindir}/quotaon \
                  -Dsulogin-path=${base_sbindir}/sulogin \
                  -Dnologin-path=${base_sbindir}/nologin \
                  -Dumount-path=${base_bindir}/umount"

# The 60 seconds is watchdog's default vaule.
WATCHDOG_TIMEOUT ??= "60"

do_install() {
	meson_do_install
	install -d ${D}/${base_sbindir}
	if ${@bb.utils.contains('PACKAGECONFIG', 'serial-getty-generator', 'false', 'true', d)}; then
		# Provided by a separate recipe
		rm ${D}${systemd_system_unitdir}/serial-getty* -f
	fi

	# Provide support for initramfs
	[ ! -e ${D}/init ] && ln -s ${rootlibexecdir}/systemd/systemd ${D}/init
	[ ! -e ${D}/${base_sbindir}/udevd ] && ln -s ${rootlibexecdir}/systemd/systemd-udevd ${D}/${base_sbindir}/udevd

	install -d ${D}${sysconfdir}/udev/rules.d/
	install -d ${D}${sysconfdir}/tmpfiles.d
	for rule in $(find ${WORKDIR} -maxdepth 1 -type f -name "*.rules"); do
		install -m 0644 $rule ${D}${sysconfdir}/udev/rules.d/
	done

	install -m 0644 ${WORKDIR}/00-create-volatile.conf ${D}${sysconfdir}/tmpfiles.d/

	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/systemd-udevd
		sed -i s%@UDEVD@%${rootlibexecdir}/systemd/systemd-udevd% ${D}${sysconfdir}/init.d/systemd-udevd
		install -Dm 0755 ${S}/src/systemctl/systemd-sysv-install.SKELETON ${D}${systemd_unitdir}/systemd-sysv-install
	fi

	if "${@'true' if oe.types.boolean(d.getVar('VOLATILE_LOG_DIR')) else 'false'}"; then
		# /var/log is typically a symbolic link to inside /var/volatile,
		# which is expected to be empty.
		rm -rf ${D}${localstatedir}/log
	else
		chown root:systemd-journal ${D}${localstatedir}/log/journal

		# journal-remote creates this at start
		rm -rf ${D}${localstatedir}/log/journal/remote
	fi

	install -d ${D}${systemd_system_unitdir}/graphical.target.wants
	install -d ${D}${systemd_system_unitdir}/multi-user.target.wants
	install -d ${D}${systemd_system_unitdir}/poweroff.target.wants
	install -d ${D}${systemd_system_unitdir}/reboot.target.wants
	install -d ${D}${systemd_system_unitdir}/rescue.target.wants

	# Create symlinks for systemd-update-utmp-runlevel.service
	if ${@bb.utils.contains('PACKAGECONFIG', 'utmp', 'true', 'false', d)}; then
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_system_unitdir}/graphical.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_system_unitdir}/multi-user.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_system_unitdir}/poweroff.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_system_unitdir}/reboot.target.wants/systemd-update-utmp-runlevel.service
		ln -sf ../systemd-update-utmp-runlevel.service ${D}${systemd_system_unitdir}/rescue.target.wants/systemd-update-utmp-runlevel.service
	fi

	# this file is needed to exist if networkd is disabled but timesyncd is still in use since timesyncd checks it
	# for existence else it fails
	if [ -s ${D}${exec_prefix}/lib/tmpfiles.d/systemd.conf ] &&
	   ! ${@bb.utils.contains('PACKAGECONFIG', 'networkd', 'true', 'false', d)}; then
		echo 'd /run/systemd/netif/links 0755 root root -' >>${D}${exec_prefix}/lib/tmpfiles.d/systemd.conf
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
	if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'false', 'true', d)}; then
		rm ${D}${exec_prefix}/lib/tmpfiles.d/x11.conf
		rm -r ${D}${sysconfdir}/X11
	fi

	# If polkit is setup fixup permissions and ownership
	if ${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'true', 'false', d)}; then
		if [ -d ${D}${datadir}/polkit-1/rules.d ]; then
			chmod 700 ${D}${datadir}/polkit-1/rules.d
			chown polkitd:root ${D}${datadir}/polkit-1/rules.d
		fi
	fi

	# If polkit is not available and a fallback was requested, install a drop-in that allows networkd to
	# request hostname changes via DBUS without elevating its privileges
	if ${@bb.utils.contains('PACKAGECONFIG', 'polkit_hostnamed_fallback', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}/systemd-hostnamed.service.d/
		install -m 0644 ${WORKDIR}/00-hostnamed-network-user.conf ${D}${systemd_system_unitdir}/systemd-hostnamed.service.d/
		install -d ${D}${datadir}/dbus-1/system.d/
		install -m 0644 ${WORKDIR}/org.freedesktop.hostname1_no_polkit.conf ${D}${datadir}/dbus-1/system.d/
	fi

	# create link for existing udev rules
	ln -s ${base_bindir}/udevadm ${D}${base_sbindir}/udevadm

	# install default policy for presets
	# https://www.freedesktop.org/wiki/Software/systemd/Preset/#howto
	install -Dm 0644 ${WORKDIR}/99-default.preset ${D}${systemd_unitdir}/system-preset/99-default.preset

	# add a profile fragment to disable systemd pager with busybox less
	install -Dm 0644 ${WORKDIR}/systemd-pager.sh ${D}${sysconfdir}/profile.d/systemd-pager.sh

    if [ -n "${WATCHDOG_TIMEOUT}" ]; then
        sed -i -e 's/#RebootWatchdogSec=10min/RebootWatchdogSec=${WATCHDOG_TIMEOUT}/' \
            ${D}/${sysconfdir}/systemd/system.conf
    fi
}

python populate_packages:prepend (){
    systemdlibdir = d.getVar("rootlibdir")
    do_split_packages(d, systemdlibdir, r'^lib(.*)\.so\.*', 'lib%s', 'Systemd %s library', extra_depends='', allow_links=True)
}
PACKAGES_DYNAMIC += "^lib(udev|systemd|nss).*"

PACKAGE_BEFORE_PN = "\
    ${PN}-gui \
    ${PN}-vconsole-setup \
    ${PN}-initramfs \
    ${PN}-analyze \
    ${PN}-kernel-install \
    ${PN}-rpm-macros \
    ${PN}-binfmt \
    ${PN}-zsh-completion \
    ${PN}-container \
    ${PN}-journal-gatewayd \
    ${PN}-journal-upload \
    ${PN}-journal-remote \
    ${PN}-extra-utils \
    ${PN}-udev-rules \
    libsystemd-shared \
    udev \
    udev-hwdb \
"

SUMMARY:${PN}-container = "Tools for containers and VMs"
DESCRIPTION:${PN}-container = "Systemd tools to spawn and manage containers and virtual machines."

SUMMARY:${PN}-journal-gatewayd = "HTTP server for journal events"
DESCRIPTION:${PN}-journal-gatewayd = "systemd-journal-gatewayd serves journal events over the network. Clients must connect using HTTP. The server listens on port 19531 by default."

SUMMARY:${PN}-journal-upload = "Send journal messages over the network"
DESCRIPTION:${PN}-journal-upload = "systemd-journal-upload uploads journal entries to a specified URL."

SUMMARY:${PN}-journal-remote = "Receive journal messages over the network"
DESCRIPTION:${PN}-journal-remote = "systemd-journal-remote is a command to receive serialized journal events and store them to journal files."

SUMMARY:libsystemd-shared = "Systemd shared library"

SYSTEMD_PACKAGES = "${@bb.utils.contains('PACKAGECONFIG', 'binfmt', '${PN}-binfmt', '', d)} \
                    ${@bb.utils.contains('PACKAGECONFIG', 'microhttpd', '${PN}-journal-gatewayd', '', d)} \
                    ${@bb.utils.contains('PACKAGECONFIG', 'microhttpd', '${PN}-journal-remote', '', d)} \
                    ${@bb.utils.contains('PACKAGECONFIG', 'journal-upload', '${PN}-journal-upload', '', d)} \
"
SYSTEMD_SERVICE:${PN}-binfmt = "systemd-binfmt.service"

USERADD_PACKAGES = "${PN} ${PN}-extra-utils \
                    ${@bb.utils.contains('PACKAGECONFIG', 'microhttpd', '${PN}-journal-gatewayd', '', d)} \
                    ${@bb.utils.contains('PACKAGECONFIG', 'microhttpd', '${PN}-journal-remote', '', d)} \
                    ${@bb.utils.contains('PACKAGECONFIG', 'journal-upload', '${PN}-journal-upload', '', d)} \
"
GROUPADD_PARAM:${PN} = "-r systemd-journal;"
GROUPADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'polkit_hostnamed_fallback', '-r systemd-hostname;', '', d)}"
USERADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'coredump', '--system -d / -M --shell /sbin/nologin systemd-coredump;', '', d)}"
USERADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'networkd', '--system -d / -M --shell /sbin/nologin systemd-network;', '', d)}"
USERADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'polkit', '--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 polkitd;', '', d)}"
USERADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'resolved', '--system -d / -M --shell /sbin/nologin systemd-resolve;', '', d)}"
USERADD_PARAM:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'timesyncd', '--system -d / -M --shell /sbin/nologin systemd-timesync;', '', d)}"
USERADD_PARAM:${PN}-extra-utils = "--system -d / -M --shell /sbin/nologin systemd-bus-proxy"
USERADD_PARAM:${PN}-journal-gatewayd = "--system -d / -M --shell /sbin/nologin systemd-journal-gateway"
USERADD_PARAM:${PN}-journal-remote = "--system -d / -M --shell /sbin/nologin systemd-journal-remote"
USERADD_PARAM:${PN}-journal-upload = "--system -d / -M --shell /sbin/nologin systemd-journal-upload"

FILES:${PN}-analyze = "${bindir}/systemd-analyze"

FILES:${PN}-initramfs = "/init"
RDEPENDS:${PN}-initramfs = "${PN}"

FILES:${PN}-gui = "${bindir}/systemadm"

FILES:${PN}-vconsole-setup = "${rootlibexecdir}/systemd/systemd-vconsole-setup \
                              ${systemd_system_unitdir}/systemd-vconsole-setup.service \
                              ${systemd_system_unitdir}/sysinit.target.wants/systemd-vconsole-setup.service"

RDEPENDS:${PN}-kernel-install += "bash"
FILES:${PN}-kernel-install = "${bindir}/kernel-install \
                              ${sysconfdir}/kernel/ \
                              ${exec_prefix}/lib/kernel \
                             "
FILES:${PN}-rpm-macros = "${exec_prefix}/lib/rpm \
                         "

FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"

FILES:${PN}-binfmt = "${sysconfdir}/binfmt.d/ \
                      ${exec_prefix}/lib/binfmt.d \
                      ${rootlibexecdir}/systemd/systemd-binfmt \
                      ${systemd_system_unitdir}/proc-sys-fs-binfmt_misc.* \
                      ${systemd_system_unitdir}/systemd-binfmt.service"
RRECOMMENDS:${PN}-binfmt = "kernel-module-binfmt-misc"

RRECOMMENDS:${PN}-vconsole-setup = "kbd kbd-consolefonts kbd-keymaps"


FILES:${PN}-journal-gatewayd = "${rootlibexecdir}/systemd/systemd-journal-gatewayd \
                                ${systemd_system_unitdir}/systemd-journal-gatewayd.service \
                                ${systemd_system_unitdir}/systemd-journal-gatewayd.socket \
                                ${systemd_system_unitdir}/sockets.target.wants/systemd-journal-gatewayd.socket \
                                ${datadir}/systemd/gatewayd/browse.html \
                               "
SYSTEMD_SERVICE:${PN}-journal-gatewayd = "systemd-journal-gatewayd.socket"

FILES:${PN}-journal-upload = "${rootlibexecdir}/systemd/systemd-journal-upload \
                              ${systemd_system_unitdir}/systemd-journal-upload.service \
                              ${sysconfdir}/systemd/journal-upload.conf \
                             "
SYSTEMD_SERVICE:${PN}-journal-upload = "systemd-journal-upload.service"

FILES:${PN}-journal-remote = "${rootlibexecdir}/systemd/systemd-journal-remote \
                              ${sysconfdir}/systemd/journal-remote.conf \
                              ${systemd_system_unitdir}/systemd-journal-remote.service \
                              ${systemd_system_unitdir}/systemd-journal-remote.socket \
                             "
SYSTEMD_SERVICE:${PN}-journal-remote = "systemd-journal-remote.socket"


FILES:${PN}-container = "${sysconfdir}/dbus-1/system.d/org.freedesktop.import1.conf \
                         ${sysconfdir}/dbus-1/system.d/org.freedesktop.machine1.conf \
                         ${sysconfdir}/systemd/system/multi-user.target.wants/machines.target \
                         ${base_bindir}/machinectl \
                         ${bindir}/systemd-nspawn \
                         ${nonarch_libdir}/systemd/import-pubring.gpg \
                         ${systemd_system_unitdir}/busnames.target.wants/org.freedesktop.import1.busname \
                         ${systemd_system_unitdir}/busnames.target.wants/org.freedesktop.machine1.busname \
                         ${systemd_system_unitdir}/local-fs.target.wants/var-lib-machines.mount \
                         ${systemd_system_unitdir}/machines.target.wants/var-lib-machines.mount \
                         ${systemd_system_unitdir}/remote-fs.target.wants/var-lib-machines.mount \
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
                         ${rootlibexecdir}/systemd/systemd-machined \
                         ${rootlibexecdir}/systemd/systemd-pull \
                         ${exec_prefix}/lib/tmpfiles.d/systemd-nspawn.conf \
                         ${exec_prefix}/lib/tmpfiles.d/README \
                         ${systemd_system_unitdir}/systemd-nspawn@.service \
                         ${libdir}/libnss_mymachines.so.2 \
                         ${datadir}/dbus-1/system-services/org.freedesktop.import1.service \
                         ${datadir}/dbus-1/system-services/org.freedesktop.machine1.service \
                         ${datadir}/dbus-1/system.d/org.freedesktop.import1.conf \
                         ${datadir}/dbus-1/system.d/org.freedesktop.machine1.conf \
                         ${datadir}/polkit-1/actions/org.freedesktop.import1.policy \
                         ${datadir}/polkit-1/actions/org.freedesktop.machine1.policy \
                        "

# "machinectl import-tar" uses "tar --numeric-owner", not supported by busybox.
RRECOMMENDS:${PN}-container += "\
                         ${PN}-journal-gatewayd \
                         ${PN}-journal-remote \
                         ${PN}-journal-upload \
                         kernel-module-dm-mod \
                         kernel-module-loop \
                         kernel-module-tun \
                         tar \
                        "

FILES:${PN}-extra-utils = "\
                        ${base_bindir}/systemd-escape \
                        ${base_bindir}/systemd-inhibit \
                        ${bindir}/systemd-detect-virt \
                        ${bindir}/systemd-dissect \
                        ${bindir}/systemd-path \
                        ${bindir}/systemd-run \
                        ${bindir}/systemd-cat \
                        ${bindir}/systemd-delta \
                        ${bindir}/systemd-cgls \
                        ${bindir}/systemd-cgtop \
                        ${bindir}/systemd-stdio-bridge \
                        ${base_bindir}/systemd-ask-password \
                        ${base_bindir}/systemd-tty-ask-password-agent \
                        ${systemd_system_unitdir}/systemd-ask-password-console.path \
                        ${systemd_system_unitdir}/systemd-ask-password-console.service \
                        ${systemd_system_unitdir}/systemd-ask-password-wall.path \
                        ${systemd_system_unitdir}/systemd-ask-password-wall.service \
                        ${systemd_system_unitdir}/sysinit.target.wants/systemd-ask-password-console.path \
                        ${systemd_system_unitdir}/sysinit.target.wants/systemd-ask-password-wall.path \
                        ${systemd_system_unitdir}/multi-user.target.wants/systemd-ask-password-wall.path \
                        ${rootlibexecdir}/systemd/systemd-resolve-host \
                        ${rootlibexecdir}/systemd/systemd-ac-power \
                        ${rootlibexecdir}/systemd/systemd-activate \
                        ${rootlibexecdir}/systemd/systemd-bus-proxyd \
                        ${systemd_system_unitdir}/systemd-bus-proxyd.service \
                        ${systemd_system_unitdir}/systemd-bus-proxyd.socket \
                        ${rootlibexecdir}/systemd/systemd-socket-proxyd \
                        ${rootlibexecdir}/systemd/systemd-reply-password \
                        ${rootlibexecdir}/systemd/systemd-sleep \
                        ${rootlibexecdir}/systemd/system-sleep \
                        ${systemd_system_unitdir}/systemd-hibernate.service \
                        ${systemd_system_unitdir}/systemd-hybrid-sleep.service \
                        ${systemd_system_unitdir}/systemd-suspend.service \
                        ${systemd_system_unitdir}/sleep.target \
                        ${rootlibexecdir}/systemd/systemd-initctl \
                        ${systemd_system_unitdir}/systemd-initctl.service \
                        ${systemd_system_unitdir}/systemd-initctl.socket \
                        ${systemd_system_unitdir}/sockets.target.wants/systemd-initctl.socket \
                        ${rootlibexecdir}/systemd/system-generators/systemd-gpt-auto-generator \
                        ${rootlibexecdir}/systemd/systemd-cgroups-agent \
"

FILES:${PN}-udev-rules = "\
                        ${rootlibexecdir}/udev/rules.d/70-uaccess.rules \
                        ${rootlibexecdir}/udev/rules.d/71-seat.rules \
                        ${rootlibexecdir}/udev/rules.d/73-seat-late.rules \
                        ${rootlibexecdir}/udev/rules.d/99-systemd.rules \
"

CONFFILES:${PN} = "${sysconfdir}/systemd/coredump.conf \
	${sysconfdir}/systemd/journald.conf \
	${sysconfdir}/systemd/logind.conf \
	${sysconfdir}/systemd/networkd.conf \
	${sysconfdir}/systemd/pstore.conf \
	${sysconfdir}/systemd/resolved.conf \
	${sysconfdir}/systemd/sleep.conf \
	${sysconfdir}/systemd/system.conf \
	${sysconfdir}/systemd/timesyncd.conf \
	${sysconfdir}/systemd/user.conf \
"

FILES:${PN} = " ${base_bindir}/* \
                ${base_sbindir}/shutdown \
                ${base_sbindir}/halt \
                ${base_sbindir}/poweroff \
                ${base_sbindir}/runlevel \
                ${base_sbindir}/telinit \
                ${base_sbindir}/resolvconf \
                ${base_sbindir}/reboot \
                ${base_sbindir}/init \
                ${datadir}/dbus-1/services \
                ${datadir}/dbus-1/system-services \
                ${datadir}/polkit-1 \
                ${datadir}/${BPN} \
                ${datadir}/factory \
                ${sysconfdir}/dbus-1/ \
                ${sysconfdir}/modules-load.d/ \
                ${sysconfdir}/pam.d/ \
                ${sysconfdir}/profile.d/ \
                ${sysconfdir}/sysctl.d/ \
                ${sysconfdir}/systemd/ \
                ${sysconfdir}/tmpfiles.d/ \
                ${sysconfdir}/xdg/ \
                ${sysconfdir}/init.d/README \
                ${sysconfdir}/resolv-conf.systemd \
                ${sysconfdir}/X11/xinit/xinitrc.d/* \
                ${rootlibexecdir}/systemd/* \
                ${libdir}/pam.d \
                ${nonarch_libdir}/pam.d \
                ${systemd_unitdir}/* \
                ${base_libdir}/security/*.so \
                /cgroup \
                ${bindir}/systemd* \
                ${bindir}/busctl \
                ${bindir}/coredumpctl \
                ${bindir}/localectl \
                ${bindir}/hostnamectl \
                ${bindir}/resolvectl \
                ${bindir}/timedatectl \
                ${bindir}/bootctl \
                ${bindir}/oomctl \
                ${exec_prefix}/lib/tmpfiles.d/*.conf \
                ${exec_prefix}/lib/systemd \
                ${exec_prefix}/lib/modules-load.d \
                ${exec_prefix}/lib/sysctl.d \
                ${exec_prefix}/lib/sysusers.d \
                ${exec_prefix}/lib/environment.d \
                ${localstatedir} \
                ${rootlibexecdir}/modprobe.d/systemd.conf \
                ${rootlibexecdir}/modprobe.d/README \
                ${datadir}/dbus-1/system.d/org.freedesktop.timedate1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.locale1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.network1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.resolve1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.systemd1.conf \
                ${@bb.utils.contains('PACKAGECONFIG', 'polkit_hostnamed_fallback', '${datadir}/dbus-1/system.d/org.freedesktop.hostname1_no_polkit.conf', '', d)} \
                ${datadir}/dbus-1/system.d/org.freedesktop.hostname1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.login1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.timesync1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.portable1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.oom1.conf \
                ${datadir}/dbus-1/system.d/org.freedesktop.home1.conf \
               "

FILES:${PN}-dev += "${base_libdir}/security/*.la ${datadir}/dbus-1/interfaces/ ${sysconfdir}/rpm/macros.systemd"

RDEPENDS:${PN} += "kmod dbus util-linux-mount util-linux-umount udev (= ${EXTENDPKGV}) systemd-udev-rules util-linux-agetty util-linux-fsck"
RDEPENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'serial-getty-generator', '', 'systemd-serialgetty', d)}"
RDEPENDS:${PN} += "volatile-binds"

RRECOMMENDS:${PN} += "systemd-extra-utils \
                      udev-hwdb \
                      e2fsprogs-e2fsck \
                      kernel-module-autofs4 kernel-module-unix kernel-module-ipv6 kernel-module-sch-fq-codel \
                      os-release \
                      systemd-conf \
"

INSANE_SKIP:${PN} += "dev-so libdir"
INSANE_SKIP:${PN}-dbg += "libdir"
INSANE_SKIP:${PN}-doc += " libdir"
INSANE_SKIP:libsystemd-shared += "libdir"

FILES:libsystemd-shared = "${rootlibexecdir}/systemd/libsystemd-shared*.so"

RPROVIDES:udev = "hotplug"

RDEPENDS:udev-hwdb += "udev"

FILES:udev += "${base_sbindir}/udevd \
               ${rootlibexecdir}/systemd/network/99-default.link \
               ${rootlibexecdir}/systemd/systemd-udevd \
               ${rootlibexecdir}/udev/accelerometer \
               ${rootlibexecdir}/udev/ata_id \
               ${rootlibexecdir}/udev/cdrom_id \
               ${rootlibexecdir}/udev/collect \
               ${rootlibexecdir}/udev/dmi_memory_id \
               ${rootlibexecdir}/udev/fido_id \
               ${rootlibexecdir}/udev/findkeyboards \
               ${rootlibexecdir}/udev/keyboard-force-release.sh \
               ${rootlibexecdir}/udev/keymap \
               ${rootlibexecdir}/udev/mtd_probe \
               ${rootlibexecdir}/udev/scsi_id \
               ${rootlibexecdir}/udev/v4l_id \
               ${rootlibexecdir}/udev/keymaps \
               ${rootlibexecdir}/udev/rules.d/50-udev-default.rules \
               ${rootlibexecdir}/udev/rules.d/60-autosuspend.rules \
               ${rootlibexecdir}/udev/rules.d/60-autosuspend-chromiumos.rules \
               ${rootlibexecdir}/udev/rules.d/60-block.rules \
               ${rootlibexecdir}/udev/rules.d/60-cdrom_id.rules \
               ${rootlibexecdir}/udev/rules.d/60-drm.rules \
               ${rootlibexecdir}/udev/rules.d/60-evdev.rules \
               ${rootlibexecdir}/udev/rules.d/60-fido-id.rules \
               ${rootlibexecdir}/udev/rules.d/60-input-id.rules \
               ${rootlibexecdir}/udev/rules.d/60-persistent-alsa.rules \
               ${rootlibexecdir}/udev/rules.d/60-persistent-input.rules \
               ${rootlibexecdir}/udev/rules.d/60-persistent-storage.rules \
               ${rootlibexecdir}/udev/rules.d/60-persistent-storage-tape.rules \
               ${rootlibexecdir}/udev/rules.d/60-persistent-v4l.rules \
               ${rootlibexecdir}/udev/rules.d/60-sensor.rules \
               ${rootlibexecdir}/udev/rules.d/60-serial.rules \
               ${rootlibexecdir}/udev/rules.d/61-autosuspend-manual.rules \
               ${rootlibexecdir}/udev/rules.d/64-btrfs.rules \
               ${rootlibexecdir}/udev/rules.d/70-camera.rules \
               ${rootlibexecdir}/udev/rules.d/70-joystick.rules \
               ${rootlibexecdir}/udev/rules.d/70-memory.rules \
               ${rootlibexecdir}/udev/rules.d/70-mouse.rules \
               ${rootlibexecdir}/udev/rules.d/70-power-switch.rules \
               ${rootlibexecdir}/udev/rules.d/70-touchpad.rules \
               ${rootlibexecdir}/udev/rules.d/75-net-description.rules \
               ${rootlibexecdir}/udev/rules.d/75-probe_mtd.rules \
               ${rootlibexecdir}/udev/rules.d/78-sound-card.rules \
               ${rootlibexecdir}/udev/rules.d/80-drivers.rules \
               ${rootlibexecdir}/udev/rules.d/80-net-setup-link.rules \
               ${rootlibexecdir}/udev/rules.d/81-net-dhcp.rules \
               ${rootlibexecdir}/udev/rules.d/90-vconsole.rules \
               ${rootlibexecdir}/udev/rules.d/README \
               ${sysconfdir}/udev \
               ${sysconfdir}/init.d/systemd-udevd \
               ${systemd_system_unitdir}/*udev* \
               ${systemd_system_unitdir}/*.wants/*udev* \
               ${base_bindir}/systemd-hwdb \
               ${base_bindir}/udevadm \
               ${base_sbindir}/udevadm \
               ${datadir}/bash-completion/completions/udevadm \
               ${systemd_system_unitdir}/systemd-hwdb-update.service \
              "

FILES:udev-hwdb = "${rootlibexecdir}/udev/hwdb.d \
                   "

RCONFLICTS:${PN} = "tiny-init ${@bb.utils.contains('PACKAGECONFIG', 'resolved', 'resolvconf', '', d)}"

INITSCRIPT_PACKAGES = "udev"
INITSCRIPT_NAME:udev = "systemd-udevd"
INITSCRIPT_PARAMS:udev = "start 03 S ."

python __anonymous() {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")

    if bb.utils.contains('PACKAGECONFIG', 'repart', True, False, d) and not bb.utils.contains('PACKAGECONFIG', 'openssl', True, False, d):
        bb.error("PACKAGECONFIG[repart] requires PACKAGECONFIG[openssl]")

    if bb.utils.contains('PACKAGECONFIG', 'homed', True, False, d) and not bb.utils.contains('PACKAGECONFIG', 'userdb openssl cryptsetup', True, False, d):
        bb.error("PACKAGECONFIG[homed] requires PACKAGECONFIG[userdb], PACKAGECONFIG[openssl] and PACKAGECONFIG[cryptsetup]")
}

python do_warn_musl() {
    if d.getVar('TCLIBC') == "musl":
        bb.warn("Using systemd with musl is not recommended since it is not supported upstream and some patches are known to be problematic.")
}
addtask warn_musl before do_configure

ALTERNATIVE:${PN} = "halt reboot shutdown poweroff runlevel ${@bb.utils.contains('PACKAGECONFIG', 'resolved', 'resolv-conf', '', d)}"

ALTERNATIVE_TARGET[resolv-conf] = "${sysconfdir}/resolv-conf.systemd"
ALTERNATIVE_LINK_NAME[resolv-conf] = "${sysconfdir}/resolv.conf"
ALTERNATIVE_PRIORITY[resolv-conf] ?= "50"

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

pkg_postinst:${PN}:libc-glibc () {
	sed -e '/^hosts:/s/\s*\<myhostname\>//' \
		-e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 myhostname \3\4\5/' \
		-i $D${sysconfdir}/nsswitch.conf
}

pkg_prerm:${PN}:libc-glibc () {
	sed -e '/^hosts:/s/\s*\<myhostname\>//' \
		-e '/^hosts:/s/\s*myhostname//' \
		-i $D${sysconfdir}/nsswitch.conf
}

PACKAGE_WRITE_DEPS += "qemu-native"
pkg_postinst:udev-hwdb () {
	if test -n "$D"; then
		$INTERCEPT_DIR/postinst_intercept update_udev_hwdb ${PKG} mlprefix=${MLPREFIX} binprefix=${MLPREFIX} rootlibexecdir="${rootlibexecdir}" PREFERRED_PROVIDER_udev="${PREFERRED_PROVIDER_udev}" base_bindir="${base_bindir}"
	else
		udevadm hwdb --update
	fi
}

pkg_prerm:udev-hwdb () {
	rm -f $D${sysconfdir}/udev/hwdb.bin
}
