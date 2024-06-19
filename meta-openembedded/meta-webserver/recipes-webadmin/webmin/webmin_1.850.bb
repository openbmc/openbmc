SUMMARY = "Web-based administration interface"
HOMEPAGE = "http://www.webmin.com"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENCE;md5=0373ac9f611e542ddebe1ec6394afc3c"

SRC_URI = "${SOURCEFORGE_MIRROR}/webadmin/webmin-${PV}.tar.gz \
           file://setup.sh \
           file://init-exclude.patch \
           file://net-generic.patch \
           file://remove-startup-option.patch \
           file://disable-version-check.patch \
           file://nfs-export.patch \
           file://exports-lib.pl.patch \
           file://mount-excludefs.patch \
           file://samba-config-fix.patch \
           file://proftpd-config-fix.patch \
           file://net-lib.pl.patch \
           file://media-tomb.patch \
           file://remove-python2.3.patch \
           file://mysql-config-fix.patch \
           file://webmin.service \
            "

SRC_URI[md5sum] = "cd6ee98f73f9418562197675b952d81b"
SRC_URI[sha256sum] = "c66caa9e4cb50d5447bc8aceb7989d2284dde060278f404b13e171c7ce1690e1"

UPSTREAM_CHECK_URI = "http://www.webmin.com/download.html"
UPSTREAM_CHECK_REGEX = "webmin-(?P<pver>\d+(\.\d+)+).tar.gz"

inherit perlnative update-rc.d systemd

do_configure() {
    # Remove binaries and plugins for other platforms
    rm -rf acl/Authen-SolarisRBAC-0.1*
    rm -rf format bsdexports hpuxexports sgiexports
    rm -rf zones rbac smf ipfw ipfilter dfsadmin
    rm -f mount/freebsd-mounts* mount/netbsd-mounts*
    rm -f mount/openbsd-mounts* mount/macos-mounts*

    # Remove some plugins for the moment
    rm -rf lilo frox wuftpd telnet pserver cpan shorewall webalizer cfengine fsdump pap
    rm -rf majordomo fetchmail sendmail mailboxes procmail filter mailcap dovecot exim spam qmailadmin postfix
    rm -rf stunnel squid sarg pptp-client pptp-server jabber openslp sentry cluster-* vgetty burner heartbeat

    # Adjust configs
    [ -f init/config-debian-linux ] && mv init/config-debian-linux init/config-generic-linux
    sed -i "s/shutdown_command=.*/shutdown_command=poweroff/" init/config-generic-linux
    echo "exclude=bootmisc.sh,single,halt,reboot,hostname.sh,modutils.sh,mountall.sh,mountnfs.sh,networking,populate-volatile.sh,rmnologin.sh,save-rtc.sh,umountfs,umountnfs.sh,hwclock.sh,checkroot.sh,banner.sh,udev,udev-cache,devpts.sh,psplash.sh,sendsigs,fbsetup,bootlogd,stop-bootlogd,sysfs.sh,syslog,syslog.busybox,urandom,webmin,functions.initscripts,read-only-rootfs-hook.sh" >> init/config-generic-linux
    echo "excludefs=devpts,devtmpfs,usbdevfs,proc,tmpfs,sysfs,debugfs" >> mount/config-generic-linux

    [ -f exports/config-debian-linux ] && mv exports/config-debian-linux exports/config-generic-linux
    sed -i "s/killall -HUP rpc.nfsd && //" exports/config-generic-linux
    sed -i "s/netstd_nfs/nfsserver/g" exports/config-generic-linux

    # Fix insane naming that causes problems at packaging time (must be done before deleting below)
    find . -name "*\**" | while read from
    do
        to=`echo "$from" | sed "s/*/ALL/"`
        mv "$from" "$to"
    done

    # Remove some other files we don't need
    find . -name "config-*" -a \! -name "config-generic-linux" -a \! -name "config-ALL-linux" -a \! -name "*.pl" -delete
    find . -regextype posix-extended -regex ".*/(openserver|aix|osf1|osf|openbsd|netbsd|freebsd|unixware|solaris|macos|irix|hpux|cygwin|windows)-lib\.pl" -delete
    rm -f webmin-gentoo-init webmin-caldera-init webmin-debian-pam webmin-pam

    # Don't need these at runtime (and we have our own setup script)
    rm -f setup.sh
    rm -f setup.pl

    # Use pidof for finding PIDs
    sed -i "s/find_pid_command=.*/find_pid_command=pidof NAME/" config-generic-linux
}

WEBMIN_LOGIN ?= "admin"
WEBMIN_PASSWORD ?= "password"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/webmin
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 webmin-init ${D}${sysconfdir}/init.d/webmin

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/webmin.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
           ${D}${systemd_unitdir}/system/webmin.service

    install -d ${D}${localstatedir}
    install -d ${D}${localstatedir}/webmin

    install -d ${D}${libexecdir}/webmin
    cd ${S} || exit 1
    tar --no-same-owner --exclude='./patches' --exclude='./.pc' -cpf - . \
        | tar --no-same-owner -xpf - -C ${D}${libexecdir}/webmin

    rm -f ${D}${libexecdir}/webmin/webmin-init
    rm -f ${D}${libexecdir}/webmin/ajaxterm/ajaxterm/configure.initd.gentoo
    rm -rf ${D}${libexecdir}/webmin/patches

    # Run setup script
    export perl=perl
    export perl_runtime=${bindir}/perl
    export prefix=${D}
    export tempdir=${S}/install_tmp
    export wadir=${libexecdir}/webmin
    export config_dir=${sysconfdir}/webmin
    export var_dir=${localstatedir}/webmin
    export os_type=generic-linux
    export os_version=0
    export real_os_type="${DISTRO_NAME}"
    export real_os_version="${DISTRO_VERSION}"
    export port=10000
    export login=${WEBMIN_LOGIN}
    export password=${WEBMIN_PASSWORD}
    export ssl=0
    export atboot=1
    export no_pam=1
    mkdir -p $tempdir
    ${UNPACKDIR}/setup.sh

    # Ensure correct PERLLIB path
    sed -i -e 's#${D}##g' ${D}${sysconfdir}/webmin/start
}

INITSCRIPT_NAME = "webmin"
INITSCRIPT_PARAMS = "start 99 5 3 2 . stop 10 0 1 6 ."

SYSTEMD_SERVICE:${PN} = "webmin.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

# FIXME: some of this should be figured out automatically
RDEPENDS:${PN} += "perl perl-module-socket perl-module-exporter perl-module-exporter-heavy perl-module-carp perl-module-strict"
RDEPENDS:${PN} += "perl-module-warnings perl-module-xsloader perl-module-posix perl-module-autoloader"
RDEPENDS:${PN} += "perl-module-fcntl perl-module-tie-hash perl-module-vars perl-module-time-local perl-module-config perl-module-constant"
RDEPENDS:${PN} += "perl-module-file-glob perl-module-file-copy perl-module-sdbm-file perl-module-feature"

PACKAGES_DYNAMIC += "webmin-module-* webmin-theme-*"
RRECOMMENDS:${PN} += "webmin-module-system-status"

PACKAGES += "${PN}-module-proc ${PN}-module-raid ${PN}-module-exports ${PN}-module-fdisk ${PN}-module-lvm"
RDEPENDS:${PN}-module-proc = "procps"
RDEPENDS:${PN}-module-raid = "mdadm"
RDEPENDS:${PN}-module-exports = "perl-module-file-basename perl-module-file-path perl-module-cwd perl-module-file-spec perl-module-file-spec-unix"
RRECOMMENDS:${PN}-module-fdisk = "parted"
RRECOMMENDS:${PN}-module-lvm = "lvm2"

python populate_packages:prepend() {
    import os, os.path

    wadir = bb.data.expand('${libexecdir}/webmin', d)
    wadir_image = bb.data.expand('${D}', d) + wadir
    modules = []
    themes = []
    for mod in os.listdir(wadir_image):
        modinfo = os.path.join(wadir_image, mod, "module.info")
        themeinfo = os.path.join(wadir_image, mod, "theme.info")
        if os.path.exists(modinfo):
            modules.append(mod)
        elif os.path.exists(themeinfo):
            themes.append(mod)

    do_split_packages(d, wadir, '^(%s)$' % "|".join(modules), 'webmin-module-%s', 'Webmin module for %s', extra_depends='perl', allow_dirs=True, prepend=True)
    do_split_packages(d, wadir, '^(%s)$' % "|".join(themes), 'webmin-theme-%s', 'Webmin theme for %s', extra_depends='perl', allow_dirs=True, prepend=True)
}

# Time-savers
package_do_pkgconfig() {
    :
}
