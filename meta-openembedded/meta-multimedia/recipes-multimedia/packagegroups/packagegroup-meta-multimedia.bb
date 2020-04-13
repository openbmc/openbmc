SUMMARY = "Meta-multimedia packagegroups"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-multimedia \
    packagegroup-meta-multimedia-connectivity \
    packagegroup-meta-multimedia-dvb \
    packagegroup-meta-multimedia-mkv \
    packagegroup-meta-multimedia-support \
'

RDEPENDS_packagegroup-meta-multimedia = "\
    packagegroup-meta-multimedia \
    packagegroup-meta-multimedia-connectivity \
    packagegroup-meta-multimedia-dvb \
    packagegroup-meta-multimedia-mkv \
    packagegroup-meta-multimedia-support \
"

RDEPENDS_packagegroup-meta-multimedia = "\
    libdvbpsi libdc1394 gstd gst-shark \
    bigbuckbunny-720p tearsofsteel-1080p bigbuckbunny-1080p bigbuckbunny-480p \
    openal-soft dleyna-core dleyna-renderer dleyna-server dleyna-connector-dbus \
    alsa-equal libdvdnav libmusicbrainz tinyalsa \
    fluidsynth cdparanoia vorbis-tools tremor caps libao libavc1394 \
    opusfile gerbera libdvdcss webrtc-audio-processing \
    rtmpdump libopenmpt schroedinger mpd mpc libmpdclient \
    ncmpc libmpd dcadec libiec61883 \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "minidlna vlc", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "vo-aacenc sox libde265", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "streamripper", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "openh264 opencore-amr faac vo-amrwbenc", "", d)} \
    "

RDEPENDS_packagegroup-meta-multimedia-connectivity = "\
    rygel gupnp gupnp-igd gssdp gupnp-dlna gupnp-av libupnp \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gupnp-tools", "", d)} \
    "

RDEPENDS_packagegroup-meta-multimedia-dvb = "\
    oscam "

RDEPENDS_packagegroup-meta-multimedia-mkv = "\
    libmatroska libebml \
    "

RDEPENDS_packagegroup-meta-multimedia-support = "\
    libmediaart libmediaart-2.0 gst-instruments libsrtp crossguid \
    "
