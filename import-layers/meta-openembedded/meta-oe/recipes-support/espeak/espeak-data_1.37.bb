require espeak.inc

inherit native

PACKAGES = "${PN}"
FILES_${PN} = "${layout_datadir}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
TARGET_ARCH = "${MACHINE_ARCH}"

do_compile() {
    # Fixing byte order of phoneme data files
    cd "${S}/platforms/big_endian"
    sed -i '/^ *CC *=/d' Makefile
    sed -i 's/\(.*BYTE_ORDER\)/#undef BYTE_ORDER\n#define BYTE_ORDER BIG_ENDIAN\n\1/' espeak-phoneme-data.c
    oe_runmake
    ./espeak-phoneme-data "${S}/espeak-data"
    cp -f phondata phonindex phontab "${S}/espeak-data"
}

do_install() {
    install -d ${D}${layout_datadir}/espeak-data
    install -m 0644 ${S}/espeak-data/phon* ${D}${layout_datadir}/espeak-data
}

SRC_URI[md5sum] = "5c7b292d0537232868b4ac1317d76c02"
SRC_URI[sha256sum] = "77f406c345362185ec4f362bf49f61d202e01ac9b9cd5c14f803195a544ce6d3"
