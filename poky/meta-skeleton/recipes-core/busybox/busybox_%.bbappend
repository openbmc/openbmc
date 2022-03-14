# Example use of configuration fragments for busybox, which uses the same
# mechanism as the linux-yocto kernel recipe.
#
# The entries here will override any entries in the base busybox recipe
# when DISTRO = "mydistro" is defined in your conf/local.conf file.
#
# More details can be found in the Kernel Dev Manual
# http://www.yoctoproject.org/docs/current/kernel-dev/kernel-dev.html#changing-the-configuration
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:mydistro = " \
	    file://no_rfkill.cfg \
           "
