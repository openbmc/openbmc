require recipes-bsp/trusted-firmware-m/trusted-firmware-m-${PV}-src.inc

inherit native python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-pyelftools-native \
    python3-rich-native \
    clang-native \
"
