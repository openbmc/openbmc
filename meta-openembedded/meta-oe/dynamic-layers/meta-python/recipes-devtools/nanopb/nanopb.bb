SUMMARY = "Combined nanopb package"
PV = "1.0"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

DEPENDS = " \
    nanopb-generator \
    nanopb-runtime \
"

RDEPENDS:${PN} = " \
    nanopb-generator \
    nanopb-runtime \
"
