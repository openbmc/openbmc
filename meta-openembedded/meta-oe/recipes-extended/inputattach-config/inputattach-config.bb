SUMMARY = "inputattach configuration file"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://inputattach.conf"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -Dm 0644 ${UNPACKDIR}/inputattach.conf ${D}${sysconfdir}/inputattach.conf
}
