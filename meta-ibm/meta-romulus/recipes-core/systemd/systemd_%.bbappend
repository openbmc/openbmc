FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Romulus doesn't have the space for the both zstd and xz compression
# libraries and currently phosphor-debug-collector is using xz.  Switch systemd
# to use xz so only one of the two is added into the image.
PACKAGECONFIG:remove:romulus = "zstd"
PACKAGECONFIG:append:romulus = " xz"

# Remove seccomp support for Romulus to save space.  This isn't actively
# leveraged.
PACKAGECONFIG:remove:romulus = "seccomp"

# Nothing on the BMC calls org.freedesktop.timedate1 (time management
# goes through phosphor-network and timesyncd); drop the daemon,
# timedatectl, and units.
PACKAGECONFIG:remove:romulus = "timedated"

# devlink manages switch-ASIC port params, unneeded on AST2500 BMC NICs.
# systemd 261.3 added it as a hard udev dep; drop it to save ~200KB.
RDEPENDS:udev:remove:romulus = "iproute2-devlink"

# systemd-resolved runs a caching stub resolver the BMC never uses: DNS is
# written directly to /run/systemd/resolve/resolv.conf by phosphor-network
# (the recipe provisions that fallback path when resolved is disabled) and
# nsswitch has no 'resolve' entry. Drop it to save ~600KB (~240KB compressed).
# NOTE: the NameServers D-Bus/Redfish property reads from resolve1 and will
# report empty after this; verify DNS + NameServers on boot.
PACKAGECONFIG:remove:romulus = "resolved"
