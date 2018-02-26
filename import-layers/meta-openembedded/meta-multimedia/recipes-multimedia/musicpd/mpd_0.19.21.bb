SUMMARY = "Music Player Daemon"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE ="http://www.musicpd.org"

inherit autotools useradd systemd pkgconfig

DEPENDS += " \
    alsa-lib \
    libsamplerate0 \
    libsndfile1 \
    libvorbis \
    libogg \
    faad2 \
    ffmpeg \
    curl \
    sqlite \
    bzip2 \
    pulseaudio \
    tcp-wrappers \
    openal-soft \
    yajl \
    jack \
    faad2 \
    flac \
    libao \
    fluidsynth \
    libcdio \
    wavpack \
    libopus \
    mpg123 \
    libmms \
    libmodplug \
    boost \
    icu \
    dbus \
    expat \
    zlib \
    libupnp1.6 \
"

# While this item does not require it, it depends on mpg123 which does
LICENSE_FLAGS = "commercial"

SRC_URI = " \
    http://www.musicpd.org/download/${BPN}/0.19/${BP}.tar.xz \
    file://mpd.conf.in \
"
SRC_URI[md5sum] = "fc19f76b512bd471b6af5214965ff7c5"
SRC_URI[sha256sum] = "8305b8bc026f4b6bde28b8dd09bfdddbe5590acf36358eed4d083a396e301730"

EXTRA_OECONF = "enable_bzip2=yes"
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/', '--without-systemdsystemunitdir', d)}"

PACKAGECONFIG[audiofile] = "--enable-audiofile,--disable-audiofile,audiofile"
PACKAGECONFIG[cdioparanoia] = "--enable-cdio-paranoia,--disable-cdio-paranoia,libcdio-paranoia"
PACKAGECONFIG[mad] = "--enable-mad,--disable-mad,libmad"
PACKAGECONFIG[id3tag] = "--enable-id3,--disable-id3,libid3tag"
PACKAGECONFIG[lame] = "--enable-lame-encoder,--disable-lame-encoder,lame"
PACKAGECONFIG[smb] = "--enable-smbclient,--disable-smbclient,samba"

do_configure_prepend() {
    sed -i -e 's|libsystemd-daemon|libsystemd|' ${S}/configure.ac
}

do_install_append() {
    install -d ${D}/${localstatedir}/lib/mpd/music
    chmod 775 ${D}/${localstatedir}/lib/mpd/music
    install -d ${D}/${localstatedir}/lib/mpd/playlists
    chown -R mpd ${D}/${localstatedir}/lib/mpd
    chown mpd:mpd ${D}/${localstatedir}/lib/mpd/music

    install -d ${D}/${sysconfdir}
    install -m 644 ${WORKDIR}/mpd.conf.in ${D}/${sysconfdir}/mpd.conf
    sed -i \
        -e 's|%music_directory%|${localstatedir}/lib/mpd/music|' \
        -e 's|%playlist_directory%|${localstatedir}/lib/mpd/playlists|' \
        -e 's|%db_file%|${localstatedir}/lib/mpd/mpd.db|' \
        -e 's|%log_file%|${localstatedir}/log/mpd.log|' \
        -e 's|%state_file%|${localstatedir}/lib/mpd/state|' \
        ${D}/${sysconfdir}/mpd.conf

    if [ -e ${D}/${systemd_unitdir}/system/mpd.service ] ; then
        sed -i \
            's|^ExecStart=.*|ExecStart=${bindir}/mpd --no-daemon|' \
            ${D}/${systemd_unitdir}/system/mpd.service
    fi
}

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "mpd.socket"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = " \
    --system --no-create-home \
    --home ${localstatedir}/lib/mpd \
    --groups audio \
    --user-group mpd"
