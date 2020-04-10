# python-mako installs /usr/bin/mako-render for both python2 and python3,
# which causes a bitbake QA failure.  Remove it from installation for the
# native target to avoid the collision.
#
# We don't currently use this as a target package and if we did, we shouldn't
# install both python2 and python3 variants.
#
# Once we are done with python2, we can delete this.

do_install_append_class-native() {
    rm ${D}${bindir}/mako-render
}
