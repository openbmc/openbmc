#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.37"
UNINATIVE_VERSION = "3.9"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/${UNINATIVE_VERSION}/"
UNINATIVE_CHECKSUM[aarch64] ?= "de35708c95c34573af140da910132c3291ba4fd26ebf7b74b755ada432cdf07b"
UNINATIVE_CHECKSUM[i686] ?= "adac07b08adb88eb26fc7fd87fee0cec9d5be167bf7c5ffd3a549a2a6699c29c"
UNINATIVE_CHECKSUM[x86_64] ?= "3dd82c3fbdb59e87bf091c3eef555a05fae528eeda3083828f76cd4deaceca8b"
