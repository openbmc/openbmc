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

inherit gnomebase gsettings gobject-introspection vala gtk-doc manpages bash-completion features_check

SRC_URI[archive.md5sum] = "a317bc50c5e63dd0746a48af478fb92b"
SRC_URI[archive.sha256sum] = "7472aa28d7862620d3ca2bbec3b103df547d7319c12e95a7f7aa9f9f6dee4b19"
SRC_URI += "file://0001-meson.build-Just-warn-if-we-build-without-libseccomp.patch"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

PACKAGECONFIG ??= " \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "ffmpeg", "", d)} \
    flac \
    gexiv2 \
    gstreamer \
    icu \
    libexif \
    libgsf \
    jpeg \
    png \
    tiff \
    xml \
    pdf \
"

PACKAGECONFIG[ffmpeg]     = ",,ffmpeg"
PACKAGECONFIG[flac]       = "-Dflac=enabled,-Dflac=disabled,flac"
PACKAGECONFIG[gexiv2]     = ",,gexiv2"
PACKAGECONFIG[gstreamer]  = ",,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[gupnp]      = ",,gupnp-dlna"
PACKAGECONFIG[icu]        = ",,icu"
PACKAGECONFIG[libexif]    = "-Dexif=enabled,-Dexif=disabled,libexif"
PACKAGECONFIG[libgsf]     = "-Dgsf=enabled,-Dgsf=disabled,libgsf"
PACKAGECONFIG[jpeg]       = "-Djpeg=enabled,-Djpeg=disabled,jpeg"
PACKAGECONFIG[png]        = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[tiff]       = "-Dtiff=enabled,-Dtiff=disabled,tiff"
PACKAGECONFIG[xml]        = "-Dxml=enabled,-Dxml=disabled,libxml2"
PACKAGECONFIG[vorbis]     = "-Dvorbis=enabled,-Dvorbis=disabled,libvorbis"
PACKAGECONFIG[pdf]        = "-Dpdf=enabled,-Dpdf=disabled,poppler"
PACKAGECONFIG[upower]     = ",,upower"

# For security reasons it is strongly recommended to set add meta-security in
# your layers and 'libseccomp' to PACKAGECONFIG".
PACKAGECONFIG[libseccomp] = ",,libseccomp"
# not yet in meta-gnome
PACKAGECONFIG[rss]        = "-Dminer_rss=true,-Dminer_rss=false,libgrss"

EXTRA_OEMESON += " \
    -Dsystemd_user_services=${systemd_user_unitdir} \
"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/tracker \
    ${libdir}/tracker-miners-2.0 \
    ${systemd_user_unitdir} \
"
