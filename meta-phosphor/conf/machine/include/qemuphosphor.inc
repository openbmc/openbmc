# The qemu helper scripts don't work with
# cpio images.

# Prefer linux-yocto with qemu.
PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"
