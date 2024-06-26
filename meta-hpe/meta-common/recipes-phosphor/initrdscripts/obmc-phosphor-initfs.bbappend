FILESEXTRAPATHS:prepend :="${THISDIR}/files:"

SRC_URI += "file://gxp-obmc-init.sh \
           "

do_install:append() {
        install -m 0755 ${UNPACKDIR}/gxp-obmc-init.sh ${D}/init
}

FILES:${PN} += " /init /shutdown /update /whitelist /dev "
FILES:${PN} += " /init-options /init-download-url "

