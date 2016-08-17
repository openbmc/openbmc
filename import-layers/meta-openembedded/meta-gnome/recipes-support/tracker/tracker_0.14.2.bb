DESCRIPTION = "Tracker is a tool designed to extract information and metadata about your personal data so that it can be searched easily and quickly."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ee31012bf90e7b8c108c69f197f3e3a4"
DEPENDS = "file gstreamer dbus libexif gettext sqlite3 icu gst-plugins-base libgnome-keyring poppler tiff enca libgsf libunistring giflib taglib bzip2 upower gtk+3 libgee networkmanager"

RDEPENDS_${PN} += " gvfs gsettings-desktop-schemas"
HOMEPAGE = "http://projects.gnome.org/tracker/"

PR = "r7"

inherit autotools pkgconfig gnomebase gettext gsettings systemd gobject-introspection

VER_DIR = "${@gnome_verdir("${PV}")}"
SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/tracker/${VER_DIR}/tracker-${PV}.tar.xz \
           file://enable-sqlite-crosscompile.patch \
	   file://fix-removable-media-detection.patch \
           file://90tracker \
           file://tracker-store.service \
           file://tracker-miner-fs.service \
           file://tracker-dbus.service \
           "

SYSTEMD_SERVICE_${PN} = " tracker-store.service tracker-miner-fs.service tracker-dbus.service "
SYSTEMD_AUTO_ENABLE = "disable"

EXTRA_OECONF += " tracker_cv_have_ioprio=yes"

PACKAGECONFIG ?= "nautilus"
PACKAGECONFIG[nautilus] = "--enable-nautilus-extension,--disable-nautilus-extension,nautilus"

# Disable the desktop-centric miners
EXTRA_OECONF += "--disable-miner-thunderbird --disable-miner-firefox \
                 --disable-miner-evolution --disable-miner-flickr"

LEAD_SONAME = "libtrackerclient.so.0"

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/src/libtracker-sparql-backend/.libs:${B}/src/libtracker-data/.libs:${B}/src/libtracker-common/.libs"
}

do_install_append() {
    cp -PpR ${D}${STAGING_DATADIR}/* ${D}${datadir}/ || true
#   install -d ${D}/${sysconfdir}/X11/Xsession.d/
#   install -m 0755 ${WORKDIR}/90tracker  ${D}/${sysconfdir}/X11/Xsession.d/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tracker-store.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tracker-miner-fs.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tracker-dbus.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@LIBEXECDIR@,${libexecdir},g' \
           -e 's,@BASE_BINDIR@,${base_bindir},g' \
               ${D}${systemd_unitdir}/system/*.service
}

PACKAGES =+ "${PN}-tests ${PN}-vala ${PN}-nautilus-extension"

FILES_${PN} += "${datadir}/dbus-1/ \
                ${libdir}/tracker-${VER_DIR}/*.so.* \
                ${libdir}/tracker-${VER_DIR}/extract-modules/*.so \
                ${libdir}/tracker-${VER_DIR}/writeback-modules/*.so \
                ${datadir}/icons/hicolor/*/apps/tracker.* \
                ${libdir}/nautilus/extensions-2.0/*.la \
                ${datadir}/glib-2.0/schemas/* \
                ${systemd_unitdir}/system/tracker-store.service \
                ${systemd_unitdir}/system/tracker-miner-fs.service \
                ${systemd_unitdir}/system/tracker-dbus.service \
"

FILES_${PN}-dev += "${libdir}/tracker-${VER_DIR}/*.la \
                    ${libdir}/tracker-${VER_DIR}/*.so \
                    ${libdir}/tracker-${VER_DIR}/*/*.la \
                    ${libdir}/tracker-${VER_DIR}/extract-modules/*.la"

FILES_${PN}-staticdev += "${libdir}/nautilus/extensions-2.0/*.a"
FILES_${PN}-dbg += "${libdir}/*/*/.debug \
                    ${libdir}/*/.debug"
FILES_${PN}-tests = "${datadir}/tracker-tests/"
FILES_${PN}-vala = "${datadir}/vala/"
FILES_${PN}-nautilus-extension += "${libdir}/nautilus/extensions-2.0/*.so"

SRC_URI[md5sum] = "f3a871beeebf86fd752863ebd22af9ac"
SRC_URI[sha256sum] = "9b59330aa2e9e09feee587ded895e9247f71fc25f46b023d616d9969314bc7f1"
