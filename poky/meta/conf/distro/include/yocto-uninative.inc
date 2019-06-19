#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.29"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/2.5/"
UNINATIVE_CHECKSUM[aarch64] ?= "ca977ff95c77f983570141908d451ff7d78add2864471605af404302bb36a1fa"
UNINATIVE_CHECKSUM[i686] ?= "7b5822891c293795faf8a4a80586b36f8cde405387524916a24f9055ea82f7ca"
UNINATIVE_CHECKSUM[x86_64] ?= "ed0ac07c710b711925cb976685dd855fb1d442dd840d00194751c18bf480c4ed"
