SUMMARY = "Provides an icon to shut down the system cleanly"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://shutdown.desktop"

PR = "r1"

S = "${WORKDIR}"

do_install() {
	install -d ${D}${datadir}/applications
	install -m 0644 shutdown.desktop ${D}${datadir}/applications/

	sed -i ${D}${datadir}/applications/shutdown.desktop -e 's#^Exec=\(.*\)#Exec=${base_sbindir}/\1#'
}

pkg_postinst_${PN} () {
    grep -q qemuarm $D${sysconfdir}/hostname && \
        sed -i $D${datadir}/applications/shutdown.desktop -e 's#^Exec=\(.*\)/halt#Exec=\1/reboot#' \
        || true
}

inherit allarch
