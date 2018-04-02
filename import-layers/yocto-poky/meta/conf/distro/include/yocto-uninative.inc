#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.27"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/1.8/"
UNINATIVE_CHECKSUM[i686] ?= "427ce522ec97f65c75fd89587d90ef789e8cbca4a617abc4b5822abb01c2d0ae"
UNINATIVE_CHECKSUM[x86_64] ?= "de4947e98e09e1432d069311cc2093974ecb9138a714fd5466f73524de66a693"
