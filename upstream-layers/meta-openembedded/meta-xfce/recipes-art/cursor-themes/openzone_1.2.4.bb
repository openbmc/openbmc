SUMMARY = "X11 Mouse Theme"
HOMEPAGE = "http://xfce-look.org/content/show.php/OpenZone?content=111343"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a4dd0d1066e1f1224b3bc4bbb206b754"

inherit allarch gtk-icon-cache

SRC_URI = "https://mirrors.dotsrc.org/mirrors/exherbo/111343-OpenZone-${PV}.tar.xz"
SRC_URI[sha256sum] = "db6fa6d0798aceb56998ce81382fa2246c77911d56b3b06e6aadee4cf5b4816e"

S = "${UNPACKDIR}/OpenZone"

do_compile() {
}

do_install() {
    install -d ${D}${datadir}/icons
    for theme in `find -name '*.tar.xz'`; do
        tar -Jxf ${theme} -C ${D}${datadir}/icons
    done
}

python populate_packages:prepend () {
    icondir = d.expand('${datadir}/icons/')
    iconname = d.expand('${PN}-%s')
    do_split_packages(d, icondir, '^(.*)',
                      iconname,
                      'Open Zone cursors %s',
                      extra_depends='',
                      prepend=True,
                      aux_files_pattern=['${datadir}/icons/%s/*'])
}

FILES:${PN} += "${datadir}/icons"

PACKAGES_DYNAMIC += "^${PN}-.*"
ALLOW_EMPTY:${PN} = "1"
