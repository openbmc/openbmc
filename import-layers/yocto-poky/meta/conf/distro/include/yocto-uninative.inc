#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/1.7/"
UNINATIVE_CHECKSUM[i686] ?= "d7c341460035936c19d63fe02f354ef1bc993c62d694ae3a31458d1c6997f0c5"
UNINATIVE_CHECKSUM[x86_64] ?= "ed033c868b87852b07957a4400f3b744c00aef5d6470346ea1a59b6d3e03075e"
