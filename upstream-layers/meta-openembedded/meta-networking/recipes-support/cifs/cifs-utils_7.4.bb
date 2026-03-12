DESCRIPTION = "A a package of utilities for doing and managing mounts of the Linux CIFS filesystem."
HOMEPAGE = "http://wiki.samba.org/index.php/LinuxCIFS_utils"
SECTION = "otherosfs"
LICENSE = "GPL-3.0-only & LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "edac7178bec9520fb57d14946e67f5dd33b82d43"
SRC_URI = "git://git.samba.org/cifs-utils.git;branch=master;tag=${BP}"

DEPENDS += "libtalloc"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cap] = "--with-libcap,--without-libcap,libcap"
# when enabled, it creates ${bindir}/cifscreds and --ignore-fail-on-non-empty in do_install:append is needed
PACKAGECONFIG[cifscreds] = "--enable-cifscreds,--disable-cifscreds,keyutils"
# when enabled, it creates ${sbindir}/cifs.upcall and --ignore-fail-on-non-empty in do_install:append is needed
PACKAGECONFIG[cifsupcall] = "--enable-cifsupcall,--disable-cifsupcall,krb5 libtalloc keyutils"
PACKAGECONFIG[cifsidmap] = "--enable-cifsidmap,--disable-cifsidmap,keyutils samba"
PACKAGECONFIG[cifsacl] = "--enable-cifsacl,--disable-cifsacl,samba"
PACKAGECONFIG[pam] = "--enable-pam --with-pamdir=${base_libdir}/security,--disable-pam,libpam keyutils"

inherit autotools pkgconfig

CFLAGS += "-I ${B}"

do_configure:prepend() {
    # want installed to /usr/sbin rather than /sbin to be DISTRO_FEATURES usrmerge compliant
    # must override ROOTSBINDIR (default '/sbin'),
    # setting --exec-prefix or --prefix in EXTRA_OECONF does not work
    if ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','true','false',d)}; then
        export ROOTSBINDIR=${sbindir}
    fi
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','false','true',d)}; then
        # Remove empty /usr/bin and /usr/sbin directories since the mount helper
        # is installed to /sbin
        rmdir --ignore-fail-on-non-empty ${D}${bindir} ${D}${sbindir}
    fi
}

PACKAGE_BEFORE_PN = "${PN}-scripts"
FILES:${PN} += "${base_libdir}/security"
FILES:${PN}-dbg += "${base_libdir}/security/.debug"
# these scripts are python, package them separately so python
# won't be mandatory dependency
FILES:${PN}-scripts = "${bindir}/smb2-quota ${bindir}/smbinfo"
RDEPENDS:${PN}-scripts += "python3-core"
RRECOMMENDS:${PN} = "kernel-module-cifs"
