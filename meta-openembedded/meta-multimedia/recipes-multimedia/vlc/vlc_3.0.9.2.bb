DESCRIPTION = "Video player and streamer - davinci edition"
HOMEPAGE = "http://www.videolan.org"
SECTION = "multimedia"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "coreutils-native fribidi libtool libgcrypt libgcrypt-native \
   dbus libxml2 gnutls \
   tremor faad2 ffmpeg flac alsa-lib \
   lua-native lua libidn \
   avahi jpeg xz libmodplug mpeg2dec \
   libmtp libopus orc libsamplerate0 libusb1 schroedinger taglib \
   tiff"

LDFLAGS_append_riscv64 = " -latomic"

# While this item does not require it, it depends on ffmpeg which does
LICENSE_FLAGS = "commercial"

SRC_URI = "http://download.videolan.org/pub/videolan/${BPN}/${PV}/${BP}.tar.xz \
           file://0006-make-opencv-configurable.patch \
           file://0007-use-vorbisidec.patch \
           file://0008-fix-luaL-checkint.patch \
           file://0001-Use-packageconfig-to-detect-mmal-support.patch \
"
SRC_URI[sha256sum] = "a9bdad293d81cd48516abad8d490d8ab4012964ae541ff19e00021e071e47601"

inherit autotools features_check gettext pkgconfig mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

export BUILDCC = "${BUILD_CC} -std=c99"
EXTRA_OECONF = "\
    --enable-run-as-root \
    --enable-xvideo \
    --disable-screen --disable-caca \
    --enable-vlm \
    --enable-freetype \
    --enable-tremor \
    --enable-v4l2 --disable-aa --disable-faad \
    --enable-dbus \
    --without-contrib \
    --without-kde-solid \
    --enable-realrtsp \
    --disable-libtar \
    --enable-avcodec \
    ac_cv_path_MOC=${STAGING_BINDIR_NATIVE}${QT_DIR_NAME}/moc \
    ac_cv_path_RCC=${STAGING_BINDIR_NATIVE}${QT_DIR_NAME}/rcc \
    ac_cv_path_UIC=${STAGING_BINDIR_NATIVE}${QT_DIR_NAME}/uic \
"

PACKAGECONFIG ?= " \
    live555 dc1394 dv1394 notify fontconfig fluidsynth freetype dvdread png \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    x264 \
"

inherit ${@bb.utils.contains('PACKAGECONFIG', 'qt5', 'qmake5_paths', '', d)}

PACKAGECONFIG[mmal] = "--enable-omxil --enable-omxil-vout --enable-rpi-omxil --enable-mmal,,userland"
PACKAGECONFIG[x264] = "--enable-x264,--disable-x264,x264"
PACKAGECONFIG[fluidsynth] = ",,fluidsynth"
PACKAGECONFIG[mad] = "--enable-mad,--disable-mad,libmad"
PACKAGECONFIG[a52] = "--enable-a52,--disable-a52,liba52"
PACKAGECONFIG[jack] = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[live555] = "--enable-live555 LIVE555_PREFIX=${STAGING_DIR_HOST}${prefix},--disable-live555,live555"
PACKAGECONFIG[libass] = "--enable-libass,--disable-libass,libass"
PACKAGECONFIG[postproc] = "--enable-postproc,--disable-postproc,libpostproc"
PACKAGECONFIG[libva] = "--enable-libva,--disable-libva,libva"
PACKAGECONFIG[opencv] = "--enable-opencv,--disable-opencv,opencv"
PACKAGECONFIG[speex] = "--enable-speex,--disable-speex,speex"
PACKAGECONFIG[gstreamer] = "--enable-gst-decode,--disable-gst-decode,gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad"
PACKAGECONFIG[vpx] = "--enable-vpx,--disable-vpx, libvpx"
PACKAGECONFIG[qt5] = "--enable-qt,--disable-qt, qtbase-native qtx11extras qtsvg"
PACKAGECONFIG[freerdp] = "--enable-freerdp,--disable-freerdp, freerdp"
PACKAGECONFIG[dvbpsi] = "--enable-dvbpsi,--disable-dvbpsi, libdvbpsi"
PACKAGECONFIG[samba] = "--enable-smbclient,--disable-smbclient, samba"
PACKAGECONFIG[upnp] = "--enable-upnp,--disable-upnp,libupnp"
PACKAGECONFIG[dvdnav] = "--enable-dvdnav,--disable-dvdnav,libdvdnav libdvdcss"
PACKAGECONFIG[sftp] = "--enable-sftp,--disable-sftp,libssh2"
PACKAGECONFIG[vorbis] = "--enable-vorbis,--disable-vorbis,libvorbis libogg"
PACKAGECONFIG[ogg] = "--enable-ogg,--disable-ogg,libvorbis libogg"
PACKAGECONFIG[dc1394] = "--enable-dc1394,--disable-dc1394,libdc1394"
PACKAGECONFIG[dv1394] = "--enable-dv1394,--disable-dv1394,libraw1394 libavc1394"
PACKAGECONFIG[svg] = "--enable-svg,--disable-svg,librsvg"
PACKAGECONFIG[svgdec] = "--enable-svgdec,--disable-svgdec,librsvg cairo"
PACKAGECONFIG[notify] = "--enable-notify,--disable-notify, libnotify gtk+3"
PACKAGECONFIG[fontconfig] = "--enable-fontconfig,--disable-fontconfig, fontconfig"
PACKAGECONFIG[freetype] = "--enable-freetype,--disable-freetype, freetype"
PACKAGECONFIG[dvdread] = "--enable-dvdread,--disable-dvdread, libdvdread libdvdcss"
PACKAGECONFIG[vnc] = "--enable-vnc,--disable-vnc, libvncserver"
PACKAGECONFIG[x11] = "--with-x --enable-xcb,--without-x --disable-xcb,  xcb-util-keysyms libxpm libxinerama"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"
PACKAGECONFIG[vdpau] = "--enable-vdpau,--disable-vdpau,libvdpau"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland wayland-native"

do_configure_append() {
    sed -i -e s:'${top_builddir_slash}libtool':'${top_builddir_slash}'${TARGET_SYS}-libtool:g ${B}/doltlibtool

    # moc needs support: precreate build paths
    for qtpath in adapters components/epg components/playlist components/sout dialogs managers styles util/buttons; do
        mkdir -p "${B}/modules/gui/qt/$qtpath"
    done
}

# This recipe packages vlc as a library as well, so qt4 dependencies
# can be avoided when only the library is installed.
PACKAGES =+ "libvlc"

LEAD_SONAME_libvlc = "libvlc.so.5"
FILES_libvlc = "${libdir}/lib*.so.*"

FILES_${PN} += "\
    ${bindir}/vlc \
    ${libdir}/vlc/vlc/libvlc_vdpau.so \
    ${datadir}/applications \
    ${datadir}/vlc/ \
    ${datadir}/icons \
    ${datadir}/metainfo/vlc.appdata.xml \
"

FILES_${PN}-dbg += "\
    ${libdir}/vlc/*/.debug \
    ${libdir}/vlc/plugins/*/.debug \
"

FILES_${PN}-staticdev += "\
    ${libdir}/vlc/plugins/*/*.a \
"

INSANE_SKIP_${PN} = "dev-so"

