SUMMARY = "PolicyKit Authorization Framework"
DESCRIPTION = "The polkit package is an application-level toolkit for defining and handling the policy that allows unprivileged processes to speak to privileged processes."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/polkit"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=155db86cdbafa7532b41f390409283eb \
                    file://src/polkit/polkit.h;beginline=1;endline=20;md5=0a8630b0133176d0504c87a0ded39db4"

DEPENDS = "expat glib-2.0 intltool-native mozjs-91"

inherit autotools gtk-doc pkgconfig useradd systemd gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "polkit"

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', \
                    bb.utils.contains('DISTRO_FEATURES', 'x11', 'consolekit', '', d), d)} \
                "

PACKAGECONFIG[pam] = "--with-authfw=pam,--with-authfw=shadow,libpam,libpam"
PACKAGECONFIG[systemd] = "--enable-libsystemd-login=yes --with-systemdsystemunitdir=${systemd_unitdir}/system/,--enable-libsystemd-login=no --with-systemdsystemunitdir=,systemd"
# there is no --enable/--disable option for consolekit and it's not picked by shlibs, so add it to RDEPENDS
PACKAGECONFIG[consolekit] = ",,,consolekit"

PAM_SRC_URI = "file://polkit-1_pam.patch"
SRC_URI = "http://www.freedesktop.org/software/polkit/releases/polkit-${PV}.tar.gz \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_SRC_URI}', '', d)} \
           file://0003-make-netgroup-support-optional.patch \
           file://0002-jsauthority-port-to-mozjs-91.patch \
           file://0003-jsauthority-ensure-to-call-JS_Init-and-JS_ShutDown-e.patch \
           "
SRC_URI[sha256sum] = "c8579fdb86e94295404211285fee0722ad04893f0213e571bd75c00972fd1f5c"

EXTRA_OECONF = "--with-os-type=moblin \
                --disable-man-pages \
                --disable-libelogind \
               "

do_compile:prepend () {
    export GIR_EXTRA_LIBS_PATH="${B}/src/polkit/.libs"
}

PACKAGES =+ "${PN}-examples"

FILES:${PN}:append = " \
    ${libdir}/${BPN}-1 \
    ${nonarch_libdir}/${BPN}-1 \
    ${datadir}/dbus-1 \
    ${datadir}/${BPN}-1 \
    ${datadir}/gettext \
"

FILES:${PN}-examples = "${bindir}/*example*"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/${BPN}-1 polkitd"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"
