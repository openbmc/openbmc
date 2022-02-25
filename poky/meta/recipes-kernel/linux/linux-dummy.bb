SUMMARY = "Dummy Linux kernel"
DESCRIPTION = "Dummy Linux kernel, to be selected as the preferred \
provider for virtual/kernel to satisfy dependencies for situations \
where you wish to build the kernel externally from the build system."
SECTION = "kernel"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

PROVIDES += "virtual/kernel"

inherit deploy linux-dummy

PACKAGES_DYNAMIC += "^kernel-module-.*"
PACKAGES_DYNAMIC += "^kernel-image-.*"
PACKAGES_DYNAMIC += "^kernel-firmware-.*"

PACKAGES += "kernel-modules kernel-vmlinux"
FILES:kernel-modules = ""
ALLOW_EMPTY:kernel-modules = "1"
DESCRIPTION:kernel-modules = "Kernel modules meta package"
FILES:kernel-vmlinux = ""
ALLOW_EMPTY:kernel-vmlinux = "1"
DESCRIPTION:kernel-vmlinux = "Kernel vmlinux meta package"


INHIBIT_DEFAULT_DEPS = "1"

COMPATIBLE_HOST = ".*-linux"

PR = "r1"

SRC_URI = "file://COPYING.GPL"
S = "${WORKDIR}"

do_configure() {
	:
}

do_compile () {
	:
}

do_compile_kernelmodules() {
    :
}

do_shared_workdir () {
	:
}

do_install() {
	:
}

do_bundle_initramfs() {
	:
}

do_deploy() {
	:
}

addtask bundle_initramfs after do_install before do_deploy
addtask deploy after do_install
addtask shared_workdir after do_compile before do_install
addtask compile_kernelmodules
