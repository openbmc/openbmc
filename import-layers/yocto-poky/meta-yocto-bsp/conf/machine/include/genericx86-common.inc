include conf/machine/include/x86-base.inc
MACHINE_FEATURES += "wifi efi pcbios"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"
PREFERRED_VERSION_linux-yocto ?= "4.4%"
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
XSERVER ?= "${XSERVER_X86_BASE} \
            ${XSERVER_X86_EXT} \
            ${XSERVER_X86_I915} \
            ${XSERVER_X86_I965} \
            ${XSERVER_X86_FBDEV} \
            ${XSERVER_X86_VESA} \
            ${XSERVER_X86_MODESETTING} \
           "

MACHINE_EXTRA_RRECOMMENDS += "linux-firmware eee-acpi-scripts"

GLIBC_ADDONS = "nptl"

EXTRA_OECONF_append_pn-matchbox-panel-2 = " --with-battery=acpi"
