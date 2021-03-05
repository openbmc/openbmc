SUMMARY = "A suite of basic system administration utilities"
HOMEPAGE = "https://en.wikipedia.org/wiki/Util-linux"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

SECTION = "base"

LICENSE = "GPLv2+ & LGPLv2.1+ & BSD-3-Clause & BSD-4-Clause"
LICENSE_${PN}-libblkid = "LGPLv2.1+"
LICENSE_${PN}-libfdisk = "LGPLv2.1+"
LICENSE_${PN}-libmount = "LGPLv2.1+"
LICENSE_${PN}-libsmartcols = "LGPLv2.1+"
LICENSE_${PN}-libuuid = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://README.licensing;md5=0fd5c050c6187d2bf0a4492b7f4e33da \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://Documentation/licenses/COPYING.GPL-2.0-or-later;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://Documentation/licenses/COPYING.LGPL-2.1-or-later;md5=4fbd65380cdd255951079008b364516c \
                    file://Documentation/licenses/COPYING.BSD-3-Clause;md5=58dcd8452651fc8b07d1f65ce07ca8af \
                    file://Documentation/licenses/COPYING.BSD-4-Clause-UC;md5=263860f8968d8bafa5392cab74285262 \
                    file://libuuid/COPYING;md5=6d2cafc999feb2c2de84d4d24b23290c \
                    file://libmount/COPYING;md5=7c7e39fb7d70ffe5d693a643e29987c2 \
                    file://libblkid/COPYING;md5=693bcbbe16d3a4a4b37bc906bc01cc04 \
                    file://libfdisk/COPYING;md5=693bcbbe16d3a4a4b37bc906bc01cc04 \
                    file://libsmartcols/COPYING;md5=693bcbbe16d3a4a4b37bc906bc01cc04 \
"

#gtk-doc is not enabled as it requires xmlto which requires util-linux
inherit autotools gettext manpages pkgconfig systemd update-alternatives python3-dir bash-completion ptest
DEPENDS = "libcap-ng ncurses virtual/crypt zlib"

MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/v${MAJOR_VERSION}/${BP}.tar.xz \
           file://configure-sbindir.patch \
           file://runuser.pamd \
           file://runuser-l.pamd \
           file://ptest.patch \
           file://run-ptest \
           file://display_testname_for_subtest.patch \
           file://avoid_parallel_tests.patch \
           "
SRC_URI[sha256sum] = "f7516ba9d8689343594356f0e5e1a5f0da34adfbc89023437735872bb5024c5f"

PACKAGES =+ "${PN}-swaponoff"
PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'pylibmount', '${PN}-pylibmount', '', d)}"

python util_linux_binpackages () {
    def pkg_hook(f, pkg, file_regex, output_pattern, modulename):
        pn = d.getVar('PN')
        d.appendVar('RRECOMMENDS_%s' % pn, ' %s' % pkg)

        if d.getVar('ALTERNATIVE_' + pkg):
            return
        if d.getVarFlag('ALTERNATIVE_LINK_NAME', modulename):
            d.setVar('ALTERNATIVE_' + pkg, modulename)

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
                extras[pkg] = extras.get(pkg, '') + ' ' + file.replace(dvar, '', 1)

    pn = d.getVar('PN')
    for pkg, links in extras.items():
        of = d.getVar('FILES_' + pn + '-' + pkg)
        links = of + links
        d.setVar('FILES_' + pn + '-' + pkg, links)
}

# we must execute before update-alternatives PACKAGE_PREPROCESS_FUNCS
PACKAGE_PREPROCESS_FUNCS =+ "util_linux_binpackages "

python util_linux_libpackages() {
    do_split_packages(d, root=d.getVar('UTIL_LINUX_LIBDIR'), file_regex=r'^lib(.*)\.so\..*$',
                      output_pattern='${PN}-lib%s',
                      description='${PN} lib%s',
                      extra_depends='', prepend=True, allow_links=True)
}

PACKAGESPLITFUNCS =+ "util_linux_libpackages"

PACKAGES_DYNAMIC = "^${PN}-.*"

CACHED_CONFIGUREVARS += "scanf_cv_alloc_modifier=ms"
UTIL_LINUX_LIBDIR = "${libdir}"
UTIL_LINUX_LIBDIR_class-target = "${base_libdir}"
EXTRA_OECONF = "\
    --enable-libuuid --enable-libblkid \
    \
    --enable-fsck --enable-kill --enable-last --enable-mesg \
    --enable-mount --enable-partx --enable-raw --enable-rfkill \
    --enable-unshare --enable-write \
    \
    --disable-bfs --disable-chfn-chsh --disable-login \
    --disable-makeinstall-chown --disable-minix --disable-newgrp \
    --disable-use-tty-group --disable-vipw \
    \
    --without-udev \
    \
    usrsbin_execdir='${sbindir}' \
    --libdir='${UTIL_LINUX_LIBDIR}' \
"

EXTRA_OECONF_append_class-target = " --enable-setpriv"
EXTRA_OECONF_append_class-native = " --without-cap-ng --disable-setpriv"
EXTRA_OECONF_append_class-nativesdk = " --without-cap-ng --disable-setpriv"
EXTRA_OECONF_append = " --disable-hwclock-gplv3"

# enable pcre2 for native/nativesdk to match host distros
# this helps to keep same expectations when using the SDK or
# build host versions during development
#
PACKAGECONFIG ?= "pcre2"
PACKAGECONFIG_class-target ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
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

EXTRA_OEMAKE = "ARCH=${TARGET_ARCH} CPU= CPUOPT= 'OPT=${CFLAGS}'"

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = ""
FILES_${PN}-doc += "${datadir}/getopt/getopt-*.*"
FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/libmount/pylibmount.la"
FILES_${PN}-mount = "${sysconfdir}/default/mountall"
FILES_${PN}-runuser = "${sysconfdir}/pam.d/runuser*"
FILES_${PN}-su = "${sysconfdir}/pam.d/su-l"
CONFFILES_${PN}-su = "${sysconfdir}/pam.d/su-l"
FILES_${PN}-pylibmount = "${PYTHON_SITEPACKAGES_DIR}/libmount/pylibmount.so \
                          ${PYTHON_SITEPACKAGES_DIR}/libmount/__init__.* \
                          ${PYTHON_SITEPACKAGES_DIR}/libmount/__pycache__/*"

# Util-linux' blkid replaces the e2fsprogs one
RCONFLICTS_${PN}-blkid = "${MLPREFIX}e2fsprogs-blkid"
RREPLACES_${PN}-blkid = "${MLPREFIX}e2fsprogs-blkid"

RRECOMMENDS_${PN}_class-native = ""
RRECOMMENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""

RPROVIDES_${PN}-dev = "${PN}-libblkid-dev ${PN}-libmount-dev ${PN}-libuuid-dev"

RDEPENDS_${PN}-bash-completion += "${PN}-lsblk"
RDEPENDS_${PN}-ptest += "bash bc btrfs-tools coreutils e2fsprogs grep iproute2 kmod mdadm procps sed socat which xz"
RRECOMMENDS_${PN}-ptest += "kernel-module-scsi-debug"
RDEPENDS_${PN}-swaponoff = "${PN}-swapon ${PN}-swapoff"
ALLOW_EMPTY_${PN}-swaponoff = "1"

#SYSTEMD_PACKAGES = "${PN}-uuidd ${PN}-fstrim"
SYSTEMD_SERVICE_${PN}-uuidd = "uuidd.socket uuidd.service"
SYSTEMD_AUTO_ENABLE_${PN}-uuidd = "disable"
SYSTEMD_SERVICE_${PN}-fstrim = "fstrim.timer fstrim.service"
SYSTEMD_AUTO_ENABLE_${PN}-fstrim = "disable"

do_install () {
	# with ccache the timestamps on compiled files may
	# end up earlier than on their inputs, this allows
	# for the resultant compilation in the install step.
	oe_runmake 'CC=${CC}' 'LD=${LD}' \
		'LDFLAGS=${LDFLAGS}' 'DESTDIR=${D}' install

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
}

do_install_append_class-target () {
	if [ "${@bb.utils.filter('PACKAGECONFIG', 'pam', d)}" ]; then
		install -d ${D}${sysconfdir}/pam.d
		install -m 0644 ${WORKDIR}/runuser.pamd ${D}${sysconfdir}/pam.d/runuser
		install -m 0644 ${WORKDIR}/runuser-l.pamd ${D}${sysconfdir}/pam.d/runuser-l
		# Required for "su -" aka "su --login" because
		# otherwise it uses "other", which has "auth pam_deny.so"
		# and thus prevents the operation.
		ln -s su ${D}${sysconfdir}/pam.d/su-l
	fi
}
# nologin causes a conflict with shadow-native
# kill causes a conflict with coreutils-native (if ${bindir}==${base_bindir})
do_install_append_class-native () {
	rm -f ${D}${base_sbindir}/nologin
	rm -f ${D}${base_bindir}/kill
}

ALTERNATIVE_PRIORITY = "80"

ALTERNATIVE_LINK_NAME[blkid] = "${base_sbindir}/blkid"
ALTERNATIVE_LINK_NAME[blockdev] = "${base_sbindir}/blockdev"
ALTERNATIVE_LINK_NAME[cal] = "${bindir}/cal"
ALTERNATIVE_LINK_NAME[chrt] = "${bindir}/chrt"
ALTERNATIVE_LINK_NAME[dmesg] = "${base_bindir}/dmesg"
ALTERNATIVE_LINK_NAME[eject] = "${bindir}/eject"
ALTERNATIVE_LINK_NAME[fallocate] = "${bindir}/fallocate"
ALTERNATIVE_LINK_NAME[fdisk] = "${base_sbindir}/fdisk"
ALTERNATIVE_LINK_NAME[flock] = "${bindir}/flock"
ALTERNATIVE_LINK_NAME[fsck] = "${base_sbindir}/fsck"
ALTERNATIVE_LINK_NAME[fsfreeze] = "${sbindir}/fsfreeze"
ALTERNATIVE_LINK_NAME[fstrim] = "${base_sbindir}/fstrim"
ALTERNATIVE_LINK_NAME[getopt] = "${base_bindir}/getopt"
ALTERNATIVE_${PN}-agetty = "getty"
ALTERNATIVE_LINK_NAME[getty] = "${base_sbindir}/getty"
ALTERNATIVE_TARGET[getty] = "${base_sbindir}/agetty"
ALTERNATIVE_LINK_NAME[hexdump] = "${bindir}/hexdump"
ALTERNATIVE_LINK_NAME[hwclock] = "${base_sbindir}/hwclock"
ALTERNATIVE_LINK_NAME[ionice] = "${bindir}/ionice"
ALTERNATIVE_LINK_NAME[kill] = "${base_bindir}/kill"
ALTERNATIVE_${PN}-last = "last lastb"
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

ALTERNATIVE_${PN}-doc = "\
blkid.8 eject.1 findfs.8 fsck.8 kill.1 last.1 lastb.1 libblkid.3 logger.1 mesg.1 \
mountpoint.1 nologin.8 rfkill.8 sulogin.8 utmpdump.1 uuid.3 wall.1\
"
ALTERNATIVE_${PN}-doc += "${@bb.utils.contains('PACKAGECONFIG', 'pam', 'su.1', '', d)}"

ALTERNATIVE_LINK_NAME[blkid.8] = "${mandir}/man8/blkid.8"
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
    cp ${WORKDIR}/build/config.h ${D}${PTEST_PATH}

    # The original paths of executables to be tested point to a local folder containing
    # the executables. We want to test the installed executables, not the local copies.
    # So strip the paths, the executables will be located via "which"
    sed  -i \
         -e '/^TS_CMD/ s|$top_builddir/||g' \
         -e '/^TS_HELPER/ s|$top_builddir|${PTEST_PATH}|g' \
         ${D}${PTEST_PATH}/tests/commands.sh

    # Change 'if [ ! -x "$1" ]' to 'if [ ! -x "`which $1 2>/dev/null`"]'
    sed -i -e \
        '/^\tif[[:space:]]\[[[:space:]]![[:space:]]-x[[:space:]]"$1"/s|$1|`which $1 2>/dev/null`|g' \
         ${D}${PTEST_PATH}/tests/functions.sh

    # Running "kill" without the the complete path would use the shell's built-in kill
    sed -i -e \
         '/^TS_CMD_KILL/ s|kill|${PTEST_PATH}/bin/kill|g' \
         ${D}${PTEST_PATH}/tests/commands.sh


    sed -i 's|@base_sbindir@|${base_sbindir}|g'       ${D}${PTEST_PATH}/run-ptest

}
