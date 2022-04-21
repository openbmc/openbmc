#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.35"
UNINATIVE_VERSION = "3.5"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/${UNINATIVE_VERSION}/"
UNINATIVE_CHECKSUM[aarch64] ?= "6de0771bd21e0fcb5e80388e5b561a8023b24083bcbf46e056a089982aff75d7"
UNINATIVE_CHECKSUM[i686] ?= "8c8745becbfa1c341bae839c7eab56ddf17ce36c303bcd73d3b2f2f788b631c2"
UNINATIVE_CHECKSUM[x86_64] ?= "e8047a5748e6f266165da141eb6d08b23674f30e477b0e5505b6403d50fbc4b2"
