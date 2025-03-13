FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Palmetto doesn't have the space for the both zstd and xz compression
# libraries and currently phosphor-debug-collector is using xz.  Switch systemd
# to use xz so only one of the two is added into the image.
PACKAGECONFIG:remove:palmetto = "zstd"
PACKAGECONFIG:append:palmetto = " xz"

# Remove seccomp support for Palmetto to save space.  This isn't actively
# leveraged.
PACKAGECONFIG:remove:palmetto = "seccomp"
