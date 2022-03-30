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
    dcadec \
    dleyna-connector-dbus \
    dleyna-core \
    dleyna-renderer \
    dleyna-server \
    dvb-apps \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "faac fdk-aac mpd", "", d)} \
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
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "opencore-amr vo-aacenc vo-amrwbenc", "", d)} \
    gst-shark \
    gstd \
    rtmpdump \
    bigbuckbunny-1080p \
    bigbuckbunny-480p \
    bigbuckbunny-720p \
    tearsofsteel-1080p \
    schroedinger \
    pipewire \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "projucer", "", d)} \
    libcamera \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "libde265 openh264", "", d)} \
    vorbis-tools \
    libdvbcsa \
    libopenmpt \
    libuvc \
    mimic \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "minidlna", "", d)} \
    mycroft \
    openal-soft \
    opusfile \
    opus-tools \
    libdvdcss \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", bb.utils.contains("DISTRO_FEATURES", "x11", "vlc", "", d), "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "sox streamripper", "", d)} \
    tinyalsa \
    tremor \
    webrtc-audio-processing \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", bb.utils.contains_any("TRANSLATED_TARGET_ARCH", "i586 i686 x86-64", "x265", "", d), "", d)} \
"
RDEPENDS:packagegroup-meta-multimedia:remove:libc-musl = "projucer"
RDEPENDS:packagegroup-meta-multimedia:remove:powerpc64le = "openh264"

RDEPENDS:packagegroup-meta-multimedia-connectivity = "\
    gssdp \
    gupnp-av \
    gupnp-dlna \
    gupnp-igd \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gupnp-tools", "", d)} \
    gupnp \
    libupnp \
    ${@bb.utils.contains("DISTRO_FEATURES", "gobject-introspection-data", "rygel", "", d)} \
"
RDEPENDS:packagegroup-meta-multimedia-dvb = "\
    oscam \
    tvheadend \
"

RDEPENDS:packagegroup-meta-multimedia-mkv = "\
    libebml \
    libmatroska \
"

RDEPENDS:packagegroup-meta-multimedia-support = "\
    crossguid \
    ${@bb.utils.contains("DISTRO_FEATURES", "gobject-introspection-data", "libmediaart-2.0", "", d)} \
    gst-instruments \
    libsrtp \
    srt \
"
# devel headers/libraries only packages
# libsquish
