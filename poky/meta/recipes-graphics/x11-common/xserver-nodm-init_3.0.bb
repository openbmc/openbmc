SUMMARY = "Simple Xserver Init Script (no dm)"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "x11"
PR = "r31"

SRC_URI = "file://xserver-nodm \
           file://Xserver \
           file://X11 \
           file://gplv2-license.patch \
           file://xserver-nodm.service.in \
           file://xserver-nodm.conf.in \
           file://capability.conf \
"

S = "${WORKDIR}"

# Since we refer to ROOTLESS_X which is normally enabled per-machine
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit update-rc.d systemd features_check

REQUIRED_DISTRO_FEATURES = "x11 ${@oe.utils.conditional('ROOTLESS_X', '1', 'pam', '', d)}"

PACKAGECONFIG ??= "blank"
# dpms and screen saver will be on only if 'blank' is in PACKAGECONFIG
PACKAGECONFIG[blank] = ""
PACKAGECONFIG[nocursor] = ""

do_install() {
    install -d ${D}${sysconfdir}/default
    install xserver-nodm.conf.in ${D}${sysconfdir}/default/xserver-nodm
    install -d ${D}${sysconfdir}/xserver-nodm
    install Xserver ${D}${sysconfdir}/xserver-nodm/Xserver
    install -d ${D}${sysconfdir}/X11/Xsession.d
    install X11/Xsession.d/* ${D}${sysconfdir}/X11/Xsession.d/
    install X11/Xsession ${D}${sysconfdir}/X11/

    BLANK_ARGS="${@bb.utils.contains('PACKAGECONFIG', 'blank', '', '-s 0 -dpms', d)}"
    NO_CURSOR_ARG="${@bb.utils.contains('PACKAGECONFIG', 'nocursor', '-nocursor', '', d)}"
    if [ "${ROOTLESS_X}" = "1" ] ; then
        XUSER_HOME="/home/xuser"
        XUSER="xuser"
        install -D capability.conf ${D}${sysconfdir}/security/capability.conf
        sed -i "s:@USER@:${XUSER}:" ${D}${sysconfdir}/security/capability.conf
    else
        XUSER_HOME=${ROOT_HOME}
        XUSER="root"
    fi
    sed -i "s:@HOME@:${XUSER_HOME}:; s:@USER@:${XUSER}:; s:@BLANK_ARGS@:${BLANK_ARGS}:" \
        ${D}${sysconfdir}/default/xserver-nodm
    sed -i "s:@NO_CURSOR_ARG@:${NO_CURSOR_ARG}:" ${D}${sysconfdir}/default/xserver-nodm

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/xserver-nodm.service.in ${D}${systemd_system_unitdir}/xserver-nodm.service
        sed -i "s:@USER@:${XUSER}:" ${D}${systemd_system_unitdir}/xserver-nodm.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
        install -d ${D}${sysconfdir}/init.d
        install xserver-nodm ${D}${sysconfdir}/init.d
    fi
}

RDEPENDS:${PN} = "xinit ${@oe.utils.conditional('ROOTLESS_X', '1', 'xuser-account libcap libcap-bin', '', d)}"

INITSCRIPT_NAME = "xserver-nodm"
INITSCRIPT_PARAMS = "start 9 5 . stop 20 0 1 2 3 6 ."
SYSTEMD_SERVICE:${PN} = "xserver-nodm.service"

RCONFLICTS:${PN} = "xserver-common (< 1.34-r9) x11-common"
