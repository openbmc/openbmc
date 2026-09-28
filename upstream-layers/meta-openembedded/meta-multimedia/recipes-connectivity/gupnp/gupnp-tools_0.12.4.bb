SUMMARY = "Tools for GUPnP"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gupnp gssdp  gtk+3 libsoup-3.0 libxml2 glib-2.0 "

inherit gnomebase features_check pkgconfig gettext gtk-icon-cache

ANY_OF_DISTRO_FEATURES = "x11 wayland"

SRC_URI[archive.sha256sum] = "2c7f6f538fb36a15322be532217e5de161182c943c0a8b0da4458cf970b4092f"

PACKAGECONFIG ??= "av-tools"
PACKAGECONFIG[av-tools] = "-Dav-tools=true,-Dav-tools=false,gupnp-av"
PACKAGECONFIG[gtksourceview] = ",,gtksourceview4"

CFLAGS += "-Wno-deprecated-declarations"

RRECOMMENDS:${PN} = "adwaita-icon-theme"
