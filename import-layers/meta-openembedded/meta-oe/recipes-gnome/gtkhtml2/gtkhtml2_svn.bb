SECTION = "libs"
DEPENDS = "gtk+ glib-2.0 libxml2"
SUMMARY = "A GTK+ HTML rendering library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"

SRCREV = "1161"
PV = "2.11.0+svnr${SRCPV}"
PR = "r5"

SRC_URI = "svn://svn.gnome.org/svn/gtkhtml2/;module=trunk;protocol=http \
           http://git.yoctoproject.org/cgit/cgit.cgi/web-patches/plain/css-stylesheet-user.patch;striplevel=0;name=patch2 \
           http://git.yoctoproject.org/cgit/cgit.cgi/web-patches/plain/css-media.patch;striplevel=0;name=patch3 \
           http://git.yoctoproject.org/cgit/cgit.cgi/web-patches/plain/add-end-element-signal.patch;striplevel=0;name=patch4 \
           http://git.yoctoproject.org/cgit/cgit.cgi/web-patches/plain/add-dom-functions.patch;striplevel=0;name=patch5 \
           http://git.yoctoproject.org/cgit/cgit.cgi/web-patches/plain/iain-mem-leak.patch;striplevel=0;name=patch6 \
           file://0001-tests-main.c-fix-build-with-glib-2.36.patch \
"

SRC_URI[patch2.md5sum] = "05fc3627ca364095702dc804f41c8391"
SRC_URI[patch2.sha256sum] = "df5cca50a8f95333505d7920929fea251daea3be25be6834a1c50a742d9eb674"

SRC_URI[patch3.md5sum] = "d3fe4cda3545f3e4718f1acc186608ab"
SRC_URI[patch3.sha256sum] = "3aefaa17ffa38143bf5df1161c51ab402d35bfbee41ab4643c313edf569165d5"

SRC_URI[patch4.md5sum] = "651b1601d8a1b21c8a3040fadb729043"
SRC_URI[patch4.sha256sum] = "d067e8331bf9c6851f1c6067d991a7f54327f532900b405ebdf8e149c071f381"

SRC_URI[patch5.md5sum] = "041be9711a16e629d01487664ba97152"
SRC_URI[patch5.sha256sum] = "42956fb41341cf82ae8bce18b4cf96a7e2aa631b1b60657afb6d7e9be7cd138c"

SRC_URI[patch6.md5sum] = "4e11dc7899d68f2be2e06ccee01d296d"
SRC_URI[patch6.sha256sum] = "1e2cc080e654c1839c5cb4b4adf4c62a23e7da208427f3ba0b16cfed9e5cfa98"

S = "${WORKDIR}/trunk"

inherit pkgconfig autotools

EXTRA_OECONF = " --disable-accessibility"
