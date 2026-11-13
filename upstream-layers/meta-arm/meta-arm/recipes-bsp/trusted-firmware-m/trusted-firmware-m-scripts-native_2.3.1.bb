require recipes-bsp/trusted-firmware-m/trusted-firmware-m-${PV}-src.inc

inherit native python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-cryptography-native \
    python3-pyasn1-native \
    python3-pyyaml-native \
    python3-cbor2-native \
    python3-imgtool-native \
    python3-click-native \
    python3-pyelftools-native \
    python3-rich-native \
    clang-native \
"

do_install:append() {
    install -d ${D}${libdir}/tfm-scripts
    install -m 0644 ${S}/bl2/ext/mcuboot/*.pem ${D}${libdir}/tfm-scripts/

    # TF-M applies additional patches to its bundled MCUboot imgtool. Install
    # that copy privately so the signing wrapper can emit TF-M-specific TLVs.
    install -d ${D}${libdir}/tfm-scripts/imgtool/keys
    install -m 0644 ${S}/external/mcuboot/scripts/imgtool/*.py \
        ${D}${libdir}/tfm-scripts/imgtool/
    install -m 0644 ${S}/external/mcuboot/scripts/imgtool/keys/*.py \
        ${D}${libdir}/tfm-scripts/imgtool/keys/
}

FILES:${PN} += "${libdir}/tfm-scripts"
