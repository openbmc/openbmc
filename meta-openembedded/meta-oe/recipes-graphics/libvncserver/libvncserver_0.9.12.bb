DESCRIPTION = "library for easy implementation of a RDP/VNC server"
HOMEPAGE = "https://libvnc.github.io"
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"

PACKAGECONFIG ??= " \
    24bpp \
    filetransfer \
    ${@bb.utils.contains('LICENSE_FLAGS_WHITELIST','commercial','ffmpeg','',d)} \
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
FILES_libvncclient = "${libdir}/libvncclient.*"

inherit cmake

SRC_URI = "git://github.com/LibVNC/libvncserver;branch=master;protocol=https"
SRCREV = "1354f7f1bb6962dab209eddb9d6aac1f03408110"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DMAKE_INSTALL_LIBDIR=${libdir}"
