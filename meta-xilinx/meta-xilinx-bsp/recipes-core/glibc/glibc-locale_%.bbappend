do_install_append() {
    # Remove the libdir if it is empty when gconv is not copied
    find ${D}${libdir} -type d -empty -delete
}
