require gem5-source_20.inc

SUMMARY = "m5term allows users to connect to gem5's simulated console"
HOMEPAGE = "https://www.gem5.org/documentation/general_docs/fullsystem/m5term"
LICENSE = "BSD-3-Clause"

inherit native

M5TERM_DIR = "util/term"

SRC_URI += "file://0001-add-makefile-flags.patch"

do_compile() {
    oe_runmake -C ${S}/${M5TERM_DIR}
}

# The Makefile for m5term does not provide a "install" target
# We do the install process within the recipe
do_install() {
    install -d ${D}${bindir}
    install -m 755 ${B}/${M5TERM_DIR}/m5term ${D}${bindir}
}

addtask addto_recipe_sysroot before do_build
