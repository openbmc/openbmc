FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://ventura2-rmc-cpld-dump"

do_install:append() {
    install -m 0755 ${UNPACKDIR}/ventura2-rmc-cpld-dump ${S}/tools/dreport.d/plugins.d/
}
