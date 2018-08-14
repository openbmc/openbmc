SUMMARY = "Helpers for AV applications using UPnP"
DESCRIPTION = "GUPnP-AV is a collection of helpers for building AV (audio/video) applications using GUPnP."
LICENSE = "LGPLv2"
DEPENDS = "gupnp"

inherit autotools pkgconfig gobject-introspection vala

# Copy vapigen.m4 so that it doesn't get removed by vala class
# (normally this would be the right thing to do, but in gupnp-av the vapigen.m4 has only a custom macro)
do_configure_prepend() {
        cp -f ${S}/m4/vapigen.m4 ${S}/m4/vapigen-custom.m4 || true
}

