DESCRIPTION = "Video player and streamer - davinci edition"
HOMEPAGE = "http://www.videolan.org"
SECTION = "multimedia"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libfribidi libtool libgcrypt libgcrypt-native \
   dbus dbus-glib libxml2 gnutls \
   tremor faad2 ffmpeg flac fluidsynth alsa-lib \
   lua-native lua libidn \
   avahi jpeg xz libmodplug mpeg2dec \
   libmtp libopus orc libsamplerate0 libusb1 schroedinger taglib \
   tiff"

SRC_URI = "http://download.videolan.org/pub/videolan/${BPN}/${PV}/${BP}.tar.xz"

inherit autotools gettext pkgconfig distro_features_check

ARM_INSTRUCTION_SET = "arm"

EXTRA_OECONF = "\
    --enable-run-as-root \
    --enable-xvideo \ 
    --disable-screen --disable-caca \
    --enable-httpd --enable-vlm \
    --enable-freetype \
    --enable-tremor \
    --enable-v4l2 --disable-aa --disable-faad \
    --enable-dbus \
    --without-contrib \
    --without-kde-solid \
    --enable-realrtsp \
    --disable-libtar \
    ac_cv_path_MOC=${STAGING_BINDIR_NATIVE}/moc4 \
    ac_cv_path_RCC=${STAGING_BINDIR_NATIVE}/rcc4 \
    ac_cv_path_UIC=${STAGING_BINDIR_NATIVE}/uic4 \
"

PACKAGECONFIG ?= " live555 dc1394 dv1394 notify fontconfig freetype dvdread png ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 sdl', '', d)}"
PACKAGECONFIG[mad] = "--enable-mad,--disable-mad,libmad"
PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,virtual/libsdl libsdl-image "
PACKAGECONFIG[a52] = "--enable-a52,--disable-a52,liba52"
PACKAGECONFIG[jack] = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[live555] = "--enable-live555,--disable-live555,live555"
PACKAGECONFIG[libass] = "--enable-libass,--disable-libass,libass"
PACKAGECONFIG[mkv] = "--enable-mkv,--disable-mkv,libmatroska libebml"
PACKAGECONFIG[postproc] = "--enable-postproc,--disable-postproc,libpostproc"
PACKAGECONFIG[opencv] = "--enable-opencv,--disable-opencv,opencv"
PACKAGECONFIG[libva] = "--enable-libva --enable-avcodec,--disable-libva --disable-avcodec,libva libav"
PACKAGECONFIG[speex] = "--enable-speex,--disable-speex,speex"
PACKAGECONFIG[gstreamer] = "--enable-gst-decode,--disable-gst-decode,gstreamer1.0 gstreamer1.0-plugins-base gst-plugins-bad"
PACKAGECONFIG[vpx] = "--enable-vpx,--disable-vpx, libvpx"
PACKAGECONFIG[qt4] = "--enable-qt,--disable-qt, qt4-x11-free"
PACKAGECONFIG[freerdp] = "--enable-freerdp,--disable-freerdp, freerdp"
PACKAGECONFIG[dvbpsi] = "--enable-dvbpsi,--disable-dvbpsi, libdvbpsi"
PACKAGECONFIG[gnome-vfs] = "--enable-gnomevfs,--disable-gnomevfs, gnome-vfs"
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
PACKAGECONFIG[notify] = "--enable-notify,--disable-notify, libnotify gtk+"
PACKAGECONFIG[fontconfig] = "--enable-fontconfig,--disable-fontconfig, fontconfig"
PACKAGECONFIG[freetype] = "--enable-freetype,--disable-freetype, freetype"
PACKAGECONFIG[dvdread] = "--enable-dvdread,--disable-dvdread, libdvdread libdvdcss"
PACKAGECONFIG[vnc] = "--enable-vnc,--disable-vnc, libvncserver"
PACKAGECONFIG[x11] = "--with-x --enable-xcb,--without-x --disable-xcb,  xcb-util-keysyms libxpm libxinerama"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"

do_configure_prepend() {
    cp ${STAGING_DATADIR}/libtool/config.* ${S}/autotools/ || true
}

do_configure_append() {
    sed -i -e s:'$(MOC) $(DEFS) $(CPPFLAGS)':'$(MOC) $(DEFS)'\ -I${B}/include\ -DSYS_LINUX:g ${B}/modules/gui/qt4/Makefile
    sed -i -e s:'${top_builddir_slash}libtool':'${top_builddir_slash}'${TARGET_SYS}-libtool:g ${B}/doltlibtool
}

# This recipe packages vlc as a library as well, so qt4 dependencies
# can be avoided when only the library is installed.
PACKAGES =+ "libvlc"

LEAD_SONAME_libvlc = "libvlc.so.5"
FILES_libvlc = "${libdir}/lib*.so.*"

FILES_${PN} += "\
    ${bindir}/vlc \
    ${datadir}/applications \
    ${datadir}/vlc/ \
    ${datadir}/icons \
"

FILES_${PN}-dbg += "\
    ${libdir}/vlc/*/.debug \
    ${libdir}/vlc/plugins/*/.debug \
"

FILES_${PN}-staticdev += "\
    ${libdir}/vlc/plugins/*/*.a \
"
