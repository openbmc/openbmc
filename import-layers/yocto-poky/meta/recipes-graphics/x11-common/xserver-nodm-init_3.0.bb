SUMMARY = "Simple Xserver Init Script (no dm)"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "x11"
PR = "r31"

SRC_URI = "file://xserver-nodm \
           file://Xserver \
           file://X11 \
           file://gplv2-license.patch \
           file://xserver-nodm.service.in \
           file://xserver-nodm.conf.in \
"

S = "${WORKDIR}"

# Since we refer to ROOTLESS_X which is normally enabled per-machine
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit update-rc.d systemd distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

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
    else
        XUSER_HOME=${ROOT_HOME}
        XUSER="root"
    fi
    sed -i "s:@HOME@:${XUSER_HOME}:; s:@USER@:${XUSER}:; s:@BLANK_ARGS@:${BLANK_ARGS}:" \
        ${D}${sysconfdir}/default/xserver-nodm
    sed -i "s:@NO_CURSOR_ARG@:${NO_CURSOR_ARG}:" ${D}${sysconfdir}/default/xserver-nodm

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/xserver-nodm.service.in ${D}${systemd_unitdir}/system/xserver-nodm.service
        sed -i "s:@USER@:${XUSER}:" ${D}${systemd_unitdir}/system/xserver-nodm.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
        install -d ${D}${sysconfdir}/init.d
        install xserver-nodm ${D}${sysconfdir}/init.d
    fi
}

RDEPENDS_${PN} = "xinit ${@oe.utils.conditional('ROOTLESS_X', '1', 'xuser-account', '', d)}"

INITSCRIPT_NAME = "xserver-nodm"
INITSCRIPT_PARAMS = "start 9 5 . stop 20 0 1 2 3 6 ."
SYSTEMD_SERVICE_${PN} = "xserver-nodm.service"

RCONFLICTS_${PN} = "xserver-common (< 1.34-r9) x11-common"
