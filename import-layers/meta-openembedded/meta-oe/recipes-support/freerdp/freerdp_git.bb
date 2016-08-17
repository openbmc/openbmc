# Copyright (C) 2010-2012 O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license

DESCRIPTION = "FreeRDP RDP client & server library"
HOMEPAGE = "http://www.freerdp.com"
DEPENDS = "openssl alsa-lib pcsc-lite"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pkgconfig cmake gitpkgv

PV = "1.2.5+gitr${SRCPV}"
PKGV = "${GITPKGVTAG}"

SRCREV = "62da9d28c674814c81c245c1c7882eb0da7be76b"
SRC_URI = "git://github.com/FreeRDP/FreeRDP.git \
    file://winpr-makecert-Build-with-install-RPATH.patch \
    file://0001-FindGStreamer_1_0-fix-build-failure-for-new-gstreame.patch \
"

S = "${WORKDIR}/git"

EXTRA_OECMAKE += " \
    -DWITH_ALSA=ON \
    -DWITH_PCSC=ON \
    -DWITH_FFMPEG=OFF \
    -DWITH_CUNIT=OFF \
    -DWITH_NEON=OFF \
    -DBUILD_STATIC_LIBS=OFF \
    -DCMAKE_POSITION_INDEPENDANT_CODE=ON \
    -DWITH_MANPAGES=OFF \
"

PACKAGECONFIG ??= "  \
                ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}\
                ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}\
                ${@bb.utils.contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)}\
                ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}\
                ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)}\
                gstreamer cups \
                "

X11_DEPS = "virtual/libx11 libxinerama libxext libxcursor libxv libxi libxrender libxfixes libxdamage libxrandr libxkbfile"
PACKAGECONFIG[x11] = "-DWITH_X11=ON -DWITH_XINERAMA=ON -DWITH_XEXT=ON -DWITH_XCURSOR=ON -DWITH_XV=ON -DWITH_XI=ON -DWITH_XRENDER=ON -DWITH_XFIXES=ON -DWITH_XDAMAGE=ON -DWITH_XRANDR=ON -DWITH_XKBFILE=ON,-DWITH_X11=OFF,${X11_DEPS}"
PACKAGECONFIG[wayland] = "-DWITH_WAYLAND=ON,-DWITH_WAYLAND=OFF,wayland"
PACKAGECONFIG[directfb] = "-DWITH_DIRECTFB=ON,-DWITH_DIRECTFB=OFF,directfb"
PACKAGECONFIG[pam] = "-DWITH_PAM=ON,-DWITH_PAM=OFF,libpam"
PACKAGECONFIG[pulseaudio] = "-DWITH_PULSEAUDIO=ON,-DWITH_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[gstreamer] = "-DWITH_GSTREAMER_1_0=ON,-DWITH_GSTREAMER_1_0=OFF,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[cups] = "-DWITH_CUPS=ON,-DWITH_CUPS=OFF,cups"

PACKAGES =+ "libfreerdp"

LEAD_SONAME = "libfreerdp.so"
FILES_libfreerdp = "${libdir}/lib*${SOLIBS}"

PACKAGES_DYNAMIC += "^libfreerdp-plugin-.*"

# we will need winpr-makecert to generate TLS certificates
do_install_append () {
    install -d ${D}${bindir}
    install -m755 winpr/tools/makecert/cli/winpr-makecert ${D}${bindir}
    rm -rf ${D}${libdir}/cmake
    rm -rf ${D}${libdir}/freerdp
}

python populate_packages_prepend () {
    freerdp_root = d.expand('${libdir}/freerdp')

    do_split_packages(d, freerdp_root, '^(audin_.*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='libfreerdp-plugin-audin')

    do_split_packages(d, freerdp_root, '^(rdpsnd_.*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='libfreerdp-plugin-rdpsnd')

    do_split_packages(d, freerdp_root, '^(tsmf_.*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='libfreerdp-plugin-tsmf')

    do_split_packages(d, freerdp_root, '^([^-]*)\.so$',
        output_pattern='libfreerdp-plugin-%s',
        description='FreeRDP plugin %s',
        prepend=True, extra_depends='')
}
