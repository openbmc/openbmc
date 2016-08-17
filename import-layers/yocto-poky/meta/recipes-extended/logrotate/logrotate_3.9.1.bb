SUMMARY = "Rotates, compresses, removes and mails system log files"
SECTION = "console/utils"
HOMEPAGE = "https://fedorahosted.org/logrotate/"
LICENSE = "GPLv2"

# TODO: logrotate 3.8.8 adds autotools/automake support, update recipe to use it.
# TODO: Document coreutils dependency. Why not RDEPENDS? Why not busybox?

DEPENDS="coreutils popt"

LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

SRC_URI = "https://fedorahosted.org/releases/l/o/logrotate/logrotate-${PV}.tar.gz \
           file://act-as-mv-when-rotate.patch \
           file://update-the-manual.patch \
           file://disable-check-different-filesystems.patch \
            "

SRC_URI[md5sum] = "4492b145b6d542e4a2f41e77fa199ab0"
SRC_URI[sha256sum] = "022769e3288c80981559a8421703c88e8438b447235e36dd3c8e97cd94c52545"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
"

PACKAGECONFIG[acl] = ",,acl"
PACKAGECONFIG[selinux] = ",,libselinux"

CONFFILES_${PN} += "${localstatedir}/lib/logrotate.status \
		    ${sysconfdir}/logrotate.conf"

# If RPM_OPT_FLAGS is unset, it adds -g itself rather than obeying our
# optimization variables, so use it rather than EXTRA_CFLAGS.
EXTRA_OEMAKE = "\
    LFS= \
    OS_NAME='${OS_NAME}' \
    'CC=${CC}' \
    'RPM_OPT_FLAGS=${CFLAGS}' \
    'EXTRA_LDFLAGS=${LDFLAGS}' \
    ${@bb.utils.contains('PACKAGECONFIG', 'acl', 'WITH_ACL=yes', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'selinux', 'WITH_SELINUX=yes', '', d)} \
"

# OS_NAME in the makefile defaults to `uname -s`. The behavior for
# freebsd/netbsd is questionable, so leave it as Linux, which only sets
# INSTALL=install and BASEDIR=/usr.
OS_NAME = "Linux"

do_compile_prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.depend
}

do_install(){
    oe_runmake install DESTDIR=${D} PREFIX=${D} MANDIR=${mandir}
    mkdir -p ${D}${sysconfdir}/logrotate.d
    mkdir -p ${D}${sysconfdir}/cron.daily
    mkdir -p ${D}${localstatedir}/lib
    install -p -m 644 examples/logrotate-default ${D}${sysconfdir}/logrotate.conf
    install -p -m 755 examples/logrotate.cron ${D}${sysconfdir}/cron.daily/logrotate
    touch ${D}${localstatedir}/lib/logrotate.status
}
