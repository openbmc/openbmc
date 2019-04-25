DESCRIPTION = "eSpeak is a compact open source software speech synthesizer"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://License.txt;md5=cb7a20edb4c9f5f478de6523dcd7362c"

SRC_URI = "http://downloads.sourceforge.net/espeak/espeak-1.48.04-source.zip \
           file://0001-Fix-build-of-shared-library-on-architectures-needing.patch \
           file://0002-tr_languages-cast-string_ordinal-init-values.patch \
"
SRC_URI[md5sum] = "cadd7482eaafe9239546bdc09fa244c3"
SRC_URI[sha256sum] = "bf9a17673adffcc28ff7ea18764f06136547e97bbd9edf2ec612f09b207f0659"

S = "${WORKDIR}/espeak-${PV}-source"

DEPENDS = "portaudio-v19 qemu-helper-native"
inherit siteinfo qemu


CXXFLAGS += "-DUSE_PORTAUDIO"
TARGET_CC_ARCH += "${LDFLAGS}"

FILES_${PN} += "${datadir}/espeak-data"

do_configure() {
    #  "speak" binary, a TTS engine, uses portaudio in either APIs V18 or V19, use V19
    install -m 0644 "${S}/src/portaudio19.h" "${S}/src/portaudio.h"
}

do_compile() {
    cd src
    oe_runmake

    cd "${S}/platforms/big_endian"
    qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_TARGET}', ['${S}/platforms/big_endian', '${STAGING_DIR_TARGET}${base_libdir}'])}"
    cat >qemuwrapper <<EOF
#!/bin/sh
$qemu_binary "\$@"
EOF
    chmod +x qemuwrapper
    sed -i '/^ *CC *=/d' Makefile
    # Fixing byte order of phoneme data files
    if [ "${SITEINFO_ENDIANNESS}" = "be" ]; then
        sed -i 's/\(.*BYTE_ORDER\)/#undef BYTE_ORDER\n#define BYTE_ORDER BIG_ENDIAN\n\1/' espeak-phoneme-data.c
    else
        sed -i 's/\(.*BYTE_ORDER\)/#undef BYTE_ORDER\n#define BYTE_ORDER LITTLE_ENDIAN\n\1/' espeak-phoneme-data.c
    fi
    oe_runmake
    ./qemuwrapper ./espeak-phoneme-data "${S}/espeak-data" "." "${S}/espeak-data/phondata-manifest"
    cp -f phondata phonindex phontab "${S}/espeak-data"
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}/espeak
    install -d ${D}${datadir}/espeak-data

    # we do not ship "speak" binary though.
    install -m 0755 ${S}/src/espeak ${D}${bindir}
    install -m 0644 ${S}/src/speak_lib.h ${D}${includedir}/espeak/
    ln -sf espeak/espeak.h ${D}${includedir}/
    oe_libinstall -so -C src libespeak ${D}${libdir}

    cp -R --no-dereference --preserve=mode,links ${S}/espeak-data/* ${D}${datadir}/espeak-data
}

RDEPENDS_${PN} = "portaudio-v19"
