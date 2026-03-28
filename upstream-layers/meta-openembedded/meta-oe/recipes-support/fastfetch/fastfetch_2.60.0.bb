SUMMARY = "A maintained, feature-rich and performance oriented, neofetch like system information tool."
DESCRIPTION = "Fastfetch is a neofetch-like tool for fetching system \
information and displaying it in a visually appealing way. It is written \
mainly in C, with a focus on performance and customizability."
HOMEPAGE = "https://github.com/fastfetch-cli/fastfetch"
BUGTRACKER = "https://github.com/fastfetch-cli/fastfetch/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2090e7d93df7ad5a3d41f6fb4226ac76"

DEPENDS = "yyjson"

SRC_URI = "git://github.com/fastfetch-cli/fastfetch.git;protocol=https;branch=dev;tag=${PV}"
SRCREV = "06ecc9c4805794ca8d632844e64950e82a6d5f7e"

inherit cmake pkgconfig

EXTRA_OECMAKE += "\
    -DENABLE_SYSTEM_YYJSON=ON \
    -DENABLE_DIRECTX_HEADERS=OFF \
"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'egl', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'glx', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xcb xrandr', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'opencl pulseaudio vulkan wayland', d)} \
    dbus \
    drm \
    sqlite3 \
"

PACKAGECONFIG[chafa] = "-DENABLE_CHAFA=ON,-DENABLE_CHAFA=OFF,chafa"
PACKAGECONFIG[dbus] = "-DENABLE_DBUS=ON,-DENABLE_DBUS=OFF,dbus"
PACKAGECONFIG[dconf] = "-DENABLE_DCONF=ON,-DENABLE_DCONF=OFF,dconf"
PACKAGECONFIG[ddcutil] = "-DENABLE_DDCUTIL=ON,-DENABLE_DDCUTIL=OFF,ddcutil"
PACKAGECONFIG[drm-amdgpu] = "-DENABLE_DRM_AMDGPU=ON,-DENABLE_DRM_AMDGPU=OFF,"
PACKAGECONFIG[drm] = "-DENABLE_DRM=ON,-DENABLE_DRM=OFF,libdrm"
PACKAGECONFIG[egl] = "-DENABLE_EGL=ON,-DENABLE_EGL=OFF,virtual/egl"
PACKAGECONFIG[elf] = "-DENABLE_ELF=ON,-DENABLE_ELF=OFF,elfutils"
PACKAGECONFIG[flashfetch] = "-DBUILD_FLASHFETCH=ON,-DBUILD_FLASHFETCH=OFF,"
PACKAGECONFIG[freetype] = "-DENABLE_FREETYPE=ON,-DENABLE_FREETYPE=OFF,freetype"
PACKAGECONFIG[gio] = "-DENABLE_GIO=ON,-DENABLE_GIO=OFF,glib-2.0"
PACKAGECONFIG[glx] = "-DENABLE_GLX=ON,-DENABLE_GLX=OFF,virtual/libgles2"
PACKAGECONFIG[imagemagick] = "-DENABLE_IMAGEMAGICK7=ON -DENABLE_IMAGEMAGICK6=OFF,-DENABLE_IMAGEMAGICK7=OFF -DENABLE_IMAGEMAGICK6=OFF,imagemagick"
PACKAGECONFIG[opencl] = "-DENABLE_OPENCL=ON,-DENABLE_OPENCL=OFF,opencl-headers virtual/libopencl1"
PACKAGECONFIG[pulseaudio] = "-DENABLE_PULSE=ON,-DENABLE_PULSE=OFF,pulseaudio"
PACKAGECONFIG[rpm] = "-DENABLE_RPM=ON,-DENABLE_RPM=OFF,rpm"
PACKAGECONFIG[sqlite3] = "-DENABLE_SQLITE3=ON,-DENABLE_SQLITE3=OFF,sqlite3"
PACKAGECONFIG[vulkan] = "-DENABLE_VULKAN=ON,-DENABLE_VULKAN=OFF,vulkan-loader"
PACKAGECONFIG[wayland] = "-DENABLE_WAYLAND=ON,-DENABLE_WAYLAND=OFF,wayland"
PACKAGECONFIG[xcb] = "-DENABLE_XCB_RANDR=ON,-DENABLE_XCB_RANDR=OFF,libxcb"
PACKAGECONFIG[xrandr] = "-DENABLE_XRANDR=ON,-DENABLE_XRANDR=OFF,libxrandr"
PACKAGECONFIG[zfs] = "-DENABLE_LIBZFS=ON,-DENABLE_LIBZFS=OFF,zfs"
PACKAGECONFIG[zlib] = "-DENABLE_ZLIB=ON,-DENABLE_ZLIB=OFF,zlib"

PACKAGES =+ "${PN}-completions"

FILES:${PN} += "${datadir}/licenses"

FILES:${PN}-completions = "\
    ${datadir}/bash-completion \
    ${datadir}/fish \
    ${datadir}/zsh \
"
RDEPENDS:${PN}-completions += "python3-json"
