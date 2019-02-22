SUMMARY = "Open Source multimedia player"
DESCRIPTION = "mpv is a fork of mplayer2 and MPlayer. It shares some features with the former projects while introducing many more."
SECTION = "multimedia"
HOMEPAGE = "http://www.mpv.io/"
DEPENDS = "zlib ffmpeg jpeg virtual/libx11 xsp libxv \
           libxscrnsaver libv4l libxinerama \
"

REQUIRED_DISTRO_FEATURES = "x11"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91f1cb870c1cc2d31351a4d2595441cb"

# While this item does not require it, it depends on ffmpeg which does
LICENSE_FLAGS = "commercial"

SRC_URI = " \
    https://github.com/mpv-player/mpv/archive/v${PV}.tar.gz;name=mpv \
    http://www.freehackers.org/~tnagy/release/waf-1.8.12;name=waf;downloadfilename=waf;subdir=${BPN}-${PV} \
"
SRC_URI[mpv.md5sum] = "038d0b660de07ff645ad6a741704ecab"
SRC_URI[mpv.sha256sum] = "daf3ef358d5f260f2269f7caabce27f446c291457ec330077152127133b71b46"
SRC_URI[waf.md5sum] = "cef4ee82206b1843db082d0b0506bf71"
SRC_URI[waf.sha256sum] = "01bf2beab2106d1558800c8709bc2c8e496d3da4a2ca343fe091f22fca60c98b"

inherit waf pkgconfig pythonnative distro_features_check

LUA ?= "lua"
LUA_mips64  = ""
LUA_aarch64  = ""
# Note: both lua and libass are required to get on-screen-display (controls)
PACKAGECONFIG ??= " \
    ${LUA} \
    libass \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)} \
"

PACKAGECONFIG_remove_aarch64 = "lua"
PACKAGECONFIG[drm] = "--enable-drm,--disable-drm,libdrm"
PACKAGECONFIG[gbm] = "--enable-gbm,--disable-gbm,virtual/mesa"
PACKAGECONFIG[lua] = "--enable-lua,--disable-lua,lua luajit"
PACKAGECONFIG[libass] = "--enable-libass,--disable-libass,libass"
PACKAGECONFIG[libarchive] = "--enable-libarchive,--disable-libarchive,libarchive"
PACKAGECONFIG[jack] = "--enable-jack, --disable-jack, jack"
PACKAGECONFIG[vaapi] = "--enable-vaapi, --disable-vaapi,libva"
PACKAGECONFIG[vdpau] = "--enable-vdpau, --disable-vdpau,libvdpau"
PACKAGECONFIG[wayland] = "--enable-wayland, --disable-wayland,wayland libxkbcommon"

SIMPLE_TARGET_SYS = "${@'${TARGET_SYS}'.replace('${TARGET_VENDOR}', '')}"

EXTRA_OECONF = " \
    --prefix=${prefix} \
    --target=${SIMPLE_TARGET_SYS} \
    --confdir=${sysconfdir} \
    --datadir=${datadir} \
    --disable-manpage-build \
    --disable-gl \
    --disable-libsmbclient \
    --disable-encoding \
    --disable-libbluray \
    --disable-dvdread \
    --disable-dvdnav \
    --disable-cdda \
    --disable-uchardet \
    --disable-rubberband \
    --disable-lcms2 \
    --disable-vapoursynth \
    --disable-vapoursynth-lazy \
    ${PACKAGECONFIG_CONFARGS} \
"

adjust_waf_perms() {
    chmod +x ${S}/waf
}

do_patch[postfuncs] += "adjust_waf_perms"

FILES_${PN} += "${datadir}/icons"
