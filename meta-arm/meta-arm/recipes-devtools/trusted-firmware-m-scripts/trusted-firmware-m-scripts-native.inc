SUMMARY = "Trusted Firmware image signing scripts"
DESCRIPTION = "Trusted Firmware-M image signing scripts"
HOMEPAGE = "https://git.trustedfirmware.org/trusted-firmware-m.git"

inherit native

# See bl2/ext/mcuboot/scripts/requirements.txt
RDEPENDS:${PN} = "\
    python3-cryptography-native \
    python3-pyasn1-native \
    python3-pyyaml-native \
    python3-cbor2-native \
    python3-imgtool-native \
    python3-click-native \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}/${libdir}
    cp -rf ${S}/bl2/ext/mcuboot/scripts/ ${D}/${libdir}/tfm-scripts
    cp -rf ${S}/bl2/ext/mcuboot/*.pem ${D}/${libdir}/tfm-scripts
}
FILES:${PN} = "${libdir}/tfm-scripts"
