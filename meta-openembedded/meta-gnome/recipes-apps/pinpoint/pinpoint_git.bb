SUMMARY = "A simple presentation tool for hackers"
DESCRIPTION = "Pinpoint is a simple presentation tool that hopes to avoid audience death \
               by bullet point and instead encourage presentations containing beautiful \
               images and small amounts of concise text in slides."
SECTION = "x11/multimedia"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24 \
                    file://pinpoint.c;beginline=6;endline=17;md5=201d438283607da393fae6aca085454c"

DEPENDS = "glib-2.0 gdk-pixbuf cogl-1.0 clutter-1.0 clutter-gst-3.0 librsvg"

inherit autotools gettext pkgconfig distro_features_check

# cogl requires opengl
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI = "git://git.gnome.org/pinpoint"

SRCREV = "80a056c57e819d0b75035424638813b0670830e1"
PV = "0.1.4+gitr${SRCPV}"

S = "${WORKDIR}/git"

RRECOMMENDS_${PN} = "gdk-pixbuf-loader-jpeg gdk-pixbuf-loader-png"
