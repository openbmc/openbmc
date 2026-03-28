#
# This include points at uninative tarballs generated and maintained
# by the Yocto Project. These file can be included by distros if they
# want to use/maintain a unified sstate feed for native/cross artefacts
# rather than the feeds having native/cross artefacts which are specific
# to the distro running on the build machine.
#

UNINATIVE_MAXGLIBCVERSION = "2.43"
UNINATIVE_VERSION = "5.1"

UNINATIVE_URL ?= "http://downloads.yoctoproject.org/releases/uninative/${UNINATIVE_VERSION}/"
UNINATIVE_CHECKSUM[aarch64] ?= "4166237a9dabd222dcb9627a9435dffd756764fabf76ed7ef2e93dc2964567ad"
UNINATIVE_CHECKSUM[i686] ?= "761502cc9aef4d54d0c6fe9418beb9fdd2c6220da6f2b04128c89f47902ab9ae"
UNINATIVE_CHECKSUM[x86_64] ?= "2b63a078c26535e0786e87f81ae69509df30f4dce40693004c527bd5e4ab2b85"
