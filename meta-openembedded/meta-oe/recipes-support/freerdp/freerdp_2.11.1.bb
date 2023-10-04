# Copyright (C) 2010-2012 O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license

DESCRIPTION = "FreeRDP RDP client & server library"
HOMEPAGE = "http://www.freerdp.com"
DEPENDS = "openssl alsa-lib libusb1"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pkgconfig cmake gitpkgv

PE = "1"
PKGV = "${GITPKGVTAG}"

SRCREV = "9d3d40bf3e429c0cd6609f686c0e73ed0eff0a07"
SRC_URI = "git://github.com/FreeRDP/FreeRDP.git;branch=stable-2.0;protocol=https \
           file://winpr-makecert-Build-with-install-RPATH.patch \
           file://0001-Fix-const-qualifier-error.patch \
           "

S = "${WORKDIR}/git"

EXTRA_OECMAKE += " \
    -DWITH_ALSA=ON \
    -DWITH_FFMPEG=OFF \
    -DWITH_CUNIT=OFF \
    -DWITH_NEON=OFF \
    -DBUILD_STATIC_LIBS=OFF \
    -DCMAKE_POSITION_INDEPENDANT_CODE=ON \
    -DWITH_MANPAGES=OFF \
    -DPROXY_PLUGINDIR=${libdir}/freerdp/proxy/plugins \
"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'directfb pam pulseaudio wayland x11', d)}\
    gstreamer cups pcsc server \
"

X11_DEPS = "virtual/libx11 libxinerama libxext libxcursor libxv libxi libxrender libxfixes libxdamage libxrandr libxkbfile"
PACKAGECONFIG[x11] = "-DWITH_X11=ON -DWITH_XINERAMA=ON -DWITH_XEXT=ON -DWITH_XCURSOR=ON -DWITH_XV=ON -DWITH_XI=ON -DWITH_XRENDER=ON -DWITH_XFIXES=ON -DWITH_XDAMAGE=ON -DWITH_XRANDR=ON -DWITH_XKBFILE=ON,-DWITH_X11=OFF,${X11_DEPS}"
PACKAGECONFIG[wayland] = "-DWITH_WAYLAND=ON,-DWITH_WAYLAND=OFF,wayland wayland-native libxkbcommon"
PACKAGECONFIG[directfb] = "-DWITH_DIRECTFB=ON,-DWITH_DIRECTFB=OFF,directfb"
PACKAGECONFIG[pam] = "-DWITH_PAM=ON,-DWITH_PAM=OFF,libpam"
PACKAGECONFIG[pcsc] = "-DWITH_PCSC=ON,-DWITH_PCSC=OFF,pcsc-lite"
PACKAGECONFIG[pulseaudio] = "-DWITH_PULSEAUDIO=ON,-DWITH_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[gstreamer] = "-DWITH_GSTREAMER_1_0=ON,-DWITH_GSTREAMER_1_0=OFF,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[cups] = "-DWITH_CUPS=ON,-DWITH_CUPS=OFF,cups"
PACKAGECONFIG[server] = "-DWITH_SERVER=ON,-DWITH_SERVER=OFF"

PACKAGES =+ "libfreerdp"

LEAD_SONAME = "libfreerdp.so"
FILES:libfreerdp = "${libdir}/lib*${SOLIBS}"

PACKAGES_DYNAMIC += "^libfreerdp-plugin-.*"

do_configure:append() {
    sed -i -e 's|${WORKDIR}||g' ${B}/buildflags.h
}

# we will need winpr-makecert to generate TLS certificates
do_install:append () {
    install -d ${D}${bindir}
    install -m755 winpr/tools/makecert-cli/winpr-makecert ${D}${bindir}
    rm -rf ${D}${libdir}/cmake
    rm -rf ${D}${libdir}/freerdp
}

python populate_packages:prepend () {
    freerdp_root = d.expand('${libdir}/freerdp')

    do_split_packages(d, freerdp_root, r'^(audin_.*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='libfreerdp-plugin-audin')

    do_split_packages(d, freerdp_root, r'^(rdpsnd_.*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='libfreerdp-plugin-rdpsnd')

    do_split_packages(d, freerdp_root, r'^(tsmf_.*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='libfreerdp-plugin-tsmf')

    do_split_packages(d, freerdp_root, r'^([^-]*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='')
}
