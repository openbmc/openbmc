SUMMARY = "The X.Org X server"
HOMEPAGE = "http://www.x.org"
SECTION = "x11/base"
LICENSE = "MIT"
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

CVE_PRODUCT = "xorg-server x_server"

CVE_STATUS[CVE-2011-4613] = "not-applicable-platform: This is specific to Debian's xserver-wrapper.c"
CVE_STATUS[CVE-2020-25697] = "upstream-wontfix: \
As per upstream, exploiting this flaw is non-trivial and it requires exact \
timing on the behalf of the attacker. Many graphical applications exit if their \
connection to the X server is lost, so a typical desktop session is either \
impossible or difficult to exploit. There is currently no upstream patch \
available for this flaw."
CVE_STATUS[CVE-2022-3553] = "cpe-incorrect: This is specific to XQuartz, which is the macOS X server port"

S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit meson pkgconfig

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

LIB_DEPS = "pixman libxfont2 xtrans libxau libxext libxdmcp libdrm libxkbfile libpciaccess libxcvt libtirpc"
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

XSERVER_RDEPENDS = "xkeyboard-config rgb xserver-xf86-config xkbcomp xf86-input-libinput"
RDEPENDS:${PN} += "${XSERVER_RDEPENDS}"
RDEPENDS:${PN}-xwayland += "${XSERVER_RDEPENDS}"
RDEPENDS:${PN}-xvfb += "xkeyboard-config"
RDEPENDS:${PN}-module-exa = "${PN} (= ${EXTENDPKGV})"

FILES:${PN} = "${bindir} ${libdir}/X11/Options ${libdir}/X11/Cards ${libdir}/X11/getconfig ${libdir}/X11/etc ${libdir}/modules/*.so ${libdir}/xorg/modules/input/*.so ${libdir}/xorg/modules/*.so /etc/X11 ${libdir}/xorg/protocol.txt ${datadir}/X11/xorg.conf.d"
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

EXTRA_OEMESON += " \
                 -Dxnest=false \
                 -Ddtrace=false \
                 -Dint10=x86emu \
                 -Dxkb_output_dir=/var/lib/xkb \
"

OPENGL_PKGCONFIGS = "dri glx glamor dri3"
PACKAGECONFIG ??= "dga dri2 udev ${XORG_CRYPTO} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', '${OPENGL_PKGCONFIGS}', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-logind', '', d)} \
"

PACKAGECONFIG[udev] = "-Dudev=true,-Dudev=false,udev"
PACKAGECONFIG[dga] = "-Ddga=true,-Ddga=false"
PACKAGECONFIG[dri] = "-Ddri1=true,-Ddri1=false,virtual/mesa"
PACKAGECONFIG[dri2] = "-Ddri2=true,-Ddri2=false"
PACKAGECONFIG[dri3] = "-Ddri3=true,-Ddri3=false"
PACKAGECONFIG[glx] = "-Dglx=true,-Dglx=false,virtual/libgl virtual/libx11"
PACKAGECONFIG[glamor] = "-Dglamor=true,-Dglamor=false,libepoxy virtual/libgbm,libegl"
PACKAGECONFIG[unwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"
PACKAGECONFIG[systemd-logind] = "-Dsystemd_logind=true,-Dsystemd_logind=false,dbus,"
PACKAGECONFIG[xinerama] = "-Dxinerama=true,-Dxinerama=false"
PACKAGECONFIG[xvfb] = "-Dxvfb=true,-Dxvfb=false"

# Xorg requires a SHA1 implementation, pick one
XORG_CRYPTO ??= "openssl"
PACKAGECONFIG[openssl] = "-Dsha1=libcrypto,,openssl"
PACKAGECONFIG[nettle] = "-Dsha1=libnettle,,nettle"
PACKAGECONFIG[gcrypt] = "-Dsha1=libgcrypt,,libgcrypt"

do_install:append () {
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

CVE_STATUS[CVE-2023-5574] = "${@bb.utils.contains('PACKAGECONFIG', 'xvfb', 'unpatched', 'not-applicable-config: specific to Xvfb', d)}"
