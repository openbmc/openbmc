FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://yv5-cm-debug-dump"

do_install:append() {
    install -m 0755 ${UNPACKDIR}/yv5-cm-debug-dump ${S}/tools/dreport.d/plugins.d/
}
