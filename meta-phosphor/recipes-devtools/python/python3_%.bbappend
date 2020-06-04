inherit update-alternatives

ALTERNATIVE_${PN}-core += "python"
ALTERNATIVE_LINK_NAME[python] = "${bindir}/python"
ALTERNATIVE_TARGET[python] = "${bindir}/python3"

# python3 takes up a lot of space that most embedded systems
# do not have, so remove some un-needed files from the rootfs
do_install_append_class-target() {
    # Even though python3 is built with --without-ensurepip, it still installs
    # a large, compressed version of pip.  Remove it to free up the space.
    rm -rf ${D}${libdir}/python${PYTHON_MAJMIN}/ensurepip
}
