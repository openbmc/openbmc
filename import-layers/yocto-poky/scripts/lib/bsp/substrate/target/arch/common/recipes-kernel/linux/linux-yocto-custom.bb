# yocto-bsp-filename {{ if kernel_choice == "custom": }} this
# This file was derived from the linux-yocto-custom.bb recipe in
# oe-core.
#
# linux-yocto-custom.bb:
#
#   A yocto-bsp-generated kernel recipe that uses the linux-yocto and
#   oe-core kernel classes to apply a subset of yocto kernel
#   management to git managed kernel repositories.
#
# Warning:
#
#   Building this kernel without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
# Notes:
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition:
#            SRC_URI += "file://0001-linux-version-tweak.patch
#   example feature addition:
#            SRC_URI += "file://feature.scc"
#

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

{{ if kernel_choice == "custom" and custom_kernel_remote == "y": }}
SRC_URI = "{{=custom_kernel_remote_path}};protocol=git;bareclone=1;branch=${KBRANCH}"
{{ if kernel_choice == "custom" and custom_kernel_remote == "n": }}
SRC_URI = "git://{{=custom_kernel_local_path}};protocol=file;bareclone=1;branch=${KBRANCH}"

SRC_URI += "file://defconfig"

SRC_URI += "file://{{=machine}}.scc \
            file://{{=machine}}.cfg \
            file://{{=machine}}-user-config.cfg \
            file://{{=machine}}-user-patches.scc \
            file://{{=machine}}-user-features.scc \
           "

{{ if kernel_choice == "custom" and custom_kernel_need_kbranch == "y" and custom_kernel_kbranch and custom_kernel_kbranch != "master": }}
KBRANCH = "{{=custom_kernel_kbranch}}"

LINUX_VERSION ?= "{{=custom_kernel_linux_version}}"
LINUX_VERSION_EXTENSION ?= "{{=custom_kernel_linux_version_extension}}"

SRCREV="{{=custom_kernel_srcrev}}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_{{=machine}} = "{{=machine}}"
