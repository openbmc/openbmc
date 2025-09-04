# Add post code event log configurations
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:fb-compute-amd = " \
    file://amd-post-code-handlers.json \
"

do_install:append:fb-compute-amd() {
    install -d ${D}${datadir}/phosphor-post-code-manager
    install -m 0644 ${UNPACKDIR}/amd-post-code-handlers.json \
        ${D}${datadir}/phosphor-post-code-manager/post-code-handlers.json
}
