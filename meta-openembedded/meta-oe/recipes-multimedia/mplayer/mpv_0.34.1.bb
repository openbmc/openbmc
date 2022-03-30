SUMMARY = "Open Source multimedia player"
DESCRIPTION = "mpv is a fork of mplayer2 and MPlayer. It shares some features with the former projects while introducing many more."
SECTION = "multimedia"
HOMEPAGE = "http://www.mpv.io/"

DEPENDS = " \
    zlib \
    ffmpeg \
    jpeg \
    libv4l \
    libass \
"

DEPENDS += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' virtual/libx11 xsp libxv libxscrnsaver libxinerama', '', d)} \
"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV_mpv = "349e437466163cb52f7d0aa227d4606edd9db501"
SRC_URI = "git://github.com/mpv-player/mpv;name=mpv;branch=release/0.34;protocol=https \
           https://waf.io/waf-2.0.20;name=waf;subdir=git \
           "
SRC_URI[waf.sha256sum] = "bf971e98edc2414968a262c6aa6b88541a26c3cd248689c89f4c57370955ee7f"

S = "${WORKDIR}/git"

inherit waf pkgconfig mime-xdg

LDFLAGS:append:riscv64 = " -latomic"

LUA ?= "lua"
LUA:mips64  = ""
LUA:powerpc64  = ""
LUA:powerpc64le  = ""
LUA:riscv64  = ""
LUA:riscv32  = ""
LUA:powerpc  = ""

# Note: lua is required to get on-screen-display (controls)
PACKAGECONFIG ??= " \
    ${LUA} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland egl', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)} \
"

PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11"
PACKAGECONFIG[xv] = "--enable-xv,--disable-xv,libxv"
PACKAGECONFIG[opengl] = "--enable-gl,--disable-gl,virtual/libgl"
PACKAGECONFIG[egl] = "--enable-egl,--disable-egl,virtual/egl"
PACKAGECONFIG[drm] = "--enable-drm,--disable-drm,libdrm"
PACKAGECONFIG[gbm] = "--enable-gbm,--disable-gbm,virtual/libgbm"
PACKAGECONFIG[lua] = "--enable-lua,--disable-lua,lua luajit"
PACKAGECONFIG[libarchive] = "--enable-libarchive,--disable-libarchive,libarchive"
PACKAGECONFIG[jack] = "--enable-jack, --disable-jack, jack"
PACKAGECONFIG[vaapi] = "--enable-vaapi,--disable-vaapi,libva"
PACKAGECONFIG[vdpau] = "--enable-vdpau,--disable-vdpau,libvdpau"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland wayland-native libxkbcommon"

python __anonymous() {
    packageconfig = (d.getVar("PACKAGECONFIG") or "").split()
    extras = []
    if "x11" in packageconfig and "opengl" in packageconfig:
        extras.append(" --enable-gl-x11")
    if "x11" in packageconfig and "egl" in packageconfig:
        extras.append(" --enable-egl-x11")
    if "egl" in packageconfig and "drm" in packageconfig:
        extras.append(" --enable-egl-drm")
    if "vaapi" in packageconfig and "x11" in packageconfig:
        extras.append(" --enable-vaapi-x11")
    if "vaapi" in packageconfig and "drm" in packageconfig:
        extras.append(" --enable-vaapi-drm")
    if "vaapi" in packageconfig and "x11" in packageconfig and "egl" in packageconfig:
        extras.append(" --enable-vaapi-x-egl")
    if "vdpau" in packageconfig and "opengl" in packageconfig and "x11" in packageconfig:
        extras.append(" --enable-vdpau-gl-x11")
    if "wayland" in packageconfig and "opengl" in packageconfig:
        extras.append(" --enable-gl-wayland")
    if "wayland" in packageconfig and "vaapi" in packageconfig:
        extras.append(" --enable-vaapi-wayland")
    if extras:
        d.appendVar("EXTRA_OECONF", "".join(extras))
}

SIMPLE_TARGET_SYS = "${@'${TARGET_SYS}'.replace('${TARGET_VENDOR}', '')}"

EXTRA_OECONF = " \
    --prefix=${prefix} \
    --target=${SIMPLE_TARGET_SYS} \
    --confdir=${sysconfdir} \
    --datadir=${datadir} \
    --disable-manpage-build \
    --disable-libbluray \
    --disable-dvdnav \
    --disable-cdda \
    --disable-uchardet \
    --disable-rubberband \
    --disable-lcms2 \
    --disable-vapoursynth \
    ${PACKAGECONFIG_CONFARGS} \
"

link_waf() {
    ln -s waf-2.0.20 ${S}/waf
}
do_unpack[postfuncs] += "link_waf"

FILES:${PN} += " \
    ${datadir}/icons \
    ${datadir}/zsh \
    ${datadir}/bash-completion \
    "
EXCLUDE_FROM_WORLD = "${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "0", "1", d)}"
