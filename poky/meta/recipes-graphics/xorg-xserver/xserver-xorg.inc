SUMMARY = "The X.Org X server"
HOMEPAGE = "http://www.x.org"
SECTION = "x11/base"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=5df87950af51ac2c5822094553ea1880"

# xf86-*-* packages depend on an X server built with the xfree86 DDX
# so we have a virtual to represent that:
# deprecated, we should use virtual/xserver instead
PROVIDES = "virtual/xserver-xf86"

# Other packages tend to just care that there is *an* X server:
PROVIDES += "virtual/xserver"

PE = "2"

XORG_PN = "xorg-server"
SRC_URI = "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${PV}.tar.xz"

UPSTREAM_CHECK_REGEX = "xorg-server-(?P<pver>\d+(\.(?!99)\d+)+)\.tar"

CVE_PRODUCT = "xorg-server"

S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit autotools pkgconfig

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

LIB_DEPS = "pixman libxfont2 xtrans libxau libxext libxdmcp libdrm libxkbfile libpciaccess"
DEPENDS = "xorgproto ${LIB_DEPS} font-util"

# Split out some modules and extensions from the main package
# These aren't needed for basic operations and only take up space:
#  32.0k   libdri.so
#  91.0k   libexa.so
#  336.0k  libglx.so
#  1360k   libint10.so
#  180.0k  libwfb.so
#  320.0k  libxaa.so
#  124.0k  libxf1bpp.so
#  84.0k   libxf4bpp.so
#          librecord.so
#          libextmod.so
#          libdbe.so

PACKAGES =+ "${PN}-sdl \
             ${PN}-fbdev \
             ${PN}-xvfb \
             ${PN}-utils \
             ${PN}-xephyr \
             ${PN}-xwayland \
             ${PN}-multimedia-modules \
             ${PN}-extension-dri \
             ${PN}-extension-dri2 \
             ${PN}-extension-glx \
             ${PN}-extension-record \
             ${PN}-extension-extmod \
             ${PN}-extension-dbe \
             ${PN}-module-libint10 \
             ${PN}-module-libafb \
             ${PN}-module-libwfb  \
             ${PN}-module-libmfb \
             ${PN}-module-libcfb \
             ${PN}-module-exa \
             ${PN}-module-xaa \
             ${PN}-module-libxf1bpp \
             ${PN}-module-libxf4bpp \
             xf86-video-modesetting"

SUMMARY:xf86-video-modesetting = "X.Org X server -- modesetting display driver"
INSANE_SKIP:${MLPREFIX}xf86-video-modesetting = "xorg-driver-abi"

XSERVER_RRECOMMENDS = "xkeyboard-config rgb xserver-xf86-config xkbcomp xf86-input-libinput"
RRECOMMENDS:${PN} += "${XSERVER_RRECOMMENDS}"
RRECOMMENDS:${PN}-xwayland += "${XSERVER_RRECOMMENDS}"
RDEPENDS:${PN}-xvfb += "xkeyboard-config"
RDEPENDS:${PN}-module-exa = "${PN} (= ${EXTENDPKGV})"

FILES:${PN} = "${bindir} ${libdir}/X11/Options ${libdir}/X11/Cards ${libdir}/X11/getconfig ${libdir}/X11/etc ${libdir}/modules/*.so ${libdir}/xorg/modules/*.so /etc/X11 ${libdir}/xorg/protocol.txt ${datadir}/X11/xorg.conf.d"
FILES:${PN}-dev += "${libdir}/xorg/modules/*.la ${libdir}/xorg/modules/*/*.la"
FILES:${PN}-doc += "${libdir}/X11/doc ${datadir}/X11/xkb/compiled/README.compiled ${localstatedir}/lib/xkb/README.compiled"
FILES:${PN}-sdl = "${bindir}/Xsdl"
FILES:${PN}-fbdev = "${bindir}/Xfbdev"
FILES:${PN}-xvfb = "${bindir}/Xvfb"
FILES:${PN}-utils = "${bindir}/scanpci ${bindir}/pcitweak ${bindir}/ioport ${bindir}/in[bwl] ${bindir}/out[bwl] ${bindir}/mmap[rw] ${bindir}/gtf ${bindir}/getconfig ${bindir}/getconfig.pl"
FILES:${PN}-xephyr = "${bindir}/Xephyr"
FILES:${PN}-xwayland = "${bindir}/Xwayland"
FILES:${PN}-multimedia-modules = "${libdir}/xorg/modules/multimedia/*drv*"
FILES:${PN}-extension-dri = "${libdir}/xorg/modules/extensions/libdri.so"
FILES:${PN}-extension-dri2 = "${libdir}/xorg/modules/extensions/libdri2.so"
FILES:${PN}-extension-glx = "${libdir}/xorg/modules/extensions/libglx.so"
FILES:${PN}-extension-record = "${libdir}/xorg/modules/extensions/librecord.so"
FILES:${PN}-extension-extmod = "${libdir}/xorg/modules/extensions/libextmod.so"
FILES:${PN}-extension-dbe = "${libdir}/xorg/modules/extensions/libdbe.so"
FILES:${PN}-module-libint10 = "${libdir}/xorg/modules/libint10.so"
FILES:${PN}-module-libafb = "${libdir}/xorg/modules/libafb.so"
FILES:${PN}-module-libwfb = "${libdir}/xorg/modules/libwfb.so"
FILES:${PN}-module-libmfb = "${libdir}/xorg/modules/libmfb.so"
FILES:${PN}-module-libcfb = "${libdir}/xorg/modules/libcfb.so"
FILES:${PN}-module-exa = "${libdir}/xorg/modules/libexa.so"
FILES:${PN}-module-xaa = "${libdir}/xorg/modules/libxaa.so"
FILES:${PN}-module-libxf1bpp = "${libdir}/xorg/modules/libxf1bpp.so"
FILES:${PN}-module-libxf4bpp = "${libdir}/xorg/modules/libxf4bpp.so"
FILES:xf86-video-modesetting = "${libdir}/xorg/modules/drivers/modesetting_drv.so"

EXTRA_OECONF += "--with-fop=no \
                 --with-pic \
                 --disable-static \
                 --disable-record \
                 --disable-dmx \
                 --disable-xnest \
                 --enable-xvfb \
                 --enable-composite \
                 --without-dtrace \
                 --with-int10=x86emu \
                 --sysconfdir=/etc/X11 \
                 --localstatedir=/var \
                 --with-xkb-output=/var/lib/xkb \
                 --with-os-name=Linux \
"

OPENGL_PKGCONFIGS = "dri glx glamor dri3 xshmfence"
PACKAGECONFIG ??= "dga dri2 udev ${XORG_CRYPTO} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', '${OPENGL_PKGCONFIGS}', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd systemd-logind', '', d)} \
"

PACKAGECONFIG[udev] = "--enable-config-udev,--disable-config-udev,udev"
PACKAGECONFIG[dga] = "--enable-dga,--disable-dga"
PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,virtual/mesa"
PACKAGECONFIG[dri2] = "--enable-dri2,--disable-dri2"
# DRI3 requires xshmfence to also be enabled
PACKAGECONFIG[dri3] = "--enable-dri3,--disable-dri3"
PACKAGECONFIG[glx] = "--enable-glx,--disable-glx,virtual/libgl virtual/libx11"
PACKAGECONFIG[glamor] = "--enable-glamor,--disable-glamor,libepoxy virtual/libgbm,libegl"
PACKAGECONFIG[unwind] = "--enable-libunwind,--disable-libunwind,libunwind"
PACKAGECONFIG[xshmfence] = "--enable-xshmfence,--disable-xshmfence,libxshmfence"
PACKAGECONFIG[xmlto] = "--with-xmlto, --without-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[systemd-logind] = "--enable-systemd-logind=yes,--enable-systemd-logind=no,dbus,"
PACKAGECONFIG[systemd] = "--with-systemd-daemon,--without-systemd-daemon,systemd"
PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama"
PACKAGECONFIG[xwayland] = "--enable-xwayland,--disable-xwayland,wayland wayland-native wayland-protocols libepoxy"

# Xorg requires a SHA1 implementation, pick one
XORG_CRYPTO ??= "openssl"
PACKAGECONFIG[openssl] = "--with-sha1=libcrypto,,openssl"
PACKAGECONFIG[nettle] = "--with-sha1=libnettle,,nettle"
PACKAGECONFIG[gcrypt] = "--with-sha1=libgcrypt,,libgcrypt"

do_install:append () {
	# Its assumed base-files creates this for us
	rmdir ${D}${localstatedir}/log/
        sed -i -e 's,${libdir}/xorg/modules,${prefix}/lib*/xorg/modules,' ${D}${mandir}/man5/xorg.conf.5
}

# Add runtime provides for the ABI versions of the video and input subsystems,
# so that drivers can depend on the relevant version.
python populate_packages:prepend() {
    import subprocess

    # Set PKG_CONFIG_PATH so pkg-config looks at the .pc files that are going
    # into the new package, not the staged ones.
    newenv = dict(os.environ)
    newenv["PKG_CONFIG_PATH"] = d.expand("${PKGD}${libdir}/pkgconfig/")

    def get_abi(name):
        abis = {
          "video": "abi_videodrv",
          "input": "abi_xinput"
        }
        p = subprocess.Popen(args="pkg-config --variable=%s xorg-server" % abis[name],
                             shell=True, env=newenv, stdout=subprocess.PIPE)
        stdout, stderr = p.communicate()
        output = stdout.decode("utf-8").split(".")[0]
        mlprefix = d.getVar('MLPREFIX') or ''
        return "%sxorg-abi-%s-%s" % (mlprefix, name, output)

    pn = d.getVar("PN")
    d.appendVar("RPROVIDES:" + pn, " " + get_abi("input"))
    d.appendVar("RPROVIDES:" + pn, " " + get_abi("video"))
}
