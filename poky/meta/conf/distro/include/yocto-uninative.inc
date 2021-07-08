#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.33"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/3.2/"
UNINATIVE_CHECKSUM[aarch64] ?= "4f0872cdca2775b637a8a99815ca5c8dd42146abe903a24a50ee0448358c764b"
UNINATIVE_CHECKSUM[i686] ?= "e2eeab92e67263db37d9bb6d4c58579abd1f47ff4cded3171bde572fece124b2"
UNINATIVE_CHECKSUM[x86_64] ?= "3ee8c7d55e2d4c7ae3887cddb97219f97b94efddfeee2e24923c0cb0e8ce84c6"
