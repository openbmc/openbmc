HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "8c5d4fd0ec222701598a27b26ab7265d4cee45a3"

SRC_URI = "git://git.denx.de/u-boot.git \
           file://CVE-2018-1000205-1.patch \
           file://CVE-2018-1000205-2.patch \
"

S = "${WORKDIR}/git"
