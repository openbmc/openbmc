#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.29"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/2.6/"
UNINATIVE_CHECKSUM[aarch64] ?= "a37118fc8b423f48146120707b81dd15017512c3e8ef9e6ca2cb3a033f4f4046"
UNINATIVE_CHECKSUM[i686] ?= "3234fc3ded810225071f23a0e9a99f4f8c2480059945a848eff076ce78122ade"
UNINATIVE_CHECKSUM[x86_64] ?= "133387753a9acf3e1b788103c59fac91e968e2ee331d7a4b9498e926ada7be57"
