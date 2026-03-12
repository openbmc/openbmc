SUMMARY = "Remote desktop daemon for GNOME using pipewire."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig gettext gsettings features_check useradd

REQUIRED_DISTRO_FEATURES = "opengl polkit"

SRC_URI[archive.sha256sum] = "7800f388301eeb8147b0be55e8fa7cbd7521066a48a06115cc0ead9f0cea188a"

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
PACKAGECONFIG[rdp] = "-Drdp=true,-Drdp=false,freerdp3 fuse3 libxkbcommon shaderc-native"
PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"

do_install:append() {
	install -d ${D}${sysconfdir}/tmpfiles.d
	echo "d ${localstatedir}/lib/gnome-remote-desktop 700 gnome-remote-desktop gnome-remote-desktop - -" > ${D}${sysconfdir}/tmpfiles.d/gnome-remote-desktop.conf
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = " \
	--system \
	--no-create-home \
	--user-group \
	--home-dir ${localstatedir}/lib/gnome-remote-desktop \
	--shell /sbin/nologin \
	gnome-remote-desktop \
"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"
FILES:${PN} += "${systemd_user_unitdir} ${systemd_system_unitdir} ${datadir} ${libdir}/sysusers.d ${libdir}/tmpfiles.d"
