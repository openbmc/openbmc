HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "f3dd87e0b98999a78e500e8c6d2b063ebadf535a"

SRC_URI = "git://git.denx.de/u-boot.git \
    file://MPC8315ERDB-enable-DHCP.patch \
    file://0001-efi_loader-avoid-make-race-condition.patch \
"

S = "${WORKDIR}/git"
