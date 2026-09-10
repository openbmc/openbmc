HOMEPAGE = "https://u-boot-project.org"
DESCRIPTION = "U-Boot, a boot loader for Embedded boards based on PowerPC, \
ARM, MIPS and several other processors, which can be installed in a boot \
ROM and used to initialize and test the hardware or to download and run \
application code."
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native python3-setuptools-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"
PE = "1"

CVE_PRODUCT = "u-boot:u-boot denx:u-boot"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "ece349ade2973e220f524ce59e59711cc919263f"

SRC_URI = "git://git.u-boot-project.org/u-boot/u-boot.git;protocol=https;branch=main;tag=v${PV} \
           file://0001-pylibfdt-Replace-removed-SWIG-Python-2-compatibility.patch \
           file://0001-Add-support-for-OpenSSL-Provider-API.patch \
           "

B = "${WORKDIR}/build"

inherit pkgconfig

do_configure[cleandirs] = "${B}"
