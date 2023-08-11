#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.37"
UNINATIVE_VERSION = "4.1"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/${UNINATIVE_VERSION}/"
UNINATIVE_CHECKSUM[aarch64] ?= "b6ff9171aa7d3828bc81197822e804725908856bbd488bf412121cc0deddcb60"
UNINATIVE_CHECKSUM[i686] ?= "6354fd2e09af1f111bad5e34ce7af4f9ad7cd266188af7eeceaeb982afd5354b"
UNINATIVE_CHECKSUM[x86_64] ?= "f83eca543170adfd2432b135ca655922a4303622d73cc4b13e92b973cdf49e3a"
