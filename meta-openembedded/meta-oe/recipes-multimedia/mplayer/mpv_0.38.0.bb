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
    libplacebo \
"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV_mpv = "02254b92dd237f03aa0a151c2a68778c4ea848f9"
SRC_URI = "git://github.com/mpv-player/mpv;name=mpv;branch=release/0.38;protocol=https \
           file://0001-file2string-Avoid-emitting-absolute-filepaths-into-g.patch \
           "

S = "${WORKDIR}/git"

inherit meson pkgconfig mime-xdg

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

PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,virtual/libx11 xsp libxv libxscrnsaver libxinerama libxpresent libxext"
PACKAGECONFIG[xv] = "-Dxv=enabled,-Dxv=disabled,libxv"
PACKAGECONFIG[opengl] = "-Dgl=enabled,-Dgl=disabled,virtual/libgl"
PACKAGECONFIG[egl] = "-Degl=enabled,-Degl-disabled,virtual/egl"
PACKAGECONFIG[drm] = "-Ddrm=enabled,-Ddrm=disabled,libdrm"
PACKAGECONFIG[gbm] = "-Dgbm=enabled,-Dgbm=disabled,virtual/libgbm"
PACKAGECONFIG[lua] = "-Dlua=luajit,-Dlua=disabled,lua luajit"
PACKAGECONFIG[libarchive] = "-Dlibarchive=enabled,-Dlibarchive=disabled,libarchive"
PACKAGECONFIG[jack] = "-Djack=enabled,-Djack=disabled,jack"
PACKAGECONFIG[vaapi] = "-Dvaapi=enabled,-Dvaapi=disabled,libva"
PACKAGECONFIG[vdpau] = "-Dvdpau=enabled,-Dvdpau=disabled,libvdpau"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,wayland wayland-native libxkbcommon"

python __anonymous() {
    packageconfig = (d.getVar("PACKAGECONFIG") or "").split()
    extras = []
    if "x11" in packageconfig and "opengl" in packageconfig:
        extras.append(" -Dgl-x11=enabled")
    if "x11" in packageconfig and "egl" in packageconfig:
        extras.append(" -Degl-x11=enabled")
    if "egl" in packageconfig and "drm" in packageconfig:
        extras.append(" -Degl-drm=enabled")
    if "vaapi" in packageconfig and "x11" in packageconfig:
        extras.append(" -Dvaapi-x11=enabled")
    if "vaapi" in packageconfig and "drm" in packageconfig:
        extras.append(" -Dvaapi-drm=enabled")
    if "vaapi" in packageconfig and "x11" in packageconfig and "egl" in packageconfig:
        extras.append(" -Dvaapi-x-egl=enabled")
    if "vdpau" in packageconfig and "opengl" in packageconfig and "x11" in packageconfig:
        extras.append(" -Dvdpau-gl-x11=enabled")
    if "wayland" in packageconfig and "opengl" in packageconfig:
        extras.append(" -Dgl-wayland=enabled")
    if "wayland" in packageconfig and "vaapi" in packageconfig:
        extras.append(" -Dvaapi-wayland=enabled")
    if extras:
        d.appendVar("EXTRA_OECONF", "".join(extras))
}

#SIMPLE_TARGET_SYS = "${@'${TARGET_SYS}'.replace('${TARGET_VENDOR}', '')}"

EXTRA_OECONF = " \
    -Dmanpage-build=disabled \
    -Dlibbluray=disabled \
    -Ddvdnav=disabled \
    -Dcdda=disabled \
    -Duchardet=disabled \
    -Drubberband=disabled \
    -Dlcms2=disabled \
    -Dvapoursynth=disabled \
    ${PACKAGECONFIG_CONFARGS} \
"

do_configure:append() {
    sed -i -e 's#${WORKDIR}#<WORKDIR>#g' ${B}/config.h
}

FILES:${PN} += " \
    ${datadir}/icons \
    ${datadir}/zsh \
    ${datadir}/bash-completion \
    ${datadir}/metainfo \
    "
EXCLUDE_FROM_WORLD = "${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "0", "1", d)}"
