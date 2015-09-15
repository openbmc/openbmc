require gstreamer1.0-plugins.inc

LICENSE = "GPLv2+ & LGPLv2.1+ & LGPLv2+"
LICENSE_FLAGS = "commercial"

DEPENDS += "gstreamer1.0-plugins-base libid3tag"

inherit gettext


PACKAGECONFIG ??= " \
    orc a52dec lame mad mpeg2dec \
    "

PACKAGECONFIG[a52dec]   = "--enable-a52dec,--disable-a52dec,liba52"
PACKAGECONFIG[cdio]     = "--enable-cdio,--disable-cdio,libcdio"
PACKAGECONFIG[dvdread]  = "--enable-dvdread,--disable-dvdread,libdvdread"
PACKAGECONFIG[lame]     = "--enable-lame,--disable-lame,lame"
PACKAGECONFIG[mad]      = "--enable-mad,--disable-mad,libmad"
PACKAGECONFIG[mpeg2dec] = "--enable-mpeg2dec,--disable-mpeg2dec,mpeg2dec"
PACKAGECONFIG[x264]     = "--enable-x264,--disable-x264,x264"


EXTRA_OECONF += " \
    --disable-amrnb \
    --disable-amrwb \
    --disable-sidplay \
    --disable-twolame \
    "

