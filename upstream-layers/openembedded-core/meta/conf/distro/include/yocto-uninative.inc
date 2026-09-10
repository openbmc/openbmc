#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.44"
UNINATIVE_VERSION = "5.2"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/${UNINATIVE_VERSION}/"
UNINATIVE_CHECKSUM[aarch64] ?= "8cf46c7ba75ebb1863fd6b74cf7d7ab910a3e99de4f01b1e9490babf8e6b9e05"
UNINATIVE_CHECKSUM[x86_64] ?= "1a2749c1379fd9f60ea3c82f1bf4844924d5a99117b74a70c57036e94e2379d4"
