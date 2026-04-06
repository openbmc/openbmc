# Core audit infrastructure for OpenBMC
# Enable for kernel audit and rules generation

DISTRO_FEATURES += "linux-audit"
DISTROOVERRIDES .= ":df-linux-audit"

# Install audit userspace utilities (auditd, ausearch, aureport, auditctl, etc.)
# Note: auditd is package being installed
#   - 'audit' = base libraries only
#   - 'auditd' = daemon and CLI tools (ausearch, aureport, auditctl, etc.)
IMAGE_INSTALL:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'linux-audit', 'auditd', '', d)}"
