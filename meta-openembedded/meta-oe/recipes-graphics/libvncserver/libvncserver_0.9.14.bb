DESCRIPTION = "library for easy implementation of a RDP/VNC server"
HOMEPAGE = "https://libvnc.github.io"
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"

# ffmpeg support is not currently compatible with ffmpeg 5.0
PACKAGECONFIG ??= " \
    24bpp \
    filetransfer \
    gcrypt \
    gnutls \
    jpeg \
    lzo \
    png \
    pthread \
    ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '',d)} \
    sdl \
    websockets \
    zlib \
"

PACKAGECONFIG[24bpp] = "-DWITH_24BPP=ON,-DWITH_24BPP=OFF,"
PACKAGECONFIG[filetransfer] = "-DWITH_TIGHTVNC_FILETRANSFER=ON,-DWITH_TIGHTVNC_FILETRANSFER=OFF,"
PACKAGECONFIG[ffmpeg] = "-DWITH_FFMPEG=ON,-DWITH_FFMPEG=OFF,ffmpeg,ffmpeg"
PACKAGECONFIG[gcrypt] = "-DWITH_GCRYPT=ON,-DWITH_GCRYPT=OFF,libgcrypt,libgcrypt"
PACKAGECONFIG[gnutls] = "-DWITH_GNUTLS=ON,-DWITH_GNUTLS=OFF,gnutls"
PACKAGECONFIG[jpeg] = "-DWITH_JPEG=ON,-DWITH_JPEG=OFF,jpeg"
PACKAGECONFIG[ipv6] = "-DWITH_IPv6=ON,-DWITH_IPv6=OFF,"
PACKAGECONFIG[lzo] = "-DWITH_LZO=ON,-DWITH_LZO=OFF,lzo"
PACKAGECONFIG[openssl] = "-DWITH_OPENSSL=ON,-DWITH_OPENSSL=OFF,openssl"
PACKAGECONFIG[png] = "-DWITH_PNG=ON,-DWITH_PNG=OFF,libpng,libpng"
PACKAGECONFIG[pthread] = "-DWITH_THREADS=ON,-DWITH_THREADS=OFF,"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF,systemd"
PACKAGECONFIG[sasl] = "-DWITH_SASL=ON,-DWITH_SASL=OFF,cyrus-sasl"
PACKAGECONFIG[sdl] = "-DWITH_SDL=ON,-DWITH_SDL=OFF,libsdl2"
PACKAGECONFIG[websockets] = "-DWITH_WEBSOCKETS=ON,-DWITH_WEBSOCKETS=OFF,"
PACKAGECONFIG[zlib] = "-DWITH_ZLIB=ON,-DWITH_ZLIB=OFF,zlib"

PACKAGE_BEFORE_PN = "libvncclient"
FILES:libvncclient = "${libdir}/libvncclient.*"

inherit cmake

SRC_URI = "git://github.com/LibVNC/libvncserver;branch=master;protocol=https"
SRCREV = "10e9eb75f73e973725dc75c373de5d89807af028"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DMAKE_INSTALL_LIBDIR=${libdir}"

do_install:append() {
    sed -i -e 's|${STAGING_DIR_HOST}||g' ${D}${libdir}/cmake/LibVNCServer/LibVNCServerTargets.cmake
}
