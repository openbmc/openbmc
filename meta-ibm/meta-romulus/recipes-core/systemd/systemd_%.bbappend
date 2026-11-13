FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Romulus doesn't have the space for the both zstd and xz compression
# libraries and currently phosphor-debug-collector is using xz.  Switch systemd
# to use xz so only one of the two is added into the image.
PACKAGECONFIG:remove:romulus = "zstd"
PACKAGECONFIG:append:romulus = " xz"

# Remove seccomp support for Romulus to save space.  This isn't actively
# leveraged.
PACKAGECONFIG:remove:romulus = "seccomp"

# devlink manages switch-ASIC port params, unneeded on AST2500 BMC NICs.
# systemd 261.3 added it as a hard udev dep; drop it to save ~200KB.
RDEPENDS:udev:remove:romulus = "iproute2-devlink"
