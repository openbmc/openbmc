HOMEPAGE = "https://www.sourceware.org/lvm2/"
SECTION = "utils"
DESCRIPTION = "LVM2 is a set of utilities to manage logical volumes in Linux."
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12713b4d9386533feeb07d6e4831765a \
                    file://COPYING.LIB;md5=fbc093901857fcd118f065f900982c24"

DEPENDS += "libaio"

SRC_URI = "git://sourceware.org/git/lvm2.git;branch=main \
           file://lvm.conf \
           file://0001-implement-libc-specific-reopen_stream.patch \
           file://0004-tweak-MODPROBE_CMD-for-cross-compile.patch \
           file://0001-Avoid-bashisms-in-init-scripts.patch \
           file://0005-do-not-build-manual.patch \
           file://0006-start-lvm2-monitor.service-after-tmp.mount.patch \
           file://reproducible-build.patch \
           file://0001-include-libgen.h-for-basename.patch \
           "

SRCREV = "0e01a5d3ae1100a6641772ab295e0185d8d6a6b0"
PV = "2.03.33"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\_\d+)+)"

inherit autotools-brokensep pkgconfig systemd

def get_dm_version(p):
    import re

    fn = p + '/VERSION_DM'
    try:
        f = open(fn, 'r')
    except IOError:
        return None

    lines = f.readlines()
    f.close()
    for line in lines:
        m = re.match(r'(\w+)[-. ](\w+)[-. ](\w+)', line.strip())
        if m:
            return ".".join([m.group(1), m.group(2), m.group(3)])

    return None

DM_VERSION = '${@get_dm_version("${S}")}'
python do_package:prepend() {
    d.setVar('PKGV:libdevmapper', d.getVar("DM_VERSION"))
}

LVM2_PACKAGECONFIG = "dmeventd"
LVM2_PACKAGECONFIG:append:class-target = " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)} \
"

# odirect is always enabled because there currently is a bug in
# lib/device/dev-io.c which prevents compiling without it. It is
# better to stick to configurations that were actually tested by
# upstream...
PACKAGECONFIG ??= "odirect ${LVM2_PACKAGECONFIG}"

PACKAGECONFIG[dmeventd] = "--enable-dmeventd,--disable-dmeventd"
PACKAGECONFIG[odirect] = "--enable-o_direct,--disable-o_direct"
PACKAGECONFIG[readline] = "--enable-readline,--disable-readline,readline"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"
# NOTE: Add thin-provisioning-tools only if your distro policy allows GPL-3.0 license
PACKAGECONFIG[thin-provisioning-tools] = "--with-thin=internal,--with-thin=none,,thin-provisioning-tools"

# Unset user/group to unbreak install.
EXTRA_OECONF = "--with-user= \
                --with-group= \
                --enable-realtime \
                --enable-cmdlib \
                --enable-pkgconfig \
                --with-usrlibdir=${libdir} \
                --with-systemdsystemunitdir=${systemd_system_unitdir} \
                --disable-thin_check_needs_check \
                --with-thin-check=${sbindir}/thin_check \
                --with-thin-dump=${sbindir}/thin_dump \
                --with-thin-repair=${sbindir}/thin_repair \
                --with-thin-restore=${sbindir}/thin_restore \
"
