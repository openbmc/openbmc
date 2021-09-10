# This recipe is modified from the recipe originally found in the Open-Switch
# repository:
#
# https://github.com/open-switch/ops-build
# yocto/openswitch/meta-foss-openswitch/meta-oe/recipes-support/open-vm-tools/open-vm-tools_10.0.5.bb
# Commit 9008de2d8e100f3f868c66765742bca9fa98f3f9
#
# The recipe packaging has been relicensed under the MIT license for inclusion
# in meta-openembedded by agreement of the author (Diego Dompe).
#

SUMMARY = "Tools to enhance VMWare guest integration and performance"
HOMEPAGE = "https://github.com/vmware/open-vm-tools"
DESCRIPTION = "\
open-vm-tools is a set of services and modules that enable several features in VMware products \
for better management of and seamless user interactions with guests.\
"
SECTION = "vmware-tools"

LICENSE = "LGPL-2.0 & GPL-2.0 & BSD & CDDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=559317809c5444fb39ceaf6ac45c43ac"
LICENSE:modules/freebsd/vmblock = "BSD"
LICENSE:modules/freebsd/vmmemctl = "GPL-2.0"
LICENSE:modules/freebsd/vmxnet = "GPL-2.0"
LICENSE:modules/linux = "GPL-2.0"
LICENSE:modules/solaris = "CDDL-1.0"

SRC_URI = "git://github.com/vmware/open-vm-tools.git;protocol=https \
    file://tools.conf \
    file://vmtoolsd.service \
    file://vmtoolsd.init \
    file://0001-configure.ac-don-t-use-dnet-config.patch;patchdir=.. \
    file://0003-Use-configure-test-for-struct-timespec.patch;patchdir=.. \
    file://0004-Fix-definition-of-ALLPERMS-and-ACCESSPERMS.patch;patchdir=.. \
    file://0005-Use-configure-to-test-for-feature-instead-of-platfor.patch;patchdir=.. \
    file://0006-Use-configure-test-for-sys-stat.h-include.patch;patchdir=.. \
    file://0007-Fix-subdir-objects-configure-error.patch;patchdir=.. \
    file://0008-include-poll.h-instead-of-sys-poll.h.patch;patchdir=.. \
    file://0009-Rename-poll.h-to-vm_poll.h.patch;patchdir=.. \
    file://0010-use-posix-strerror_r-unless-on-gnu-libc-system.patch;patchdir=.. \
    file://0011-Use-uintmax_t-for-handling-rlim_t.patch;patchdir=.. \
    file://0012-Use-off64_t-instead-of-__off64_t.patch;patchdir=.. \
    file://0013-misc-Do-not-print-NULL-string-into-logs.patch;patchdir=.. \
    file://0001-Make-HgfsConvertFromNtTimeNsec-aware-of-64-bit-time_.patch;patchdir=.. \
    file://0002-hgfsServerLinux-Consider-64bit-time_t-possibility.patch;patchdir=.. \
    file://0001-hgfsmounter-Makefile.am-support-usrmerge.patch;patchdir=.. \
    file://0001-pollGtk-Fix-volatile-qualifier-exposed-incorrectly.patch;patchdir=.. \
"

SRC_URI:append:libc-musl = " file://0001-Add-resolv_compat.h-for-musl-builds.patch;patchdir=.. \
"

SRCREV = "7ae57c3c7c1f68c74637ad009673dae94ee52570"

S = "${WORKDIR}/git/open-vm-tools"

DEPENDS = "glib-2.0 glib-2.0-native util-linux libdnet procps libtirpc"

# open-vm-tools is supported only on x86.
COMPATIBLE_HOST = '(x86_64.*|i.86.*)-linux'

inherit autotools pkgconfig systemd update-rc.d

SYSTEMD_SERVICE:${PN} = "vmtoolsd.service"

EXTRA_OECONF = "--without-icu --disable-multimon --disable-docs \
         --disable-tests --without-gtkmm --without-xerces --without-pam \
         --disable-vgauth --disable-deploypkg \
         --without-root-privileges --without-kernel-modules --with-tirpc \
         --with-udev-rules-dir=${nonarch_base_libdir}/udev/rules.d"

NO_X11_FLAGS = "--without-x --without-gtk2 --without-gtk3"
X11_DEPENDS = "libxext libxi libxrender libxrandr libxtst gtk+ gdk-pixbuf"
PACKAGECONFIG[x11] = ",${NO_X11_FLAGS},${X11_DEPENDS}"

# fuse gets implicitly detected; there is no --without-fuse option.
PACKAGECONFIG[fuse] = ",,fuse"

CFLAGS:append:toolchain-clang = " -Wno-address-of-packed-member"
FILES:${PN} += "\
    ${libdir}/open-vm-tools/plugins/vmsvc/lib*.so \
    ${libdir}/open-vm-tools/plugins/common/lib*.so \
    ${sysconfdir}/vmware-tools/tools.conf \
"
FILES:${PN}-locale += "${datadir}/open-vm-tools/messages"
FILES:${PN}-dev += "${libdir}/open-vm-tools/plugins/common/lib*.la"

CONFFILES:${PN} += "${sysconfdir}/vmware-tools/tools.conf"

RDEPENDS:${PN} = "util-linux libdnet fuse"

do_install:append() {
    if ! ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','true','false',d)}; then
        install -d ${D}/sbin
        ln -sf ${sbindir}/mount.vmhgfs ${D}/sbin/mount.vmhgfs
    fi
    install -d ${D}${sysconfdir}/vmware-tools
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/*.service ${D}${systemd_unitdir}/system
    else
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/vmtoolsd.init ${D}${sysconfdir}/init.d/vmtoolsd
    fi
    install -m 0644 ${WORKDIR}/tools.conf ${D}${sysconfdir}/vmware-tools/tools.conf
}

do_configure:prepend() {
    export CUSTOM_DNET_NAME=dnet
    export CUSTOM_DNET_LIBS=-L${STAGING_LIBDIR}/libdnet.so
}

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "vmtoolsd"
INITSCRIPT_PARAMS:${PN} = "start 90 2 3 4 5 . stop 60 0 1 6 ."

python() {
    if 'filesystems-layer' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-filesystems to be present to provide fuse.')
}
