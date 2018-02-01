SUMMARY = "7-zip is a commandline utility handling 7z archives."
HOMEPAGE = "http://www.7-zip.org/"
LICENSE = "LGPL-2.1+ & unRAR"
LIC_FILES_CHKSUM = "file://DOC/copying.txt;md5=4fbd65380cdd255951079008b364516c \
                    file://DOC/unRarLicense.txt;md5=9c87ddde469ef94aed153b0951d088de \
                    file://DOC/License.txt;md5=879598edf1f54dddb6930d7581357f8b"

SRC_URI = "http://downloads.sourceforge.net/p7zip/p7zip/${PV}/p7zip_${PV}_src_all.tar.bz2 \
          file://do_not_override_compiler_and_do_not_strip.patch"

SRC_URI[md5sum] = "a0128d661cfe7cc8c121e73519c54fbf"
SRC_URI[sha256sum] = "5eb20ac0e2944f6cb9c2d51dd6c4518941c185347d4089ea89087ffdd6e2341f"

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
    cat > ${D}${bindir}/7z << 'EOF'
#!/bin/sh
exec "$(dirname "$0")"/7z.bin "$@"
EOF
    chmod 0755 ${D}${bindir}/7z
}

BBCLASSEXTEND = "native"
