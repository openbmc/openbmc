SUMMARY = "Tracker miners and metadata extractors"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING.GPL;md5=ee31012bf90e7b8c108c69f197f3e3a4 \
    file://COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
"

DEPENDS = " \
    intltool-native \
    tracker \
    zlib \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection vala bash-completion features_check

SRC_URI[archive.sha256sum] = "e4d361351b156f2862a524415fbebf0539c781cd87c6629662f0265b4803b8b8"
SRC_URI += "file://0001-meson.build-Just-warn-if-we-build-without-libseccomp.patch"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST:append = " introspection"

PACKAGECONFIG ??= " \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "ffmpeg", "", d)} \
    gexiv2 \
    gstreamer \
    icu \
    libexif \
    libgsf \
    jpeg \
    pdf \
    png \
    tiff \
    xml \
    ${@bb.utils.filter('DISTRO_FEATURES', 'seccomp', d)} \
"

PACKAGECONFIG[battery]     = "-Dbattery_detection=upower,-Dbattery_detection=none,upower"
PACKAGECONFIG[ffmpeg]     = ",,ffmpeg"
PACKAGECONFIG[gexiv2]     = ",,gexiv2"
PACKAGECONFIG[gstreamer]  = ",,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[gupnp]      = ",,gupnp-dlna"
PACKAGECONFIG[gif]        = "-Dgif=enabled,-Dgif=disabled,giflib"
PACKAGECONFIG[icu]        = ",,icu"
PACKAGECONFIG[jpeg]       = "-Djpeg=enabled,-Djpeg=disabled,jpeg"
PACKAGECONFIG[libexif]    = "-Dexif=enabled,-Dexif=disabled,libexif"
PACKAGECONFIG[libgsf]     = "-Dgsf=enabled,-Dgsf=disabled,libgsf"
PACKAGECONFIG[pdf]        = "-Dpdf=enabled,-Dpdf=disabled,poppler"
PACKAGECONFIG[png]        = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[tiff]       = "-Dtiff=enabled,-Dtiff=disabled,tiff"
PACKAGECONFIG[xml]        = "-Dxml=enabled,-Dxml=disabled,libxml2"
PACKAGECONFIG[networkmanager] = "-Dnetwork_manager=enabled,-Dnetwork_manager=disabled,networkmanger"

# For security reasons it is strongly recommended to set add meta-security in
# your layers and 'libseccomp' to PACKAGECONFIG".
PACKAGECONFIG[seccomp] = "-Dseccomp=true,-Dseccomp=false,libseccomp"
# not yet in meta-gnome
PACKAGECONFIG[rss]        = "-Dminer_rss=true,-Dminer_rss=false,libgrss"

EXTRA_OEMESON += " \
    -Dman=false \
    -Dsystemd_user_services=${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)} \
    -Dsystemd_user_services_dir=${systemd_user_unitdir} \
"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/tracker3-miners \
    ${libdir}/tracker-miners-3.0 \
    ${systemd_user_unitdir} \
"
