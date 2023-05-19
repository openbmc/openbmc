DESCRIPTION = "PulseAudio Preferences (paprefs) is a simple GTK based configuration dialog for the PulseAudio sound server."
HOMEPAGE = "https://freedesktop.org/software/pulseaudio/paprefs/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "pulseaudio gtkmm3 gtk+3 libsigc++-3 glibmm"

inherit meson pkgconfig features_check

# paprefs.cc includes gdk/gdkx.h and gdkx.h isn't provided by gtk3 without x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://freedesktop.org/software/pulseaudio/paprefs/${BP}.tar.xz"

SRC_URI[sha256sum] = "b3f21e40dc3936d15e3ffc910fb0c07c14b88e8c287715b456a948c17638f633"

EXTRA_OEMESON = "-Dlynx=false"

RDEPENDS:${PN} += "pulseaudio-server"
