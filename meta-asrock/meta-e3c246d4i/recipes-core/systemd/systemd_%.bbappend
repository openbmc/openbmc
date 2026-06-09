FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# e3c246d4i doesn't have the space for the both zstd and xz compression
# libraries and currently phosphor-debug-collector is using xz.  Switch systemd
# to use xz so only one of the two is added into the image.
PACKAGECONFIG:remove:e3c246d4i = "zstd"
PACKAGECONFIG:append:e3c246d4i = " xz"

# Remove seccomp support for e3c246d4i to save space.  This isn't actively
# leveraged.
PACKAGECONFIG:remove:e3c246d4i = "seccomp"
