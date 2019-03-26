#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.29"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/2.4/"
UNINATIVE_CHECKSUM[aarch64] ?= "af2e2faf6cf00ff45cc1bcd5e3fb00cee7f79b3ec7c3be15917ad4ff8c154cfe"
UNINATIVE_CHECKSUM[i686] ?= "fafacfc537a6ce2bd122bd16c146881ab5ac69bd575abf6cb68a0dd33fa70ea2"
UNINATIVE_CHECKSUM[x86_64] ?= "06f91685b782f2ccfedf3070b3ba0fe4a5ba2f0766dad5c9d1642dccf95accd0"
