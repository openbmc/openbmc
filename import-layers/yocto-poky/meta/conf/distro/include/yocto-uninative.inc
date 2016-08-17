#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_URL = "http://downloads.yoctoproject.org/releases/uninative/0.95/"
UNINATIVE_CHECKSUM[i686] ?= "5f27d7e0f4dd2ed80a7ff6a0d88af107b08e00765b31ed3aa180cc5ce15b0811"
UNINATIVE_CHECKSUM[x86_64] ?= "26d46c61ad88cc245e31c88549717c0092a838d111b93ec169d88b08cc027581"
