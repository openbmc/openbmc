LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=87212b5f1ae096371049a12f80034f32"

SRC_URI = "https://github.com/rrthomas/psutils/releases/download/v${PV}/psutils-${PV}.tar.gz"
SRC_URI[sha256sum] = "6f8339fd5322df5c782bfb355d9f89e513353220fca0700a5a28775404d7e98b"

inherit perlnative autotools

export PERL="/usr/bin/env perl"

DEPENDS += "libpaper-native"

do_install:append() {
    sed -i -e 's|${STAGING_BINDIR_NATIVE}/perl-native/|/usr/bin/env |g' ${D}${bindir}/pstops
    for f in psbook psresize psnup psselect; do
        grep -v '${B}' ${D}${bindir}/$f > ${D}${bindir}/$f.temp
        install -m 0755 ${D}${bindir}/$f.temp ${D}${bindir}/$f
        rm -f ${D}${bindir}/$f.temp
    done
}

BBCLASSEXTEND += "native"
# /usr/bin/pstops contained in package psutils requires perl
RDEPENDS:${PN} += "perl"
