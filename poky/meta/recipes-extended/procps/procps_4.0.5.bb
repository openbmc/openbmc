SUMMARY = "System and process monitoring utilities"
DESCRIPTION = "Procps contains a set of system utilities that provide system information about processes using \
the /proc filesystem. The package includes the programs ps, top, vmstat, w, kill, and skill."
HOMEPAGE = "https://gitlab.com/procps-ng/procps"
SECTION = "base"
LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4cf66a4984120007c9881cc871cf49db \
                    "

DEPENDS = "ncurses"

inherit autotools gettext pkgconfig update-alternatives

SRC_URI = "git://gitlab.com/procps-ng/procps.git;protocol=https;branch=master \
           file://sysctl.conf \
           "
SRCREV = "f46b2f7929cdfe2913ed0a7f585b09d6adbf994e"

S = "${WORKDIR}/git"

# Upstream has a custom autogen.sh which invokes po/update-potfiles as they
# don't ship a po/POTFILES.in (which is silly).  Without that file gettext
# doesn't believe po/ is a gettext directory and won't generate po/Makefile.
do_configure:prepend() {
    ( cd ${S} && po/update-potfiles )
}

EXTRA_OECONF = "--enable-skill --disable-modern-top"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemd,--without-systemd,systemd"

do_install:append () {
	install -d ${D}${base_bindir}
	[ "${bindir}" != "${base_bindir}" ] && for i in ${base_bindir_progs}; do mv ${D}${bindir}/$i ${D}${base_bindir}/$i; done
	install -d ${D}${base_sbindir}
	[ "${sbindir}" != "${base_sbindir}" ] && for i in ${base_sbindir_progs}; do mv ${D}${sbindir}/$i ${D}${base_sbindir}/$i; done
        if [ "${base_sbindir}" != "${sbindir}" ]; then
                rmdir ${D}${sbindir}
        fi

        install -d ${D}${sysconfdir}
        install -m 0644 ${UNPACKDIR}/sysctl.conf ${D}${sysconfdir}/sysctl.conf
        if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
                install -d ${D}${sysconfdir}/sysctl.d
                ln -sf ../sysctl.conf ${D}${sysconfdir}/sysctl.d/99-sysctl.conf
        fi
}

CONFFILES:${PN} = "${sysconfdir}/sysctl.conf"

bindir_progs = "free pkill pmap pgrep pwdx skill snice top uptime w"
base_bindir_progs += "kill pidof ps watch"
base_sbindir_progs += "sysctl"

ALTERNATIVE_PRIORITY = "200"
ALTERNATIVE_PRIORITY[pidof] = "150"

ALTERNATIVE:${PN} = "${bindir_progs} ${base_bindir_progs} ${base_sbindir_progs}"

ALTERNATIVE:${PN}-doc = "kill.1 uptime.1"
ALTERNATIVE_LINK_NAME[kill.1] = "${mandir}/man1/kill.1"
ALTERNATIVE_LINK_NAME[uptime.1] = "${mandir}/man1/uptime.1"

python __anonymous() {
    for prog in d.getVar('base_bindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir'), prog))

    for prog in d.getVar('base_sbindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_sbindir'), prog))
}

PROCPS_PACKAGES = "${PN}-lib \
                   ${PN}-ps \
                   ${PN}-sysctl"

PACKAGE_BEFORE_PN = "${PROCPS_PACKAGES}"
RDEPENDS:${PN} += "${PROCPS_PACKAGES}"

RDEPENDS:${PN}-ps += "${PN}-lib"
RDEPENDS:${PN}-sysctl += "${PN}-lib"

FILES:${PN}-lib = "${libdir}"
FILES:${PN}-ps = "${base_bindir}/ps.${BPN}"
FILES:${PN}-sysctl = "${base_sbindir}/sysctl.${BPN} ${sysconfdir}/sysctl.conf ${sysconfdir}/sysctl.d"

ALTERNATIVE:${PN}:remove = "ps"
ALTERNATIVE:${PN}:remove = "sysctl"

ALTERNATIVE:${PN}-ps = "ps"
ALTERNATIVE_TARGET[ps] = "${base_bindir}/ps"
ALTERNATIVE_LINK_NAME[ps] = "${base_bindir}/ps"

ALTERNATIVE:${PN}-sysctl = "sysctl"
ALTERNATIVE_TARGET[sysctl] = "${base_sbindir}/sysctl"
ALTERNATIVE_LINK_NAME[sysctl] = "${base_sbindir}/sysctl"
