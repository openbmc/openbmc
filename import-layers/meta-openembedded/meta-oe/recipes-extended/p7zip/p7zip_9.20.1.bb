SUMMARY = "7-zip is a commandline utility handling 7z archives."
HOMEPAGE = "http://www.7-zip.org/"
LICENSE = "LGPL-2.1+ & unRAR"
LIC_FILES_CHKSUM = "file://DOCS/copying.txt;md5=ecfc54c9e37b63ac58900061ce2eab5a \
                    file://DOCS/unRarLicense.txt;md5=9c87ddde469ef94aed153b0951d088de"

SRC_URI = "http://downloads.sourceforge.net/p7zip/p7zip/${PV}/p7zip_${PV}_src_all.tar.bz2 \
          file://do_not_override_compiler_and_do_not_strip.patch"
SRC_URI[md5sum] = "bd6caaea567dc0d995c990c5cc883c89"
SRC_URI[sha256sum] = "49557e7ffca08100f9fc687f4dfc5aea703ca207640c76d9dee7b66f03cb4782"

S = "${WORKDIR}/${BPN}_${PV}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/bin/* ${D}${bindir}
}

# all3: to build bin/7za, bin/7z (with its plugins), bin/7zr and bin/7zCon.sfx
EXTRA_OEMAKE_class-native = "all3"

do_install_class-native() {
    install -d ${D}${bindir}
    install -d ${D}${bindir}/Codecs
    install -m 0755 ${S}/bin/7* ${D}${bindir}
    install -m 0755 ${S}/bin/Codecs/* ${D}${bindir}/Codecs

    # Create a shell script wrapper to execute next to 7z.so
    mv ${D}${bindir}/7z ${D}${bindir}/7z.bin
    echo "#! /bin/sh" > ${D}${bindir}/7z
    echo "exec ${D}${bindir}/7z.bin \"\$@\"" >> ${D}${bindir}/7z
    chmod 0755 ${D}${bindir}/7z
}

BBCLASSEXTEND += "native"
