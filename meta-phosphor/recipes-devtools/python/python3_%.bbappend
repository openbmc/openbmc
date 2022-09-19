inherit update-alternatives

# python3 takes up a lot of space that most embedded systems
# do not have, so remove some un-needed files from the rootfs
do_install:append:class-target() {
    # Even though python3 is built with --without-ensurepip, it still installs
    # a large, compressed version of pip.  Remove it to free up the space.
    rm -rf ${D}${libdir}/python${PYTHON_MAJMIN}/ensurepip
}

ALTERNATIVE_LINK_NAME[python] = "${bindir}/python"

ALTERNATIVE:${PN}-core += "python"
ALTERNATIVE_TARGET[python] = "${bindir}/python3"
