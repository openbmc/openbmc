FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://ventura-rmc-cpld-dump"

do_install:append() {
    install -m 0755 ${UNPACKDIR}/ventura-rmc-cpld-dump ${S}/tools/dreport.d/plugins.d/
}
