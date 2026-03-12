SUMMARY = "Simple terminal emulator based on linux kernel mode setting (KMS)."
DESCRIPTION = "\
    Kmscon is a simple terminal emulator based on linux kernel mode setting \
    (KMS). It is an attempt to replace the in-kernel VT implementation with \
    a userspace console. \
"
HOMEPAGE = "https://github.com/kmscon/kmscon"
BUGTRACKER = "https://github.com/kmscon/kmscon/issues"
CVE_PRODUCT = "kmscon"

SECTION = "graphics"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d4602d249f8a3401040238e98367d9e"

DEPENDS = "\
    libtsm \
    libxkbcommon \
    udev \
"

SRC_URI = "git://github.com/kmscon/kmscon;protocol=https;branch=main;tag=v${PV}"
SRCREV = "9ce1dc0dc639e54e243a21d93e13f502a746f0a8"

inherit meson pkgconfig systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

PACKAGECONFIG[font_pango] = "-Dfont_pango=enabled, -Dfont_pango=disabled, pango"
PACKAGECONFIG[multi_seat] = "-Dmulti_seat=enabled, -Dmulti_seat=disabled, systemd"
PACKAGECONFIG[opengl] = "-Drenderer_gltex=enabled -Dvideo_drm3d=enabled, -Drenderer_gltex=disabled -Dvideo_drm3d=disabled, libdrm virtual/egl virtual/libgles2 virtual/libgbm"
PACKAGECONFIG[video_drm2d] = "-Dvideo_drm2d=enabled, -Dvideo_drm2d=disabled, libdrm"

EXTRA_OEMESON = "\
    -Delogind=disabled \
    -Dextra_debug=false \
    -Dfont_unifont=enabled \
    -Dsession_dummy=enabled \
    -Dsession_terminal=enabled \
    -Dtests=false \
    -Dvideo_fbdev=enabled \
"

SYSTEMD_SERVICE:${PN} = "kmscon.service"
FILES:${PN} += "${systemd_system_unitdir}/kmsconvt@.service"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/systemd/system
        ln -sf ${systemd_system_unitdir}/kmsconvt@.service \
            ${D}${sysconfdir}/systemd/system/autovt@.service
    else
        # kmscon always installs service files.
        # If systemd and usrmerge are not in DISTRO_FEATURES, packaging errors
        # occur because service files are installed to /usr/lib/systemd/system/kmscon.service.
        rm -rf ${D}${libdir}/systemd
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'opengl', 'true', 'false', d)}; then
        mv ${D}${sysconfdir}/kmscon/kmscon.conf.example ${D}${sysconfdir}/kmscon/kmscon.conf
        sed -e "s@#drm@drm@g" \
            -e "s@#hwaccel@hwaccel@g" \
            -i ${D}${sysconfdir}/kmscon/kmscon.conf
    fi
}

RDEPENDS:${PN} = "xkeyboard-config"
