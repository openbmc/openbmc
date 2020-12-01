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
    alsa-equal \
    aom \
    caps \
    cdparanoia \
    dcadec \
    dleyna-connector-dbus \
    dleyna-core \
    dleyna-renderer \
    dleyna-server \
    dvb-apps \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "faac fdk-aac mpd", "", d)} \
    gerbera \
    libao \
    libavc1394 \
    libdc1394 \
    libdvbpsi \
    libdvdnav \
    libiec61883 \
    fluidsynth \
    libmusicbrainz \
    libmpdclient \
    mpc \
    ncmpc \
    libmpd \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "opencore-amr vo-aacenc vo-amrwbenc", "", d)} \
    gst-shark \
    gstd \
    rtmpdump \
    bigbuckbunny-1080p \
    bigbuckbunny-480p \
    bigbuckbunny-720p \
    tearsofsteel-1080p \
    schroedinger \
    projucer \
    libcamera \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "libde265 openh264", "", d)} \
    vorbis-tools \
    libdvbcsa \
    libopenmpt \
    libuvc \
    mimic \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "minidlna", "", d)} \
    mycroft \
    openal-soft \
    opusfile \
    libdvdcss \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "vlc", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "sox streamripper", "", d)} \
    tinyalsa \
    tremor \
    webrtc-audio-processing \
    ${@bb.utils.contains_any("TRANSLATED_TARGET_ARCH", "i586 x86-64", "x265", "", d)} \
"
RDEPENDS_packagegroup-meta-multimedia_remove_libc-musl = "projucer"

RDEPENDS_packagegroup-meta-multimedia-connectivity = "\
    gssdp \
    gupnp-av \
    gupnp-dlna \
    gupnp-igd \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gupnp-tools", "", d)} \
    gupnp \
    libupnp \
    rygel \
"
RDEPENDS_packagegroup-meta-multimedia-dvb = "\
    oscam \
    tvheadend \
"

RDEPENDS_packagegroup-meta-multimedia-mkv = "\
    libebml \
    libmatroska \
"

RDEPENDS_packagegroup-meta-multimedia-support = "\
    crossguid \
    libmediaart-2.0 \
    libmediaart \
    gst-instruments \
    libsrtp \
    srt \
"
# devel headers/libraries only packages
# libsquish
