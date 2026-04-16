SUMMARY = "PulseAudio Volume Control is a simple GTK based volume control tool for the PulseAudio sound server"
HOMEPAGE = "https://freedesktop.org/software/pulseaudio/pavucontrol/"
SECTION = "multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gtkmm4 pulseaudio json-glib"

inherit meson features_check pkgconfig gettext

ANY_OF_DISTRO_FEATURES = "opengl vulkan"

SRC_URI = "http://www.freedesktop.org/software/pulseaudio/${BPN}/${BP}.tar.xz"
SRC_URI[sha256sum] = "e93a7836c7307dcbc989e95fc7ec0878322514c475fabd90e89ed52fd4f15d32"

PACKAGECONFIG ?= ""
PACKAGECONFIG[audio-feedback] = "-Daudio-feedback=enabled,-Daudio-feedback=disabled,libcanberra"
PACKAGECONFIG[lynx] = "-Dlynx=enabled,-Dlynx=disabled,lynx"

FILES:${PN} += "${datadir}"

RDEPENDS:${PN} += "pulseaudio-server"
