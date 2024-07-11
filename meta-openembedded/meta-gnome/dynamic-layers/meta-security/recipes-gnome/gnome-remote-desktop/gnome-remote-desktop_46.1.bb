SUMMARY = "Remote desktop daemon for GNOME using pipewire."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext gsettings features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "7c62a4281fdfa9522110affbf75d09973035f2adc7fa4577511d733186beb68f"

DEPENDS = " \
    asciidoc-native \
    libdrm \
    libei \
    libepoxy \
    cairo \
    glib-2.0 \
    pipewire \
    polkit \
    libnotify \
    libopus \
    libsecret \
    nv-codec-headers \
    tpm2-tss \
"

PACKAGECONFIG ??= " \
    rdp \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,pipewire-native wireplumber-native dbus-native"
PACKAGECONFIG[vnc] = "-Dvnc=true,-Dvnc=false,libvncserver"
PACKAGECONFIG[rdp] = "-Drdp=true,-Drdp=false,freerdp3 fuse3 libxkbcommon"
PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"
FILES:${PN} += "${systemd_user_unitdir} ${systemd_system_unitdir} ${datadir} ${libdir}/sysusers.d ${libdir}/tmpfiles.d"
