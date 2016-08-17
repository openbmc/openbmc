DESCRIPTION = "The Enlightenment Window Manager Version 17"
DEPENDS = "eet evas eina ecore edje efreet edbus eeze eio elementary libxcb xcb-util-keysyms"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=76de290eb3fdda12121830191c152a7d"
SRCNAME = "e"

inherit e update-alternatives gettext pkgconfig
S = "${WORKDIR}/${SRCNAME}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
PACKAGECONFIG[pam] = "--enable-pam,--disable-pam,libpam"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"


EXTRA_OECONF = "\
    --with-edje-cc=${STAGING_BINDIR_NATIVE}/edje_cc \
    --with-eet-eet=${STAGING_BINDIR_NATIVE}/eet \
    --x-includes=${STAGING_INCDIR}/X11 \
    --x-libraries=${STAGING_LIBDIR} \
    --enable-simple-x11 \
"

do_configure_prepend() {
    autopoint || true
    sed '/^ *EFL_PKG_CHECK_VAR/ s/systemduserunitdir/systemdsystemunitdir/g' -i ${S}/configure.ac
}

do_install_append() {
    # customising - should rather make this simple upstream
    install -m 755 ${WORKDIR}/enlightenment_start.oe ${D}/${bindir}

    # security reasons, e-wm checks that in runtime
    # xinit[418]: ERROR: CONFIGURATION FILE HAS BAD PERMISSIONS
    chmod 600 ${D}/${sysconfdir}/enlightenment/sysactions.conf

    install -d ${D}/${sysconfdir}/xdg/menus
    install -m 644 ${WORKDIR}/applications.menu ${D}/${sysconfdir}/xdg/menus/
    for I in `find ${D}/${libdir}/enlightenment -name "*.a" -print`; do rm -f $I; done
    for I in `find ${D}/${libdir}/enlightenment -name "*.la" -print`; do rm -f $I; done

    # work around for issue caused in r78978, more infor in:
    # http://sourceforge.net/mailarchive/forum.php?thread_name=20121118194904.GA3438%40jama.jama.net&forum_name=enlightenment-devel
    mv ${D}/${libdir}/enlightenment/modules/policies ${D}/${libdir}/enlightenment/modules/illume2/ \
      || echo "illume2 policies are in correct place now"
    mv ${D}/${libdir}/enlightenment/modules/keyboards ${D}/${libdir}/enlightenment/modules/illume2/ \
      || echo "illume2 keyboards are in correct place now"
}

RDEPENDS_${PN} += "\
    shared-mime-info \
    mime-support \
    setxkbmap \
    edje-utils \
    ${PN}-utils \
    dbus-x11 \
"

# Uclibc build don't have 'glibc-utils'
RDEPENDS_${PN}_append_libc-glibc = " glibc-utils "

# The systray module used to be external, but is part of e-wm now
RREPLACES_${PN} = "systray"

RREPLACES_${PN}-config-mobile = "${PN}-config-illume2"
RCONFLICTS_${PN}-config-mobile = "${PN}-config-illume2"
RPROVIDES_${PN}-config-mobile = "${PN}-config-illume2"

PACKAGES =+ "\
    ${PN}-config-default \
    ${PN}-config-mobile \
    ${PN}-config-minimalist \
    ${PN}-config-netbook \
    ${PN}-config-scaleable \
    ${PN}-config-standard \
    ${PN}-config-tiling \
    ${PN}-theme-default \
    ${PN}-background-dark-gradient \
    ${PN}-background-light-gradient \
    ${PN}-backgrounds \
    ${PN}-images \
    ${PN}-icons \
    ${PN}-other \
    ${PN}-input-methods \
    ${PN}-sysactions \
    ${PN}-utils \
    ${PN}-menu \
    efm-desktop-icon \
    illume-keyboard-default-alpha \
    illume-keyboard-default-numeric \
    illume-keyboard-default-terminal \
"

ESYSACTIONS ?= "${PN}-sysactions"

RRECOMMENDS_${PN} = "\
    ${PN}-config-default \
    ${PN}-images \
    ${PN}-icons \
    ${PN}-other \
    ${PN}-input-methods \
    ${ESYSACTIONS} \
    efreet \
    evas-generic-loader-svg \
"

FILES_${PN} = "\
    ${bindir}/* \
    ${libdir}/enlightenment/utils/* \
    ${libdir}/enlightenment/modules/*/*.* \
    ${libdir}/enlightenment/modules/*/*/* \
    ${libdir}/enlightenment/modules/*/*/.order \
    ${libdir}/enlightenment/modules/keyboards/ignore_built_in_keyboards \
    ${libdir}/enlightenment/*plugins/*/*/* \
    ${libdir}/enlightenment/preload/e_precache.so \
    ${datadir}/enlightenment/data/icons \
    ${datadir}/enlightenment/data/favorites \
    ${datadir}/enlightenment/data/input_methods \
    ${datadir}/enlightenment/data/config/profile.cfg \
    ${datadir}/enlightenment/AUTHORS \
    ${datadir}/enlightenment/COPYING \
    ${datadir}/xsessions/enlightenment.desktop \
    ${sysconfdir}/xdg \
    ${systemd_unitdir} \
"

FILES_${PN}-config-default = "${datadir}/enlightenment/data/config/default"
FILES_${PN}-config-mobile = "${datadir}/enlightenment/data/config/mobile"
FILES_${PN}-config-minimalist = "${datadir}/enlightenment/data/config/minimalist"
FILES_${PN}-config-netbook = "${datadir}/enlightenment/data/config/netbook"
FILES_${PN}-config-scaleable = "${datadir}/enlightenment/data/config/scaleable"
FILES_${PN}-config-standard = "${datadir}/enlightenment/data/config/standard"
FILES_${PN}-config-tiling = "${datadir}/enlightenment/data/config/tiling"
FILES_${PN}-theme-default = "${datadir}/enlightenment/data/themes/default.edj"
FILES_${PN}-theme-default = "${datadir}/enlightenment/data/themes/default.edj"
FILES_${PN}-background-dark-gradient = "${datadir}/enlightenment/data/backgrounds/Dark_Gradient.edj"
FILES_${PN}-background-light-gradient = "${datadir}/enlightenment/data/backgrounds/Light_Gradient.edj"
FILES_${PN}-backgrounds = "${datadir}/enlightenment/data/backgrounds/*.edj"
FILES_${PN}-images = "${datadir}/enlightenment/data/images ${datadir}/enlightenment/data/flags"
FILES_${PN}-icons = "${datadir}/enlightenment/data/icons"
FILES_${PN}-other = "${datadir}/enlightenment/data/other"
FILES_${PN}-input-methods = "${datadir}/enlightenment/data/input_methods"
FILES_${PN}-sysactions = "${sysconfdir}/enlightenment/sysactions.conf"
FILES_${PN}-utils = "${libdir}/enlightenment/utils/*"
FILES_${PN}-menu = "${sysconfdir}/xdg/menus/applications.menu"

FILES_efm-desktop-icon = "\
    ${datadir}/applications/efm.desktop \
    ${datadir}/applications/enlightenment_filemanager.desktop \
    ${datadir}/icons/e-module-fileman.png \
"

KEYBOARDS_DIR="${libdir}/enlightenment/modules/illume-keyboard/keyboards"
FILES_illume-keyboard-default-alpha = "\
    ${KEYBOARDS_DIR}/Default.kbd \
    ${KEYBOARDS_DIR}/alpha.png \
"
FILES_illume-keyboard-default-numeric = "\
    ${KEYBOARDS_DIR}/Numbers.kbd \
    ${KEYBOARDS_DIR}/numeric.png \
"
FILES_illume-keyboard-default-terminal = "\
    ${KEYBOARDS_DIR}/Terminal.kbd \
    ${KEYBOARDS_DIR}/qwerty.png \
"

RRECOMMENDS_${PN}-config-default = "${PN}-theme-default"
RRECOMMENDS_${PN}-config-mobile = "\
    illume-keyboard-default-alpha \
    illume-keyboard-default-numeric \
    illume-keyboard-default-terminal \
"

RRECOMMENDS_${PN}-config-minimalist = "\
    ${PN}-background-light-gradient \
    ${PN}-theme-default \
"
RRECOMMENDS_${PN}-config-netbook = "\
    ${PN}-background-dark-gradient \
    ${PN}-theme-default \
"
RRECOMMENDS_${PN}-config-scaleable = "${PN}-theme-default"
RRECOMMENDS_${PN}-config-standard = "${PN}-theme-default"

FILES_${PN}-dbg += "\
    ${libdir}/enlightenment/modules/*/*/.debug/ \
    ${libdir}/enlightenment/modules/policies/.debug/ \
    ${libdir}/enlightenment/preload/.debug/ \
    ${libdir}/enlightenment/utils/.debug/ \
    ${libdir}/enlightenment/*plugins/*/*/.debug \
"

FILES_${PN}-doc += "\
    ${datadir}/enlightenment/doc \
"

CONFFILES_${PN}-menu = "${sysconfdir}/xdg/menus/applications.menu"
CONFFILES_${PN}-sysactions = "/etc/enlightenment/sysactions.conf"

ALTERNATIVE_${PN} = "x-window-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/enlightenment_start.oe"
ALTERNATIVE_PRIORITY[x-window-manager] = "16"
