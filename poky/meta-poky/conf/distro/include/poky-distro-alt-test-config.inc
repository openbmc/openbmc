# Add an extra DISTRO_FEATURE
DISTRO_FEATURES:append = " pam"

# Use the LTSI Kernel
PREFERRED_VERSION_linux-yocto = "5.10%"

# Ensure the kernel nfs server is enabled
KERNEL_FEATURES:append:pn-linux-yocto = " features/nfsd/nfsd-enable.scc"
