SUMMARY = "Exports your X session or FrameBuffer(fbdev) on-the-fly via VNC"
HOMEPAGE = "http://www.karlrunge.com/x11vnc/"

SECTION = "x11/utils"
AUTHOR = "Karl Runge"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/x11vnc.h;endline=31;md5=e871a2ad004776794b616822dcab6314"

SRCREV = "4ca006fed80410bd9b061a1519bd5d9366bb0bc8"
SRC_URI = "git://github.com/LibVNC/x11vnc;branch=master;protocol=https \
           file://starting-fix.patch \
           file://0001-misc-Makefile.am-don-t-install-Xdummy-when-configure.patch \
           file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch \
           file://src-cursor-fix-xfc-NULL-pointer-dereference.patch \
           "
S = "${WORKDIR}/git"

DEPENDS = "\
	jpeg \
	libtasn1 \
	libvncserver \
	openssl \
	p11-kit \
	zlib \
"

inherit pkgconfig autotools features_check

ANY_OF_DISTRO_FEATURES = "x11 fbdev"

PACKAGECONFIG ??= "\
	${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
	${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} \
"

PACKAGECONFIG[avahi] = "--with-avahi,--without-avahi,avahi"
PACKAGECONFIG[xinerama] = "--with-xinerama,--without-xinerama,libxinerama"
PACKAGECONFIG[x11] = "--with-x,--without-x, libxdamage libxext libxfixes libxrandr libxtst virtual/libx11"

RRECOMMENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'fbdev', 'kernel-module-uinput', '', d)}"
