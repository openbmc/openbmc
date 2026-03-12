SUMMARY = "Tools for GUPnP"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gupnp gssdp  gtk+3 libsoup-3.0 libxml2 glib-2.0 "

inherit gnomebase features_check pkgconfig gettext gtk-icon-cache

ANY_OF_DISTRO_FEATURES = "x11 wayland"

SRC_URI[archive.sha256sum] = "4c92f2d1a3d454ec1f5fb05ef08ca34df9c743af64c8b5965c35884d46cb005c"

PACKAGECONFIG ??= "av-tools"
PACKAGECONFIG[av-tools] = "-Dav-tools=true,-Dav-tools=false,gupnp-av"
PACKAGECONFIG[gtksourceview] = ",,gtksourceview4"

CFLAGS += "-Wno-deprecated-declarations"

RRECOMMENDS:${PN} = "adwaita-icon-theme"
