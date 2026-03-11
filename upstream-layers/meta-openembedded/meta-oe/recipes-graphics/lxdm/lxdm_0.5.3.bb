SUMMARY = "LXDM is the lightweight display manager"
HOMEPAGE = "http://blog.lxde.org/?p=531"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}%20${PV}/${BPN}-${PV}.tar.xz \
           file://lxdm.conf \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'file://lxdm-pam file://lxdm-pam-debug', '', d)} \
           ${@bb.utils.contains("IMAGE_FEATURES", "allow-root-login", "", "file://0001-lxdm.conf.in-blacklist-root-for-release-images.patch",d)} \
           file://0002-let-autotools-create-lxdm.conf.patch \
           file://0003-check-for-libexecinfo-providing-backtrace-APIs.patch \
           file://0004-fix-css-under-gtk-3.20.patch \
           file://0001-greeter-set-visible-when-switch-to-input-user.patch \
           file://0002-greeter-gdk.c-fix-typo.patch \
           file://0003-check-whether-password-expired-with-pam.patch \
           file://0004-lxdm.c-add-function-to-change-password-with-pam.patch \
           file://0005-ui.c-handle-password-expire-and-update-new-password.patch \
           file://0006-themes-Industrial-add-info-label-in-ui.patch \
           file://0007-greeter.c-support-to-update-expired-password.patch \
           file://0008-greeter.c-show-information-on-gtk-label-info.patch \
           file://0009-greeter.c-disallow-empty-new-password.patch \
           file://0001-systemd-lxdm.service-remove-plymouth-quit-conflicts.patch \
           file://0001-Initialize-msghdr-struct-in-a-portable-way.patch \
           "
SRC_URI[sha256sum] = "4891efee81c72a400cc6703e40aa76f3f3853833d048b72ec805da0f93567f2f"

PE = "1"

DEPENDS = "virtual/libintl intltool-native cairo dbus gdk-pixbuf glib-2.0 gtk+3 virtual/libx11 libxcb pango iso-codes"
DEPENDS += "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "", "consolekit", d)}"
DEPENDS:append:libc-musl = " libexecinfo"

inherit autotools pkgconfig gettext systemd features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

CFLAGS:append = " -fno-builtin-fork -fno-builtin-memset -fno-builtin-strstr "
LDFLAGS:append:libc-musl = " -lexecinfo"

EXTRA_OECONF += "--enable-gtk3=yes --enable-password=yes --with-x -with-xconn=xcb \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/ --disable-consolekit', '--without-systemdsystemunitdir', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--with-pam', '--without-pam', d)} \
"

do_configure:prepend() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/po/
}

do_compile:append() {
    # default background configured not available / no password field available / no default screensaver
    sed -i     -e 's,bg=,# bg=,g' \
        -e 's,# skip_password=,skip_password=,g' \
        -e 's,# arg=.*,arg=${bindir}/X -s 0,g' \
        ${S}/data/lxdm.conf.in
    # add default configuration
    oe_runmake -C ${B}/data lxdm.conf
}

do_install:append() {
    install -d ${D}${localstatedir}/lib/lxdm
    install -m 644 ${UNPACKDIR}/lxdm.conf ${D}${localstatedir}/lib/lxdm
    if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
        # ArchLinux version of pam config has the following advantages:
        # * simple setup of passwordless login
        # * in XFCE powerdown/restart enabled in logoff dialog
        install -m 644 ${UNPACKDIR}/${@bb.utils.contains_any("IMAGE_FEATURES", [ "allow-empty-password", "empty-root-password" ], "lxdm-pam-debug", "lxdm-pam",d)} \
            ${D}${sysconfdir}/pam.d/lxdm
    fi
}

# make installed languages choosable
pkg_postinst:${PN} () {
langs=""
for lang in `find $D${libdir}/locale -maxdepth 1 | grep _ | sort`; do
lang=`basename $lang`
if [ "x$langs" = "x" ]; then
    langs="$lang"
else
   langs="$langs $lang"
fi
done
sed -i "s:last_langs=.*$:last_langs=$langs:g" $D${localstatedir}/lib/lxdm/lxdm.conf
}

RDEPENDS:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-loginuid', '', d)} setxkbmap bash librsvg-gtk"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "lxdm.service"
