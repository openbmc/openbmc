FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:evk-axiado = " file://axiado-evk-mt2.dts"
SRC_URI:append:evk-axiado = " file://axiado-evk-sb2.dts"
SRC_URI:append:evk-axiado-github = " file://ax3000-scm3003-evk-axiado.dts"
SRC_URI:append:evk-axiado-github = " file://ax3000-scm3002-evk-axiado.dts"
SRC_URI:append:evk-axiado-github = " file://ax3080-evk-axiado.dts"
SRC_URI:append:evk-axiado-github = " file://dc-scm.cfg"
SRC_URI:append:evk-axiado-github = " file://dc-scm.scc"
SRC_URI:append:evk-axiado-github = " file://usb-storage.cfg"
SRC_URI:append:evk-axiado-github = " file://usb-storage.scc"

do_configure:append:evk-axiado() {
    cp ${UNPACKDIR}/axiado-evk-mt2.dts ${S}/arch/arm64/boot/dts/axiado/
    cp ${UNPACKDIR}/axiado-evk-sb2.dts ${S}/arch/arm64/boot/dts/axiado/
}

do_configure:append:evk-axiado-github() {
    cp ${UNPACKDIR}/ax3000-scm3003-evk-axiado.dts ${S}/arch/arm64/boot/dts/axiado/
    cp ${UNPACKDIR}/ax3000-scm3002-evk-axiado.dts ${S}/arch/arm64/boot/dts/axiado/
    cp ${UNPACKDIR}/ax3080-evk-axiado.dts ${S}/arch/arm64/boot/dts/axiado/
}
