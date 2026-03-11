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

RDEPENDS:packagegroup-meta-multimedia = "\
    packagegroup-meta-multimedia \
    packagegroup-meta-multimedia-connectivity \
    packagegroup-meta-multimedia-dvb \
    packagegroup-meta-multimedia-mkv \
    packagegroup-meta-multimedia-support \
"

RDEPENDS:packagegroup-meta-multimedia = "\
    alsa-equal \
    aom \
    bluealsa \
    caps \
    cdparanoia \
    dvb-apps \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "faac mpd", "", d)} \
    gerbera \
    libavc1394 \
    libiec61883 \
    libmusicbrainz \
    mpc \
    ncmpc \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "opencore-amr", "", d)} \
    gstd \
    rtmpdump \
    bigbuckbunny-1080p \
    bigbuckbunny-480p \
    bigbuckbunny-720p \
    tearsofsteel-1080p \
    pipewire \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", bb.utils.contains("DISTRO_FEATURES", "x11", "projucer", "", d), "", d)} \
    libcamera \
    vorbis-tools \
    libopenmpt \
    mimic \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "minidlna", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pulseaudio", "mycroft", "", d)} \
    openal-soft \
    opusfile \
    opus-tools \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "sox streamripper", "", d)} \
    tinyalsa \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "x265", "", d)} \
"
RDEPENDS:packagegroup-meta-multimedia:remove:libc-musl = "projucer"

RDEPENDS:packagegroup-meta-multimedia-connectivity = "\
    gupnp-dlna \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gupnp-tools", "", d)} \
    libupnp \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", bb.utils.contains("DISTRO_FEATURES", "gobject-introspection-data", "rygel", "", d), "", d)} \
"
RDEPENDS:packagegroup-meta-multimedia-dvb = "\
    oscam \
    tvheadend \
"

RDEPENDS:packagegroup-meta-multimedia-support = "\
    crossguid \
    gst-instruments \
"
# devel headers/libraries only packages
# libsquish
