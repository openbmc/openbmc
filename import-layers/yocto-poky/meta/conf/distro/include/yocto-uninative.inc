#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.27"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/1.9/"
UNINATIVE_CHECKSUM[i686] ?= "83a4f927da81d9889ef0cbe5c12cb782e21c6cc11e6155600b94ff0c99576dce"
UNINATIVE_CHECKSUM[x86_64] ?= "c26622a1f27dbf5b25de986b11584b5c5b2f322d9eb367f705a744f58a5561ec"
