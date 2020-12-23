FILESEXTRAPATHS_prepend :="${THISDIR}/files:"

SRC_URI += "file://gxp-obmc-init.sh \
           "

do_install_append() {
        install -m 0755 ${WORKDIR}/gxp-obmc-init.sh ${D}/init
}

FILES_${PN} += " /init /shutdown /update /whitelist /dev "
FILES_${PN} += " /init-options /init-download-url "
