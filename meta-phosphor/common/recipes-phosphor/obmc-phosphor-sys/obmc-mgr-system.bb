SUMMARY = "OpenBMC system manager"
DESCRIPTION = "OpenBMC system manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-systemd

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-subprocess \
        python-pygobject \
        pyphosphor \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pysystemmgr"

do_compile_append() {
	oe_runmake -C ../hacks
}

do_install_append() {
	oe_runmake -C ../hacks install DESTDIR=${D}
}
