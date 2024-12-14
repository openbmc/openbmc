DESCRIPTION = "JACK example tools and clients"
SECTION = "libs/multimedia"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4641e94ec96f98fabc56ff9cc48be14b"

DEPENDS = "jack"

SRC_URI = "git://github.com/jackaudio/jack-example-tools.git;branch=main;protocol=https"
SRCREV = "33de8b4285fa5054af1b37fe0496c110604ed564"

S = "${WORKDIR}/git"

inherit meson pkgconfig

PACKAGECONFIG ??= "alsa_in_out jack_rec"
PACKAGECONFIG[alsa_in_out] = ",-Dalsa_in_out=disabled,alsa-lib"
PACKAGECONFIG[jack_net] = ",-Djack_net=disabled"
PACKAGECONFIG[jack_netsource] = ",-Djack_netsource=disabled,libopus"
PACKAGECONFIG[jack_rec] = ",-Djack_rec=disabled"
PACKAGECONFIG[opus_support] = ",-Dopus_support=disabled,libopus"
PACKAGECONFIG[readline] = ",-Dreadline_support=disabled,readline"

# ZALSA requires packages that are not available (libzita-alsa-pcmi and libzita-resampler)
EXTRA_OECONF = "-Dzalsa=disabled"

# jack recipe previously packaged jack-utils when these were part of the jack2 repository
# keep using jack-utils package name to provide compatibility with old image recipes
PACKAGES =+ "jack-utils"

FILES:jack-utils = " \
    ${bindir}/* \
    ${libdir}/jack/*.so \
"
