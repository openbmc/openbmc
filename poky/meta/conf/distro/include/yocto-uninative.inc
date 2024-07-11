#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.39"
UNINATIVE_VERSION = "4.5"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/${UNINATIVE_VERSION}/"
UNINATIVE_CHECKSUM[aarch64] ?= "df2e29e2e6feb187a3499abf3b1322a3b251da819c77a7b19d4fe952351365ab"
UNINATIVE_CHECKSUM[i686] ?= "8ef3eda53428b484c20157f6ec3c130b03080b3d4b3889067e0e184e05102d35"
UNINATIVE_CHECKSUM[x86_64] ?= "43ee6a25bcf5fce16ea87076d6a96e79ead6ced90690a058d07432f902773473"
