FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://${MACHINE}.cfg \
"

# Merge source tree by original project with our layer of additional files
do_kernel_checkout_append_mtjade () {
    cp -r "${WORKDIR}/arch" "${STAGING_KERNEL_DIR}"
}
