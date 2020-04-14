inherit update-alternatives

ALTERNATIVE_${PN}-core += "python"
ALTERNATIVE_LINK_NAME[python] = "${bindir}/python"
ALTERNATIVE_TARGET[python] = "${bindir}/python3"

# Even though python3 is built with --without-ensurepip, it still installs
# a large, compressed version of pip.  Remove it to free up the space.
do_install_append_class-target() {
    rm -rf ${D}${libdir}/python${PYTHON_MAJMIN}/ensurepip
}
