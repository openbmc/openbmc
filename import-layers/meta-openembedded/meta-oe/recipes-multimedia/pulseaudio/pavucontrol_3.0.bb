DESCRIPTION = "PulseAudio Volume Control (pavucontrol) is a simple GTK based volume control tool ("mixer") for the PulseAudio sound server."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "intltool gtkmm libcanberra pulseaudio"

inherit gnome

SRC_URI = "http://freedesktop.org/software/pulseaudio/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "176308d2c03f8f3a7b2bd4f4d284fe71"
SRC_URI[sha256sum] = "b3d2ea5a25fc88dcee80c396014f72df1b4742f8cfbbc5349c39d64a0d338890"

EXTRA_OECONF = " --disable-gtk3 --disable-lynx "
