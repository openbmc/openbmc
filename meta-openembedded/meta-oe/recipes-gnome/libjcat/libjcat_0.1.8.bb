SUMMARY = "Library for reading and writing Jcat files"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

DEPENDS = "\
    glib-2.0 \
    json-glib \
"

SRC_URI = "\
    git://github.com/hughsie/libjcat.git \
    file://run-ptest \
"
SRCREV = "356cd2faf2d2197156b0dae7984482cf781d64db"
S = "${WORKDIR}/git"

inherit gobject-introspection gtk-doc meson ptest-gnome vala

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
