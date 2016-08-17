SUMMARY = "Lightweight media scanner"
DESCRIPTION = "Lightweight media scanner meant to be used in not-so-powerful devices, like embedded systems or old machines."
SECTION = "libs/multimedia"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://src/lib/lightmediascanner.c;endline=21;md5=6d8889bccb4c6c27e8b786342a3eb267"

DEPENDS = "file gawk glib-2.0 sqlite3"

PV = "0.5.1+git${SRCPV}"
SRCREV = "adfddb3486276a5ed2f5008c9e43a811e1271cc9"
SRC_URI = "git://github.com/profusion/lightmediascanner.git \
           file://id3-plugin-support-out-of-tree-build.patch \
          "

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-static --disable-mp4"

PACKAGECONFIG ??= "ogg flac wave id3 m3u pls asf rm jpeg png"
PACKAGECONFIG[generic] = "--enable-generic,--disable-generic,libav"
PACKAGECONFIG[ogg] = "--enable-ogg,--disable-ogg,libogg libvorbis libtheora"
PACKAGECONFIG[flac] = "--enable-flac,--disable-flac,flac"
PACKAGECONFIG[wave] = "--enable-wave,--disable-wave"
PACKAGECONFIG[id3] = "--enable-id3,--disable-id3"
PACKAGECONFIG[m3u] = "--enable-m3u,--disable-m3u"
PACKAGECONFIG[pls] = "--enable-pls,--disable-pls"
PACKAGECONFIG[asf] = "--enable-asf,--disable-asf"
PACKAGECONFIG[rm] = "--enable-rm,--disable-rm"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg"
PACKAGECONFIG[png] = "--enable-png,--disable-png"

do_install_append() {
       # Install "test" binary for corresponding package
       install -d ${D}/${bindir}
       install -m 755 ${B}/src/bin/.libs/test ${D}/${bindir}/test-lms
       # Remove .la files for loadable modules
       rm -f ${D}/${libdir}/${PN}/plugins/*.la
}

FILES_${PN} += "${datadir}/dbus-1"
FILES_${PN}-dbg += "${libdir}/${PN}/plugins/.debug"

PACKAGES_prepend = "${PN}-test "
FILES_${PN}-test_prepend = "${bindir}/test-lms "

PACKAGES += "${PN}-meta"
ALLOW_EMPTY_${PN}-meta = "1"

PACKAGES_DYNAMIC = "${PN}-plugin-*"

python populate_packages_prepend () {
    lms_libdir = d.expand('${libdir}/${PN}')
    pkgs = []

    pkgs += do_split_packages(d, oe.path.join(lms_libdir, "plugins"), '^(.*)\.so$', d.expand('${PN}-plugin-%s'), 'LightMediaScanner plugin for %s', prepend=True, extra_depends=d.expand('${PN}'))
    metapkg = d.getVar('PN', True) + '-meta'
    d.setVar('RDEPENDS_' + metapkg, ' '.join(pkgs))
}
