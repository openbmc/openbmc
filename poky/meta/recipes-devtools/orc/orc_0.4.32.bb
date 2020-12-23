SUMMARY = "Optimised Inner Loop Runtime Compiler"
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/orc.html"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1400bd9d09e8af56b9ec982b3d85797e"

SRC_URI = "http://gstreamer.freedesktop.org/src/orc/orc-${PV}.tar.xz"
SRC_URI[sha256sum] = "a66e3d8f2b7e65178d786a01ef61f2a0a0b4d0b8370de7ce134ba73da4af18f0"

inherit meson pkgconfig gtk-doc

GTKDOC_MESON_OPTION = "gtk_doc"
GTKDOC_MESON_ENABLE_FLAG = "enabled"
GTKDOC_MESON_DISABLE_FLAG = "disabled"

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "orc-examples"
PACKAGES_DYNAMIC += "^liborc-.*"
FILES_orc-examples = "${libdir}/orc/*"
FILES_${PN} = "${bindir}/*"

python populate_packages_prepend () {
    libdir = d.expand('${libdir}')
    do_split_packages(d, libdir, r'^lib(.*)\.so\.*', 'lib%s', 'ORC %s library', extra_depends='', allow_links=True)
}

do_compile_prepend_class-native () {
    sed -i -e 's#/tmp#.#g' ${S}/orc/orccodemem.c
}
