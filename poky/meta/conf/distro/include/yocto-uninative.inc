#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.27"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/2.2/"
UNINATIVE_CHECKSUM[i686] ?= "036b60092fe4acfa1a321d110673030db20344a2d56f33a4d047f0279498bdad"
UNINATIVE_CHECKSUM[x86_64] ?= "e3b77208169bf1ac4e89496f3cdbf27695f5b18a2694a908a793390f28b67f83"

