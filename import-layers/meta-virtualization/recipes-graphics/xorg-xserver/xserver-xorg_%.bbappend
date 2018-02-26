# Change the default Xserver OpenGL configuration for non-x86 distros with Xen: deselect 'glamor'.
# This removes the dependency on libegl to simplify the domU build.
#
# To override this (eg. if wanted for dom0 images) define:
#   REMOVED_OPENGL_PKGCONFIGS = ""

XEN_REMOVED_OPENGL_PKGCONFIGS ?= "glamor"
XEN_REMOVED_OPENGL_PKGCONFIGS_x86 = ""
XEN_REMOVED_OPENGL_PKGCONFIGS_x86-64 = ""

REMOVED_OPENGL_PKGCONFIGS ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xen', "${XEN_REMOVED_OPENGL_PKGCONFIGS}", '', d)}"

OPENGL_PKGCONFIGS_remove = "${REMOVED_OPENGL_PKGCONFIGS}"
