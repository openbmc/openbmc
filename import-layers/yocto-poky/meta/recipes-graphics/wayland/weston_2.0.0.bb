SUMMARY = "Weston, a Wayland compositor"
DESCRIPTION = "Weston is the reference implementation of a Wayland compositor"
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d79ee9e66bb0f95d3386a7acae780b70 \
                    file://libweston/compositor.c;endline=26;md5=e342df749174a8ee11065583157c7a38"

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://weston.png \
           file://weston.desktop \
           file://0001-make-error-portable.patch \
           file://0001-configure.ac-Fix-wayland-protocols-path.patch \
           file://xwayland.weston-start \
           file://0001-weston-launch-Provide-a-default-version-that-doesn-t.patch \
"
SRC_URI[md5sum] = "15f38945942bf2a91fe2687145fb4c7d"
SRC_URI[sha256sum] = "b4e446ac27f118196f1609dab89bb3cb3e81652d981414ad860e733b355365d8"

inherit autotools pkgconfig useradd distro_features_check
# depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "libxkbcommon gdk-pixbuf pixman cairo glib-2.0 jpeg"
DEPENDS += "wayland wayland-protocols libinput virtual/egl pango wayland-native"

EXTRA_OECONF = "--enable-setuid-install \
                --disable-rdp-compositor \
                WAYLAND_PROTOCOLS_SYSROOT_DIR=${RECIPE_SYSROOT} \
                "
EXTRA_OECONF_append_qemux86 = "\
		WESTON_NATIVE_BACKEND=fbdev-backend.so \
		"
EXTRA_OECONF_append_qemux86-64 = "\
		WESTON_NATIVE_BACKEND=fbdev-backend.so \
		"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'kms fbdev wayland egl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'xwayland', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd x11', d)} \
                   clients launch"
#
# Compositor choices
#
# Weston on KMS
PACKAGECONFIG[kms] = "--enable-drm-compositor,--disable-drm-compositor,drm udev virtual/mesa mtdev"
# Weston on Wayland (nested Weston)
PACKAGECONFIG[wayland] = "--enable-wayland-compositor,--disable-wayland-compositor,virtual/mesa"
# Weston on X11
PACKAGECONFIG[x11] = "--enable-x11-compositor,--disable-x11-compositor,virtual/libx11 libxcb libxcb libxcursor cairo"
# Headless Weston
PACKAGECONFIG[headless] = "--enable-headless-compositor,--disable-headless-compositor"
# Weston on framebuffer
PACKAGECONFIG[fbdev] = "--enable-fbdev-compositor,--disable-fbdev-compositor,udev mtdev"
# weston-launch
PACKAGECONFIG[launch] = "--enable-weston-launch,--disable-weston-launch,drm"
# VA-API desktop recorder
PACKAGECONFIG[vaapi] = "--enable-vaapi-recorder,--disable-vaapi-recorder,libva"
# Weston with EGL support
PACKAGECONFIG[egl] = "--enable-egl --enable-simple-egl-clients,--disable-egl --disable-simple-egl-clients,virtual/egl"
# Weston with cairo glesv2 support
PACKAGECONFIG[cairo-glesv2] = "--with-cairo-glesv2,--with-cairo=image,cairo"
# Weston with lcms support
PACKAGECONFIG[lcms] = "--enable-lcms,--disable-lcms,lcms"
# Weston with webp support
PACKAGECONFIG[webp] = "--with-webp,--without-webp,libwebp"
# Weston with unwinding support
PACKAGECONFIG[libunwind] = "--enable-libunwind,--disable-libunwind,libunwind"
# Weston with systemd-login support
PACKAGECONFIG[systemd] = "--enable-systemd-login,--disable-systemd-login,systemd dbus"
# Weston with Xwayland support (requires X11 and Wayland)
PACKAGECONFIG[xwayland] = "--enable-xwayland,--disable-xwayland"
# colord CMS support
PACKAGECONFIG[colord] = "--enable-colord,--disable-colord,colord"
# Clients support
PACKAGECONFIG[clients] = "--enable-clients --enable-simple-clients --enable-demo-clients-install,--disable-clients --disable-simple-clients"
# Weston with PAM support
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"

do_install_append() {
	# Weston doesn't need the .la files to load modules, so wipe them
	rm -f ${D}/${libdir}/libweston-2/*.la

	# If X11, ship a desktop file to launch it
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}" ]; then
		install -d ${D}${datadir}/applications
		install ${WORKDIR}/weston.desktop ${D}${datadir}/applications

		install -d ${D}${datadir}/icons/hicolor/48x48/apps
		install ${WORKDIR}/weston.png ${D}${datadir}/icons/hicolor/48x48/apps
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', 'yes', 'no', d)}" = "yes" ]; then
		install -Dm 644 ${WORKDIR}/xwayland.weston-start ${D}${datadir}/weston-start/xwayland
	fi
}

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', '${PN}-xwayland', '', d)} \
             libweston-2 ${PN}-examples"

FILES_${PN} = "${bindir}/weston ${bindir}/weston-terminal ${bindir}/weston-info ${bindir}/weston-launch ${bindir}/wcap-decode ${libexecdir} ${libdir}/${BPN}/*.so ${datadir}"

FILES_libweston-2 = "${libdir}/lib*${SOLIBS} ${libdir}/libweston-2/*.so"
SUMMARY_libweston-2 = "Helper library for implementing 'wayland window managers'."

FILES_${PN}-examples = "${bindir}/*"

FILES_${PN}-xwayland = "${libdir}/libweston-2/xwayland.so"
RDEPENDS_${PN}-xwayland += "xserver-xorg-xwayland"

RDEPENDS_${PN} += "xkeyboard-config"
RRECOMMENDS_${PN} = "liberation-fonts"
RRECOMMENDS_${PN}-dev += "wayland-protocols"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system weston-launch"
