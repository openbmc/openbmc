inherit update-rc.d

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Add startup script calling m5 readfile for automatic checkpoint and restore
SRC_URI += "file://m5-readfile.sh"

INITSCRIPT_NAME = "m5-readfile.sh"
INITSCRIPT_PARAMS = "defaults 99"

do_install:append() {
    install -d ${D}/${INIT_D_DIR}
    install -m 755 ${WORKDIR}/m5-readfile.sh ${D}/${INIT_D_DIR}
}
