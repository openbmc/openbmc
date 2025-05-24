require util-linux.inc

inherit autotools gettext manpages pkgconfig systemd update-alternatives python3-dir bash-completion ptest gtk-doc
DEPENDS = "libcap-ng ncurses virtual/crypt zlib util-linux-libuuid"

PACKAGES =+ "${PN}-swaponoff"
PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'pylibmount', '${PN}-pylibmount', '', d)}"

python util_linux_binpackages () {
    def pkg_hook(f, pkg, file_regex, output_pattern, modulename):
        pn = d.getVar('PN')
        d.appendVar('RRECOMMENDS:%s' % pn, ' %s' % pkg)

        if d.getVar('ALTERNATIVE:' + pkg):
            return
        if d.getVarFlag('ALTERNATIVE_LINK_NAME', modulename):
            d.setVar('ALTERNATIVE:' + pkg, modulename)

    bindirs = sorted(list(set(d.expand("${base_sbindir} ${base_bindir} ${sbindir} ${bindir}").split())))
    for dir in bindirs:
        do_split_packages(d, root=dir,
                          file_regex=r'(.*)', output_pattern='${PN}-%s',
                          description='${PN} %s',
                          hook=pkg_hook, extra_depends='')

    # There are some symlinks for some binaries which we have ignored
    # above. Add them to the package owning the binary they are
    # pointing to
    extras = {}
    dvar = d.getVar('PKGD')
    for root in bindirs:
        for walkroot, dirs, files in os.walk(dvar + root):
            for f in files:
                file = os.path.join(walkroot, f)
                if not os.path.islink(file):
                    continue

                pkg = os.path.basename(os.readlink(file))
                extras.setdefault(pkg, [])
                extras[pkg].append(file.replace(dvar, '', 1))

    pn = d.getVar('PN')
    for pkg, links in extras.items():
        of = d.getVar('FILES:' + pn + '-' + pkg)
        links = of + " " + " ".join(sorted(links))
        d.setVar('FILES:' + pn + '-' + pkg, links)
}

# we must execute before update-alternatives PACKAGE_PREPROCESS_FUNCS
PACKAGE_PREPROCESS_FUNCS =+ "util_linux_binpackages "

# skip libuuid as it will be packaged by the util-linux-libuuid recipe
python util_linux_libpackages() {
    do_split_packages(d, root=d.getVar('UTIL_LINUX_LIBDIR'), file_regex=r'^lib(?!uuid)(.*)\.so\..*$',
                      output_pattern='${PN}-lib%s',
                      description='${PN} lib%s',
                      extra_depends='', prepend=True, allow_links=True)
}

PACKAGESPLITFUNCS =+ "util_linux_libpackages"

PACKAGES_DYNAMIC = "^${PN}(?!.*-native)-.*"
PACKAGES_DYNAMIC:class-native = "^${BPN}-.*-native"

UTIL_LINUX_LIBDIR = "${libdir}"
UTIL_LINUX_LIBDIR:class-target = "${base_libdir}"
EXTRA_OECONF = "\
    --enable-libuuid --enable-libblkid \
    \
    --enable-fsck --enable-kill --enable-last --enable-mesg \
    --enable-mount --enable-partx --enable-rfkill \
    --enable-unshare --enable-write \
    \
    --disable-bfs --disable-login \
    --disable-makeinstall-chown --disable-minix --disable-newgrp \
    --disable-use-tty-group --disable-vipw --disable-raw \
    \
    --without-udev \
    \
    usrsbin_execdir='${sbindir}' \
    --libdir='${UTIL_LINUX_LIBDIR}' \
"

EXTRA_OECONF:append:class-target = " --enable-setpriv"
EXTRA_OECONF:append:class-native = " --without-cap-ng --disable-setpriv"
EXTRA_OECONF:append:class-nativesdk = " --without-cap-ng --disable-setpriv"
EXTRA_OECONF:append = " --disable-hwclock-gplv3"

# enable pcre2 for native/nativesdk to match host distros
# this helps to keep same expectations when using the SDK or
# build host versions during development
#
PACKAGECONFIG ?= "pcre2"
PACKAGECONFIG:class-target ?= "\
    libmount-mountfd-support \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'chfn-chsh pam lastlog2', '', d)} \
"
# inherit manpages requires this to be present, however util-linux does not have
# configuration options, and installs manpages always
PACKAGECONFIG[manpages] = ""
PACKAGECONFIG[pam] = "--enable-su --enable-runuser,--disable-su --disable-runuser, libpam,"
# Respect the systemd feature for uuidd
PACKAGECONFIG[systemd] = "--with-systemd --with-systemdsystemunitdir=${systemd_system_unitdir}, --without-systemd --without-systemdsystemunitdir,systemd"
# Build python bindings for libmount
PACKAGECONFIG[pylibmount] = "--with-python=3 --enable-pylibmount,--without-python --disable-pylibmount,python3"
# Readline support
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
# PCRE support in hardlink
PACKAGECONFIG[pcre2] = ",,libpcre2"
PACKAGECONFIG[cryptsetup] = "--with-cryptsetup,--without-cryptsetup,cryptsetup"
PACKAGECONFIG[chfn-chsh] = "--enable-chfn-chsh,--disable-chfn-chsh,"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"
PACKAGECONFIG[lastlog2] = "--enable-liblastlog2,--disable-liblastlog2,sqlite3"
# Using the new file descriptors based mount kernel API can cause rootfs remount failure with some older kernels.
# Of currently supported LTS kernels, the old mount API should be used with:
# - versions prior to 6.6.18 in the 6.6.y series.
# - versions prior to 6.1.79 in the 6.1.y series.
# - versions till at least 5.15.164 in the 5.15.y series.
# - with 5.10.y, 5.4.y and 4.19.y series kernels, libmount seemed to use the old API regardless of this option.
PACKAGECONFIG[libmount-mountfd-support] = "--enable-libmount-mountfd-support,--disable-libmount-mountfd-support"

EXTRA_OEMAKE = "ARCH=${TARGET_ARCH} CPU= CPUOPT= 'OPT=${CFLAGS}'"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN} = ""
FILES:${PN}-doc += "${datadir}/getopt/getopt-*.*"
FILES:${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/libmount/pylibmount.la"
FILES:${PN}-mount = "${sysconfdir}/default/mountall"
FILES:${PN}-runuser = "${sysconfdir}/pam.d/runuser*"
FILES:${PN}-su = "${sysconfdir}/pam.d/su-l"
CONFFILES:${PN}-su = "${sysconfdir}/pam.d/su-l"
FILES:${PN}-lastlog2 += "${base_libdir}/security/pam_lastlog2.so \
                         ${nonarch_libdir}/tmpfiles.d/lastlog2.conf \
                         ${sysconfdir}/default/volatiles/99_lastlog2"
FILES:${PN}-pylibmount = "${PYTHON_SITEPACKAGES_DIR}/libmount/pylibmount.so \
                          ${PYTHON_SITEPACKAGES_DIR}/libmount/__init__.* \
                          ${PYTHON_SITEPACKAGES_DIR}/libmount/__pycache__/*"

# Util-linux' blkid replaces the e2fsprogs one
RCONFLICTS:${PN}-blkid = "${MLPREFIX}e2fsprogs-blkid"
RREPLACES:${PN}-blkid = "${MLPREFIX}e2fsprogs-blkid"

RRECOMMENDS:${PN}:class-native = ""
RRECOMMENDS:${PN}:class-nativesdk = ""
RDEPENDS:${PN}:class-native = ""
RDEPENDS:${PN}:class-nativesdk = ""

RDEPENDS:${PN} += " util-linux-libuuid"
RDEPENDS:${PN}-dev += " util-linux-libuuid-dev"

RPROVIDES:${PN}-dev = "${PN}-libblkid-dev ${PN}-libmount-dev"

RDEPENDS:${PN}-bash-completion += "${PN}-lsblk ${PN}-findmnt"
RDEPENDS:${PN}-ptest += "bash bc btrfs-tools coreutils e2fsprogs findutils grep iproute2 kmod procps sed socat which xz diffutils"
RRECOMMENDS:${PN}-ptest += "kernel-module-scsi-debug kernel-module-sd-mod kernel-module-loop kernel-module-algif-hash"
RDEPENDS:${PN}-swaponoff = "${PN}-swapon ${PN}-swapoff"
ALLOW_EMPTY:${PN}-swaponoff = "1"

#SYSTEMD_PACKAGES = "${PN}-uuidd ${PN}-fstrim"
SYSTEMD_SERVICE:${PN}-uuidd = "uuidd.socket uuidd.service"
SYSTEMD_AUTO_ENABLE:${PN}-uuidd = "disable"
SYSTEMD_SERVICE:${PN}-fstrim = "fstrim.timer fstrim.service"
SYSTEMD_AUTO_ENABLE:${PN}-fstrim = "disable"

do_compile:prepend () {
	# this is a workaround for the unnecessary remake problem. Issue and PR are as below:
	# https://github.com/util-linux/util-linux/issues/3061
	# https://github.com/util-linux/util-linux/pull/3064
	# When newly release tarball contains the above fix, the following workaround could be dropped.
	[ -e ${S}/libsmartcols/src/filter-scanner.c ] && touch ${S}/libsmartcols/src/filter-scanner.c
	[ -e ${S}/libsmartcols/src/filter-parser.c ] && touch ${S}/libsmartcols/src/filter-parser.c
}

do_compile:append () {
	cp ${UNPACKDIR}/fcntl-lock.c ${S}/fcntl-lock.c
	${CC} ${CFLAGS} ${LDFLAGS} ${S}/fcntl-lock.c -o ${B}/fcntl-lock
}

do_install:append () {
	mkdir -p ${D}${base_bindir}

        sbinprogs="agetty ctrlaltdel cfdisk vipw vigr"
        sbinprogs_a="pivot_root hwclock mkswap losetup swapon swapoff fdisk fsck blkid blockdev fstrim sulogin switch_root nologin"
        binprogs_a="dmesg getopt kill more umount mount login su mountpoint"

        if [ "${base_sbindir}" != "${sbindir}" ]; then
        	mkdir -p ${D}${base_sbindir}
                for p in $sbinprogs $sbinprogs_a; do
                        if [ -f "${D}${sbindir}/$p" ]; then
                                mv "${D}${sbindir}/$p" "${D}${base_sbindir}/$p"
                        fi
                done
        fi

        if [ "${base_bindir}" != "${bindir}" ]; then
        	mkdir -p ${D}${base_bindir}
                for p in $binprogs_a; do
                        if [ -f "${D}${bindir}/$p" ]; then
                                mv "${D}${bindir}/$p" "${D}${base_bindir}/$p"
                        fi
                done
        fi

	install -d ${D}${sysconfdir}/default/
	echo 'MOUNTALL="-t nonfs,nosmbfs,noncpfs"' > ${D}${sysconfdir}/default/mountall

	rm -f ${D}${bindir}/chkdupexe

	install -m 0755 ${B}/fcntl-lock ${D}${bindir}
}

do_install:append:class-target () {
	if [ "${@bb.utils.filter('PACKAGECONFIG', 'pam', d)}" ]; then
		install -d ${D}${sysconfdir}/pam.d
		install -m 0644 ${UNPACKDIR}/runuser.pamd ${D}${sysconfdir}/pam.d/runuser
		install -m 0644 ${UNPACKDIR}/runuser-l.pamd ${D}${sysconfdir}/pam.d/runuser-l
		# Required for "su -" aka "su --login" because
		# otherwise it uses "other", which has "auth pam_deny.so"
		# and thus prevents the operation.
		ln -s su ${D}${sysconfdir}/pam.d/su-l

		if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
			install -d ${D}${nonarch_libdir}/tmpfiles.d
			install -m 0644 ${S}/misc-utils/lastlog2-tmpfiles.conf.in \
				${D}${nonarch_libdir}/tmpfiles.d/lastlog2.conf
		fi

		if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
			install -d ${D}${sysconfdir}/default/volatiles
			echo "d root root 0755 /var/lib/lastlog none" \
				> ${D}${sysconfdir}/default/volatiles/99_lastlog2
		fi
	fi
}
# nologin causes a conflict with shadow-native
# kill causes a conflict with coreutils-native (if ${bindir}==${base_bindir})
do_install:append:class-native () {
	rm -f ${D}${base_sbindir}/nologin
	rm -f ${D}${base_bindir}/kill
}

# dm-verity support introduces a circular build dependency, so util-linux-libuuid is split out for target builds
# Need to build libuuid for uuidgen, but then delete it and let the other recipe ship it
do_install:append () {
	rm -rf ${D}${includedir}/uuid ${D}${libdir}/pkgconfig/uuid.pc ${D}${libdir}/libuuid* ${D}${base_libdir}/libuuid*
}

ALTERNATIVE_PRIORITY = "80"

ALTERNATIVE_LINK_NAME[blkid] = "${base_sbindir}/blkid"
ALTERNATIVE_LINK_NAME[blockdev] = "${base_sbindir}/blockdev"
ALTERNATIVE_LINK_NAME[cal] = "${bindir}/cal"
ALTERNATIVE_LINK_NAME[chfn] = "${bindir}/chfn"
ALTERNATIVE_LINK_NAME[chsh] = "${bindir}/chsh"
ALTERNATIVE_LINK_NAME[chrt] = "${bindir}/chrt"
ALTERNATIVE_LINK_NAME[dmesg] = "${base_bindir}/dmesg"
ALTERNATIVE_LINK_NAME[eject] = "${bindir}/eject"
ALTERNATIVE_LINK_NAME[fallocate] = "${bindir}/fallocate"
ALTERNATIVE_LINK_NAME[fdisk] = "${base_sbindir}/fdisk"
ALTERNATIVE_LINK_NAME[findfs] = "${sbindir}/findfs"
ALTERNATIVE_LINK_NAME[flock] = "${bindir}/flock"
ALTERNATIVE_LINK_NAME[fsck] = "${base_sbindir}/fsck"
ALTERNATIVE_LINK_NAME[fsfreeze] = "${sbindir}/fsfreeze"
ALTERNATIVE_LINK_NAME[fstrim] = "${base_sbindir}/fstrim"
ALTERNATIVE_LINK_NAME[getopt] = "${base_bindir}/getopt"
ALTERNATIVE:${PN}-agetty = "getty"
ALTERNATIVE_LINK_NAME[getty] = "${base_sbindir}/getty"
ALTERNATIVE_TARGET[getty] = "${base_sbindir}/agetty"
ALTERNATIVE_LINK_NAME[hexdump] = "${bindir}/hexdump"
ALTERNATIVE_LINK_NAME[hwclock] = "${base_sbindir}/hwclock"
ALTERNATIVE_LINK_NAME[ionice] = "${bindir}/ionice"
ALTERNATIVE_LINK_NAME[ipcrm] = "${bindir}/ipcrm"
ALTERNATIVE_LINK_NAME[ipcs] = "${bindir}/ipcs"
ALTERNATIVE_LINK_NAME[kill] = "${base_bindir}/kill"
ALTERNATIVE:${PN}-last = "last lastb"
ALTERNATIVE_LINK_NAME[last] = "${bindir}/last"
ALTERNATIVE_LINK_NAME[lastb] = "${bindir}/lastb"
ALTERNATIVE_LINK_NAME[logger] = "${bindir}/logger"
ALTERNATIVE_LINK_NAME[losetup] = "${base_sbindir}/losetup"
ALTERNATIVE_LINK_NAME[mesg] = "${bindir}/mesg"
ALTERNATIVE_LINK_NAME[mkswap] = "${base_sbindir}/mkswap"
ALTERNATIVE_LINK_NAME[mcookie] = "${bindir}/mcookie"
ALTERNATIVE_LINK_NAME[more] = "${base_bindir}/more"
ALTERNATIVE_LINK_NAME[mount] = "${base_bindir}/mount"
ALTERNATIVE_LINK_NAME[mountpoint] = "${base_bindir}/mountpoint"
ALTERNATIVE_LINK_NAME[nologin] = "${base_sbindir}/nologin"
ALTERNATIVE_LINK_NAME[nsenter] = "${bindir}/nsenter"
ALTERNATIVE_LINK_NAME[pivot_root] = "${base_sbindir}/pivot_root"
ALTERNATIVE_LINK_NAME[prlimit] = "${bindir}/prlimit"
ALTERNATIVE_LINK_NAME[readprofile] = "${sbindir}/readprofile"
ALTERNATIVE_LINK_NAME[renice] = "${bindir}/renice"
ALTERNATIVE_LINK_NAME[rev] = "${bindir}/rev"
ALTERNATIVE_LINK_NAME[rfkill] = "${sbindir}/rfkill"
ALTERNATIVE_LINK_NAME[rtcwake] = "${sbindir}/rtcwake"
ALTERNATIVE_LINK_NAME[scriptreplay] = "${bindir}/scriptreplay"
ALTERNATIVE_LINK_NAME[setpriv] = "${bindir}/setpriv"
ALTERNATIVE_LINK_NAME[setsid] = "${bindir}/setsid"
ALTERNATIVE_LINK_NAME[su] = "${base_bindir}/su"
ALTERNATIVE_LINK_NAME[sulogin] = "${base_sbindir}/sulogin"
ALTERNATIVE_LINK_NAME[swapoff] = "${base_sbindir}/swapoff"
ALTERNATIVE_LINK_NAME[swapon] = "${base_sbindir}/swapon"
ALTERNATIVE_LINK_NAME[switch_root] = "${base_sbindir}/switch_root"
ALTERNATIVE_LINK_NAME[taskset] = "${bindir}/taskset"
ALTERNATIVE_LINK_NAME[umount] = "${base_bindir}/umount"
ALTERNATIVE_LINK_NAME[unshare] = "${bindir}/unshare"
ALTERNATIVE_LINK_NAME[utmpdump] = "${bindir}/utmpdump"
ALTERNATIVE_LINK_NAME[uuidgen] = "${bindir}/uuidgen"
ALTERNATIVE_LINK_NAME[wall] = "${bindir}/wall"

ALTERNATIVE:${PN}-doc = "\
blkid.8 eject.1 findfs.8 fsck.8 kill.1 last.1 lastb.1 libblkid.3 logger.1 mesg.1 \
mountpoint.1 nologin.8 rfkill.8 sulogin.8 utmpdump.1 uuid.3 wall.1\
"
ALTERNATIVE:${PN}-doc += "${@bb.utils.contains('PACKAGECONFIG', 'pam', 'su.1', '', d)}"
ALTERNATIVE:${PN}-doc += "${@bb.utils.contains('PACKAGECONFIG', 'chfn-chsh', 'chfn.1 chsh.1', '', d)}"

ALTERNATIVE_LINK_NAME[blkid.8] = "${mandir}/man8/blkid.8"
ALTERNATIVE_LINK_NAME[chfn.1] = "${mandir}/man1/chfn.1"
ALTERNATIVE_LINK_NAME[chsh.1] = "${mandir}/man1/chsh.1"
ALTERNATIVE_LINK_NAME[eject.1] = "${mandir}/man1/eject.1"
ALTERNATIVE_LINK_NAME[findfs.8] = "${mandir}/man8/findfs.8"
ALTERNATIVE_LINK_NAME[fsck.8] = "${mandir}/man8/fsck.8"
ALTERNATIVE_LINK_NAME[kill.1] = "${mandir}/man1/kill.1"
ALTERNATIVE_LINK_NAME[last.1] = "${mandir}/man1/last.1"
ALTERNATIVE_LINK_NAME[lastb.1] = "${mandir}/man1/lastb.1"
ALTERNATIVE_LINK_NAME[libblkid.3] = "${mandir}/man3/libblkid.3"
ALTERNATIVE_LINK_NAME[logger.1] = "${mandir}/man1/logger.1"
ALTERNATIVE_LINK_NAME[mesg.1] = "${mandir}/man1/mesg.1"
ALTERNATIVE_LINK_NAME[mountpoint.1] = "${mandir}/man1/mountpoint.1"
ALTERNATIVE_LINK_NAME[nologin.8] = "${mandir}/man8/nologin.8"
ALTERNATIVE_LINK_NAME[rfkill.8] = "${mandir}/man8/rfkill.8"
ALTERNATIVE_LINK_NAME[setpriv.1] = "${mandir}/man1/setpriv.1"
ALTERNATIVE_LINK_NAME[su.1] = "${mandir}/man1/su.1"
ALTERNATIVE_LINK_NAME[sulogin.8] = "${mandir}/man8/sulogin.8"
ALTERNATIVE_LINK_NAME[utmpdump.1] = "${mandir}/man1/utmpdump.1"
ALTERNATIVE_LINK_NAME[uuid.3] = "${mandir}/man3/uuid.3"
ALTERNATIVE_LINK_NAME[wall.1] = "${mandir}/man1/wall.1"

BBCLASSEXTEND = "native nativesdk"

PTEST_BINDIR = "1"
do_compile_ptest() {
    oe_runmake buildtest-TESTS
}

do_install_ptest() {
    mkdir -p ${D}${PTEST_PATH}/tests/ts
    find . -name 'test*' -maxdepth 1 -type f -perm -111 -exec cp {} ${D}${PTEST_PATH} \;
    find ./.libs -name 'sample*' -maxdepth 1 -type f -perm -111 -exec cp {} ${D}${PTEST_PATH} \;
    find ./.libs -name 'test*' -maxdepth 1 -type f -perm -111 -exec cp {} ${D}${PTEST_PATH} \;

    cp ${S}/tests/*.sh ${D}${PTEST_PATH}/tests/
    cp -pR ${S}/tests/expected ${D}${PTEST_PATH}/tests/expected
    cp -pR ${S}/tests/ts ${D}${PTEST_PATH}/tests/
    cp ${B}/config.h ${D}${PTEST_PATH}

    sed -i 's|@base_sbindir@|${base_sbindir}|g' ${D}${PTEST_PATH}/run-ptest

    # chfn needs PAM
    if ! ${@bb.utils.contains('PACKAGECONFIG', 'pam', 'true', 'false', d)}; then
        rm -rf ${D}${PTEST_PATH}/tests/ts/chfn
    fi
    # remove raid tests, known failures and avoid dependency on mdadm therefore
    # See https://github.com/util-linux/util-linux/commit/7519c3edab120b14623931d5ddb16fdc6e7cad5d
    rm -rf ${D}${PTEST_PATH}/tests/ts/blkid/md-raid0-whole
    rm -rf ${D}${PTEST_PATH}/tests/ts/blkid/md-raid1-part
    rm -rf ${D}${PTEST_PATH}/tests/ts/blkid/md-raid1-whole
    rm -rf ${D}${PTEST_PATH}/tests/ts/fdisk/align-512-4K-md
}

# Delete tests not working on musl
do_install_ptest:append:libc-musl() {
    for t in tests/ts/col/multibyte \
            tests/ts/lib/timeutils \
            tests/ts/misc/enosys \
            tests/ts/dmesg/limit; do
        rm -rf ${D}${PTEST_PATH}/$t
    done
}
