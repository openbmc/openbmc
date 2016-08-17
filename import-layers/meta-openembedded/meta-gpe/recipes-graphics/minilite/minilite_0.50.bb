SUMMARY = "GPE Screen light control dockapp"
SECTION = "gpe"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://minilite.c;beginline=8;endline=11;md5=fe9332aaecbce9f74ce2bfeb91680ed1"

DEPENDS = "libgpewidget"

inherit gpe

SRC_URI = "${GPE_MIRROR}/${BP}.tar.gz \
           file://makefile-fix.patch"
SRC_URI[md5sum] = "d81cecf269ad7bab0da960e6e7228332"
SRC_URI[sha256sum] = "2b299425203246090a4949e034f1d0efb3ff99cd1591d0e16c57370a530b361e"

export CVSBUILD="no"
