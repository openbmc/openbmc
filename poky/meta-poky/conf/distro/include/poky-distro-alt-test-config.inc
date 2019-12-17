# Add an extra DISTRO_FEATURE
DISTRO_FEATURES_append = " pam"

# Use the LTSI Kernel
PREFERRED_VERSION_linux-yocto ?= "4.19%"

# Ensure the kernel nfs server is enabled
KERNEL_FEATURES_append_pn-linux-yocto = " features/nfsd/nfsd-enable.scc"
