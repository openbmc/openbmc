SUMMARY = "X11 Mouse Theme"
HOMEPAGE = "http://xfce-look.org/content/show.php/OpenZone?content=111343"
SECTION = "x11/wm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a2f562fb8fb1e138b810d69521c4bcd7"

inherit allarch gtk-icon-cache

SRC_URI = "http://xfce-look.org/CONTENT/content-files/111343-OpenZone-${PV}.tar.xz"
SRC_URI[md5sum] = "4dae968cbd525072664ef7a4fc7c4154"
SRC_URI[sha256sum] = "dc20f97a49e1ff1becf7853ef5f137ed30a4c27490540e755021d78d339efd92"

S = "${WORKDIR}/OpenZone"

do_compile() {
}

do_install() {
    install -d ${D}${datadir}/icons
    for theme in `find -name '*.tar.xz'`; do
        tar -Jxf ${theme} -C ${D}${datadir}/icons
    done
}

python populate_packages:prepend () {
    icondir = bb.data.expand('${datadir}/icons', d)
    do_split_packages(d, icondir, '^(.*)', '%s', 'Open Zone cursors %s', allow_dirs=True)
}

FILES:${PN} += "${datadir}/icons"

PACKAGES_DYNAMIC += "^openzone-.*"
ALLOW_EMPTY:${PN} = "1"
