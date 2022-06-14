DESCRIPTION = "PulseAudio Volume Control (pavucontrol) is a simple GTK based volume control tool ("mixer") for the PulseAudio sound server."
HOMEPAGE = "https://freedesktop.org/software/pulseaudio/pavucontrol/"
SECTION = "x11/multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

# glib-2.0-native is required for glib-gettextize, which is used by the
# AM_GLIB_GNU_GETTEXT macro in configure.ac. That macro is deprecated, so the
# glib-2.0-native dependency may go away at some point (something to keep in
# mind when doing version upgrades).
DEPENDS = "libxml-parser-perl-native intltool-native glib-2.0-native gtkmm3 pulseaudio json-glib"

inherit autotools features_check perlnative pkgconfig

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI = "http://freedesktop.org/software/pulseaudio/${BPN}/${BP}.tar.xz"
SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'file://0001-pavucontrol-remove-canberra-gtk-support.patch', '', d)}"

SRC_URI[sha256sum] = "ce2b72c3b5f1a70ad0df19dd81750f9455bd20870d1d3a36d20536af2e8f4e7a"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = ",,libcanberra"

EXTRA_OECONF = "--disable-lynx "

RDEPENDS:${PN} += "pulseaudio-server"
