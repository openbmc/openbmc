SUMMARY = "A suite of basic system administration utilities"
HOMEPAGE = "http://userweb.kernel.org/~kzak/util-linux/"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

SECTION = "base"

LICENSE = "GPLv2+ & LGPLv2.1+ & BSD"

LIC_FILES_CHKSUM = "file://README.licensing;md5=1715f5ee3e01203ca1e1e0b9ee65918c \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://Documentation/licenses/COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://Documentation/licenses/COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://Documentation/licenses/COPYING.BSD-3;md5=58dcd8452651fc8b07d1f65ce07ca8af \
                    file://Documentation/licenses/COPYING.UCB;md5=263860f8968d8bafa5392cab74285262 \
                    file://libuuid/COPYING;md5=b442ffb762cf8d3e9df1b99e0bb4af70 \
                    file://libmount/COPYING;md5=fb93f01d4361069c5616327705373b16 \
                    file://libblkid/COPYING;md5=fb93f01d4361069c5616327705373b16"

#gtk-doc is not enabled as it requires xmlto which requires util-linux
inherit autotools gettext pkgconfig systemd update-alternatives python3-dir bash-completion ptest
DEPENDS = "zlib ncurses virtual/crypt"
DEPENDS_append_class-native = " lzo-native"
DEPENDS_append_class-nativesdk = " lzo-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/util-linux/v${MAJOR_VERSION}/util-linux-${PV}.tar.xz \
           "

PACKAGES =+ "util-linux-agetty util-linux-fdisk util-linux-cfdisk util-linux-sfdisk \
             util-linux-swaponoff util-linux-losetup util-linux-umount \
             util-linux-mount util-linux-readprofile util-linux-uuidd \
             util-linux-uuidgen util-linux-lscpu util-linux-fsck.cramfs util-linux-fsck \
             util-linux-blkid util-linux-mkfs util-linux-mcookie util-linux-rfkill \
             util-linux-lsblk util-linux-mkfs.cramfs util-linux-fstrim \
             util-linux-partx util-linux-hwclock util-linux-mountpoint \
             util-linux-findfs util-linux-getopt util-linux-sulogin util-linux-prlimit \
             util-linux-ionice util-linux-switch-root util-linux-unshare"
PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'pylibmount', 'util-linux-pylibmount', '', d)}"
PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'util-linux-runuser util-linux-su', '', d)}"

PACKAGES_DYNAMIC = "^util-linux-lib.*"

SHARED_EXTRA_OECONF = "--disable-use-tty-group \
                       --disable-makeinstall-chown \
                       --enable-kill --enable-last --enable-mesg --enable-partx \
                       --enable-raw --enable-rfkill --disable-login \
                       --disable-vipw --disable-newgrp --disable-chfn-chsh \
                       --enable-write --enable-mount --enable-unshare \
                       --enable-libuuid --enable-libblkid --enable-fsck \
                       --disable-minix --disable-bfs --without-udev \
                       usrsbin_execdir='${sbindir}' \
"

EXTRA_OECONF = "${SHARED_EXTRA_OECONF} --libdir=${base_libdir}"

PACKAGECONFIG_class-target ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = "--enable-su --enable-runuser,--disable-su --disable-runuser, libpam,"

# Respect the systemd feature for uuidd
PACKAGECONFIG[systemd] = "--with-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/, --without-systemd --without-systemdsystemunitdir,systemd"

# Build setpriv requires libcap-ng
PACKAGECONFIG[libcap-ng] = "--enable-setpriv,--disable-setpriv,libcap-ng,"

# Build python bindings for libmount
PACKAGECONFIG[pylibmount] = "--with-python=3 --enable-pylibmount,--without-python --disable-pylibmount,python3"

# Readline support
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"

FILES_${PN}-doc += "${datadir}/getopt/getopt-*.*"

FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/libmount/pylibmount.la"

FILES_util-linux-agetty = "${base_sbindir}/agetty"
FILES_util-linux-fdisk = "${base_sbindir}/fdisk.${BPN}"
FILES_util-linux-fstrim = "${base_sbindir}/fstrim.${BPN}"
FILES_util-linux-cfdisk = "${base_sbindir}/cfdisk"
FILES_util-linux-sfdisk = "${sbindir}/sfdisk"
FILES_util-linux-swaponoff = "${base_sbindir}/swapon.${BPN} ${base_sbindir}/swapoff.${BPN}"
FILES_util-linux-losetup = "${base_sbindir}/losetup.${BPN}"
FILES_util-linux-mount = "${base_bindir}/mount.${BPN}"
FILES_util-linux-mcookie = "${bindir}/mcookie"
FILES_util-linux-umount = "${base_bindir}/umount.${BPN}"
FILES_util-linux-readprofile = "${sbindir}/readprofile.${BPN}"
FILES_util-linux-uuidgen = "${bindir}/uuidgen"
FILES_util-linux-uuidd = "${sbindir}/uuidd"
FILES_util-linux-rfkill = "${sbindir}/rfkill*"
FILES_util-linux-partx = "${sbindir}/partx"
FILES_util-linux-hwclock = "${base_sbindir}/hwclock.${BPN}"
FILES_util-linux-findfs = "${sbindir}/findfs"
FILES_util-linux-getopt = "${base_bindir}/getopt.${BPN}"
FILES_util-linux-runuser = "${sbindir}/runuser"
FILES_util-linux-prlimit = "${bindir}/prlimit"
FILES_util-linux-ionice = "${bindir}/ionice.${BPN}"
FILES_util-linux-su = "${bindir}/su.util-linux ${sysconfdir}/pam.d/su-l"
CONFFILES_util-linux-su = "${sysconfdir}/pam.d/su-l"

FILES_util-linux-pylibmount = "${PYTHON_SITEPACKAGES_DIR}/libmount/pylibmount.so \
                               ${PYTHON_SITEPACKAGES_DIR}/libmount/__init__.* \
                               ${PYTHON_SITEPACKAGES_DIR}/libmount/__pycache__/*"
FILES_util-linux-lsblk = "${bindir}/lsblk"
FILES_util-linux-lscpu = "${bindir}/lscpu"

FILES_util-linux-fsck = "${base_sbindir}/fsck*"
FILES_util-linux-mkfs = "${sbindir}/mkfs"

FILES_util-linux-fsck.cramfs = "${sbindir}/fsck.cramfs"
FILES_util-linux-mkfs.cramfs = "${sbindir}/mkfs.cramfs"

FILES_util-linux-sulogin = "${base_sbindir}/sulogin*"
FILES_util-linux-mountpoint = "${base_bindir}/mountpoint.${BPN}"

FILES_util-linux-switch-root = "${base_sbindir}/switch_root.${BPN}"
FILES_util-linux-unshare = "${bindir}/unshare.${BPN}"

# Util-linux' blkid replaces the e2fsprogs one
FILES_util-linux-blkid = "${base_sbindir}/blkid*"
RCONFLICTS_util-linux-blkid = "e2fsprogs-blkid"
RREPLACES_util-linux-blkid = "e2fsprogs-blkid"

RDEPENDS_util-linux-runuser += "libpam"
RDEPENDS_util-linux-su += "libpam"

RDEPENDS_${PN} = "util-linux-umount util-linux-swaponoff util-linux-losetup util-linux-sulogin util-linux-lsblk"
RDEPENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'util-linux-runuser util-linux-su', '', d)}"

RRECOMMENDS_${PN} = "util-linux-fdisk util-linux-cfdisk util-linux-sfdisk util-linux-mount util-linux-readprofile util-linux-mkfs util-linux-mountpoint util-linux-prlimit util-linux-ionice util-linux-switch-root"

RRECOMMENDS_${PN}_class-native = ""
RRECOMMENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""

RPROVIDES_${PN}-dev = "util-linux-libblkid-dev util-linux-libmount-dev util-linux-libuuid-dev"

#SYSTEMD_PACKAGES = "${PN}-uuidd ${PN}-fstrim"
SYSTEMD_SERVICE_${PN}-uuidd = "uuidd.socket uuidd.service"
SYSTEMD_AUTO_ENABLE_${PN}-uuidd = "disable"
SYSTEMD_SERVICE_${PN}-fstrim = "fstrim.timer fstrim.service"
SYSTEMD_AUTO_ENABLE_${PN}-fstrim = "disable"

do_compile () {
	set -e
	oe_runmake ARCH=${TARGET_ARCH} CPU= CPUOPT= 'OPT=${CFLAGS}'
}

do_install () {
	# with ccache the timestamps on compiled files may
	# end up earlier than on their inputs, this allows
	# for the resultant compilation in the install step.
	oe_runmake ARCH=${TARGET_ARCH} CPU= CPUOPT= \
		'OPT=${CFLAGS}' 'CC=${CC}' 'LD=${LD}' \
		'LDFLAGS=${LDFLAGS}' 'DESTDIR=${D}' install

	mkdir -p ${D}${base_bindir}

        sbinprogs="agetty ctrlaltdel cfdisk vipw vigr"
        sbinprogs_a="pivot_root hwclock mkswap mkfs.minix fsck.minix losetup swapon swapoff fdisk fsck blkid blockdev fstrim sulogin switch_root nologin"
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

	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
		install -d ${D}${sysconfdir}/pam.d
		install -m 0644 ${WORKDIR}/runuser.pamd ${D}${sysconfdir}/pam.d/runuser
		install -m 0644 ${WORKDIR}/runuser-l.pamd ${D}${sysconfdir}/pam.d/runuser-l
	fi
	if [ "${@bb.utils.filter('PACKAGECONFIG', 'pam', d)}" ]; then
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

ALTERNATIVE_${PN}  = " \
    dmesg kill more mkswap blockdev pivot_root \
    hexdump last lastb logger mesg renice wall \
    setsid chrt flock utmpdump eject nologin taskset fallocate \
    fsfreeze nsenter cal rev \
"

ALTERNATIVE_LINK_NAME[dmesg] = "${base_bindir}/dmesg"
ALTERNATIVE_LINK_NAME[kill] = "${base_bindir}/kill"
ALTERNATIVE_LINK_NAME[more] = "${base_bindir}/more"
ALTERNATIVE_LINK_NAME[mkswap] = "${base_sbindir}/mkswap"
ALTERNATIVE_LINK_NAME[blockdev] = "${base_sbindir}/blockdev"
ALTERNATIVE_LINK_NAME[pivot_root] = "${base_sbindir}/pivot_root"
ALTERNATIVE_LINK_NAME[cal] = "${bindir}/cal"
ALTERNATIVE_LINK_NAME[eject] = "${bindir}/eject"
ALTERNATIVE_LINK_NAME[fallocate] = "${bindir}/fallocate"
ALTERNATIVE_LINK_NAME[rev] = "${bindir}/rev"
ALTERNATIVE_LINK_NAME[fsfreeze] = "${sbindir}/fsfreeze"
ALTERNATIVE_LINK_NAME[nologin] = "${base_sbindir}/nologin"

ALTERNATIVE_${PN}-doc = "mountpoint.1 last.1 lastb.1 mesg.1 wall.1 nologin.8 sulogin.8 utmpdump.1 rfkill.8 kill.1 libblkid.3 blkid.8 findfs.8 fsck.8 uuid.3 eject.1 logger.1"

ALTERNATIVE_LINK_NAME[last.1] = "${mandir}/man1/last.1"
ALTERNATIVE_LINK_NAME[lastb.1] = "${mandir}/man1/lastb.1"
ALTERNATIVE_LINK_NAME[mesg.1] = "${mandir}/man1/mesg.1"
ALTERNATIVE_LINK_NAME[mountpoint.1] = "${mandir}/man1/mountpoint.1"
ALTERNATIVE_LINK_NAME[nologin.8] = "${mandir}/man8/nologin.8"
ALTERNATIVE_LINK_NAME[rfkill.8] = "${mandir}/man8/rfkill.8"
ALTERNATIVE_LINK_NAME[sulogin.8] = "${mandir}/man8/sulogin.8"
ALTERNATIVE_LINK_NAME[utmpdump.1] = "${mandir}/man1/utmpdump.1"
ALTERNATIVE_LINK_NAME[wall.1] = "${mandir}/man1/wall.1"
ALTERNATIVE_LINK_NAME[kill.1] = "${mandir}/man1/kill.1"
ALTERNATIVE_LINK_NAME[libblkid.3] = "${mandir}/man3/libblkid.3"
ALTERNATIVE_LINK_NAME[blkid.8] = "${mandir}/man8/blkid.8"
ALTERNATIVE_LINK_NAME[findfs.8] = "${mandir}/man8/findfs.8"
ALTERNATIVE_LINK_NAME[fsck.8] = "${mandir}/man8/fsck.8"
ALTERNATIVE_LINK_NAME[uuid.3] = "${mandir}/man3/uuid.3"
ALTERNATIVE_LINK_NAME[eject.1] = "${mandir}/man1/eject.1"
ALTERNATIVE_LINK_NAME[logger.1] = "${mandir}/man1/logger.1"

ALTERNATIVE_util-linux-hwclock = "hwclock"
ALTERNATIVE_LINK_NAME[hwclock] = "${base_sbindir}/hwclock"

ALTERNATIVE_util-linux-fdisk = "fdisk"
ALTERNATIVE_LINK_NAME[fdisk] = "${base_sbindir}/fdisk"

ALTERNATIVE_util-linux-fstrim = "fstrim"
ALTERNATIVE_LINK_NAME[fstrim] = "${base_sbindir}/fstrim"

ALTERNATIVE_util-linux-agetty = "getty"
ALTERNATIVE_LINK_NAME[getty] = "${base_sbindir}/getty"
ALTERNATIVE_TARGET[getty] = "${base_sbindir}/agetty"

ALTERNATIVE_util-linux-mount = "mount"
ALTERNATIVE_LINK_NAME[mount] = "${base_bindir}/mount"

ALTERNATIVE_util-linux-umount = "umount"
ALTERNATIVE_LINK_NAME[umount] = "${base_bindir}/umount"

ALTERNATIVE_util-linux-readprofile = "readprofile"
ALTERNATIVE_LINK_NAME[readprofile] = "${sbindir}/readprofile"

ALTERNATIVE_util-linux-losetup = "losetup"
ALTERNATIVE_LINK_NAME[losetup] = "${base_sbindir}/losetup"

ALTERNATIVE_util-linux-swaponoff = "swapoff swapon"
ALTERNATIVE_LINK_NAME[swapoff] = "${base_sbindir}/swapoff"
ALTERNATIVE_LINK_NAME[swapon] = "${base_sbindir}/swapon"

ALTERNATIVE_util-linux-fsck = "fsck"
ALTERNATIVE_LINK_NAME[fsck] = "${base_sbindir}/fsck"

ALTERNATIVE_util-linux-blkid = "blkid"
ALTERNATIVE_LINK_NAME[blkid] = "${base_sbindir}/blkid"

ALTERNATIVE_util-linux-rfkill = "rfkill"
ALTERNATIVE_LINK_NAME[rfkill] = "${sbindir}/rfkill"

ALTERNATIVE_util-linux-getopt = "getopt"
ALTERNATIVE_LINK_NAME[getopt] = "${base_bindir}/getopt"

ALTERNATIVE_util-linux-sulogin = "sulogin"
ALTERNATIVE_LINK_NAME[sulogin] = "${base_sbindir}/sulogin"

ALTERNATIVE_util-linux-mountpoint = "mountpoint"
ALTERNATIVE_LINK_NAME[mountpoint] = "${base_bindir}/mountpoint"

ALTERNATIVE_util-linux-unshare = "unshare"
ALTERNATIVE_LINK_NAME[unshare] = "${bindir}/unshare"

ALTERNATIVE_util-linux-ionice = "ionice"
ALTERNATIVE_LINK_NAME[ionice] = "${bindir}/ionice"

ALTERNATIVE_util-linux-switch-root = "switch_root"
ALTERNATIVE_LINK_NAME[switch_root] = "${base_sbindir}/switch_root"

BBCLASSEXTEND = "native nativesdk"

python do_package_prepend () {
    if '--enable-su' in d.getVar('EXTRA_OECONF').split():
        d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' su')
        d.appendVar(d.expand('ALTERNATIVE_${PN}-doc'), ' su.1')

        d.setVarFlag('ALTERNATIVE_LINK_NAME', "su", d.expand('${base_bindir}/su'))
        d.setVarFlag('ALTERNATIVE_LINK_NAME', "su.1", d.expand('${mandir}/man1/su.1'))
}

python populate_packages_prepend() {
    do_split_packages(d, '${base_libdir}', '^lib(.*)\.so\..*$',
                      output_pattern='util-linux-lib%s',
                      description='util-linux lib%s',
                      extra_depends='', prepend=True, allow_links=True)
}

RDEPENDS_${PN}-bash-completion += "util-linux-lsblk"
RDEPENDS_${PN}-ptest = "bash grep coreutils which util-linux-blkid util-linux-fsck btrfs-tools"

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

    # "kill -L" behaves differently than "/bin/kill -L" so we need an additional fix
    sed -i -e \
         '/^TS_CMD_KILL/ s|kill|/bin/kill|g' \
         ${D}${PTEST_PATH}/tests/commands.sh
}
