# Add extra DISTRO_FEATUREs
DISTRO_FEATURES:append = " pam usrmerge"

# Use our alternate kernel version
PREFERRED_VERSION_linux-yocto = "6.6%"

# Ensure the kernel nfs server is enabled
KERNEL_FEATURES:append:pn-linux-yocto = " features/nfsd/nfsd-enable.scc"
