DESCRIPTION = "PulseAudio Volume Control (pavucontrol) is a simple GTK based volume control tool ("mixer") for the PulseAudio sound server."
HOMEPAGE = "https://freedesktop.org/software/pulseaudio/pavucontrol/"
SECTION = "x11/multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

# glib-2.0-native is required for glib-gettextize, which is used by the
# AM_GLIB_GNU_GETTEXT macro in configure.ac. That macro is deprecated, so the
# glib-2.0-native dependency may go away at some point (something to keep in
# mind when doing version upgrades).
DEPENDS = "libxml-parser-perl-native intltool-native glib-2.0-native gtkmm3 libcanberra pulseaudio"

inherit autotools features_check perlnative

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://freedesktop.org/software/pulseaudio/${BPN}/${BP}.tar.xz \
          "
SRC_URI[md5sum] = "9dcc2c76292e7e5e075d51b8dcb20202"
SRC_URI[sha256sum] = "8fc45bac9722aefa6f022999cbb76242d143c31b314e2dbb38f034f4069d14e2"

EXTRA_OECONF = "--disable-lynx "

RDEPENDS_${PN} += "pulseaudio-server"
