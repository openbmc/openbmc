SUMMARY = "Polish dictionary for Illume keyboard"
SECTION = "x11/data"
SRCREV = "38fdd9bb0d8296e984bb6443466801eea6f62f00"
PV = "1.0+gitr${SRCPV}"
PE = "1"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f523ab5986cc79b52a90d2ac3d5454a2"

SRC_URI = "git://git.shr-project.org/repo/shr-themes.git;protocol=http;branch=master"

S = "${WORKDIR}/git/e-wm/${PN}"

FILES_${PN} = "${libdir}/enlightenment/modules/illume/dicts/Polish.dic"

do_install() {
    install -d ${D}${libdir}/enlightenment/modules/illume/dicts
    install -m 0644 ${S}/Polish.dic        ${D}${libdir}/enlightenment/modules/illume/dicts/Polish.dic
}
