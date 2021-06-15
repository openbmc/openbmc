# Add an extra DISTRO_FEATURE
DISTRO_FEATURES_append = " pam"

# Use the LTSI Kernel
PREFERRED_VERSION_linux-yocto = "5.4%"

# Ensure the kernel nfs server is enabled
KERNEL_FEATURES_append_pn-linux-yocto = " features/nfsd/nfsd-enable.scc"
