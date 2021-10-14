SUMMARY = "A library to help create and query binary XML blobs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = "\
    git://github.com/hughsie/libxmlb.git \
    file://run-ptest \
"
SRCREV = "994fd0ec3d28da82b5965949a9d925510f603562"
S = "${WORKDIR}/git"

inherit gobject-introspection gtk-doc meson ptest-gnome

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

GTKDOC_MESON_OPTION = "gtkdoc"
