DESCRIPTION = "PulseAudio Volume Control (pavucontrol) is a simple GTK based volume control tool ("mixer") for the PulseAudio sound server."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "intltool-native gtk+3-native gtkmm3 libcanberra pulseaudio"

inherit gnomebase distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://freedesktop.org/software/pulseaudio/${BPN}/${BP}.tar.xz \
          "
SRC_URI[md5sum] = "9dcc2c76292e7e5e075d51b8dcb20202"
SRC_URI[sha256sum] = "8fc45bac9722aefa6f022999cbb76242d143c31b314e2dbb38f034f4069d14e2"

EXTRA_OECONF = "--disable-lynx "

RDEPENDS_${PN} += "pulseaudio-server"
