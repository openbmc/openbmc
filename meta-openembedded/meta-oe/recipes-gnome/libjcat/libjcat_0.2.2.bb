SUMMARY = "Library for reading and writing Jcat files"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

DEPENDS = "\
    glib-2.0 \
    json-glib \
"

SRC_URI = "\
    git://github.com/hughsie/libjcat.git;branch=main;protocol=https \
    file://run-ptest \
"
SRCREV = "fc04ad2d4e65e55024a4507e0aaa2659a0a588a3"
S = "${WORKDIR}/git"

inherit gobject-introspection gtk-doc meson ptest-gnome vala lib_package

PACKAGECONFIG ??= "\
    gpg \
    pkcs7 \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"
PACKAGECONFIG[gpg] = "-Dgpg=true,-Dgpg=false,gpgme"
PACKAGECONFIG[pkcs7] = "-Dpkcs7=true,-Dpkcs7=false,gnutls gnutls-native"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

# manpage generation is broken because help2man needs to run the target binary on the host...
EXTRA_OEMESON = "-Dman=false"
GTKDOC_MESON_OPTION = "gtkdoc"

RDEPENDS:${PN}:class-target = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'gpg', 'gnupg', '', d)} \
"

INSANE_SKIP:${PN}-ptest += "buildpaths"
