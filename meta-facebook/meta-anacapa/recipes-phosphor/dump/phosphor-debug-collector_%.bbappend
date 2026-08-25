FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = "\
    file://amc-dreport \
"

do_install:append() {
    # collect amc's dreport
    install -m 0755 ${UNPACKDIR}/amc-dreport ${S}/tools/dreport.d/plugins.d/
}
