require recipes-devtools/atp/atp-source_3.1.inc
inherit package

SUMMARY = "End-to-end tests evaluating ATP kernel modules service correctness"
SECTION = "kernel/userland"

S = "${WORKDIR}/git"
SRC_URI = "${ATP_SRC}"

EXTRA_OEMAKE += "-C linux/test"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} PREFIX=${prefix} install
}

DEPENDS = "atp-uapi cppunit"
RDEPENDS:${PN} = "atp-uapi"
