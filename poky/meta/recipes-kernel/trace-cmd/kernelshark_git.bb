SUMMARY = "Graphical trace viewer for Ftrace"
LICENSE = "GPLv2"

require trace-cmd.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://kernel-shark.c;beginline=6;endline=8;md5=2c22c965a649ddd7973d7913c5634a5e"

DEPENDS = "gtk+ libxml2"
RDEPENDS_${PN} = "trace-cmd"

inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

EXTRA_OEMAKE = "\
    'prefix=${prefix}' \
    'bindir_relative=${@oe.path.relative(prefix, bindir)}' \
    'libdir=${libdir}' \
    NO_PYTHON=1 \
    gui \
"
do_configure_prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.*.d
}

do_install() {
    oe_runmake DESTDIR="${D}" install_gui
    rm ${D}${bindir}/trace-cmd
    rm -rf ${D}${libdir}/trace-cmd
    rm -rf ${D}${sysconfdir}/bash_completion.d/trace-cmd.bash
    rmdir ${D}${libdir}
}
